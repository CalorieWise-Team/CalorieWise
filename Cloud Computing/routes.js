const express = require('express');
const bcrypt = require('bcrypt');
const jwt = require('jsonwebtoken');
const path = require('path');
const uuid = require('uuid');
const router = express.Router();
const { db, config } = require('./db');
const { storageClient, bucketName, upload, uploadSingle, uploadMultiple, handleMulterError } = require('./cloudstorage');

router.use(express.json());
const defaultImageUrl = 'https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/profile/default.jpg';

const verifyToken = async (req, res, next) => {
    const authHeader = req.headers.authorization;
    if (!authHeader || !authHeader.startsWith('Bearer ')) {
        return res.status(401).json({ message: 'Unauthorized' });
    }

    const token = authHeader.split(' ')[1];

    try {
        const decoded = await jwt.verify(token, config.secret);

        const sql = 'SELECT * FROM users WHERE user_id = ?';
        const userId = decoded.userId;

        db.query(sql, [userId], (err, user) => {
            if (err) throw err;
            if (!user.length) {
                return res.status(403).json({ status: 403, message: 'Forbidden' });
            }
            req.user = user[0];
            next();
        });
    } catch (err) {
        res.status(403).json({ message: 'Forbidden' });
    }
};
router.put('/update/profile', verifyToken, (req, res, next) => {
    uploadSingle('myImage')(req, res, function (err) {
        if (err) {
            return handleMulterError(err, req, res, next);
        }

        const userId = req.user.user_id; 
        const { name, gender, email, password } = req.body; 

        let imageUrl;

        async function deletePreviousImage(previousImageUrl) {
            const previousFileName = previousImageUrl.split('/').pop();
            const previousFilePath = `images/profile/${previousFileName}`;

            try {
                await storageClient.bucket(bucketName).file(previousFilePath).delete();
                console.log('Success delete previous image ' + previousImageUrl);
            } catch (err) {
                if (err.code === 404) {
                    console.log('Previous image not found in Google Cloud Storage. Skipping :', previousImageUrl);
                } else {
                    console.error('Error deleting previous image from Google Cloud Storage:', err);
                }
            }
        }

        async function uploadNewImage(file) {
            const fileName = `${uuid.v4()}${path.extname(file.originalname)}`;
            const fileUpload = storageClient.bucket(bucketName).file(`images/profile/${fileName}`);
            await fileUpload.save(file.buffer);
            return `https://storage.googleapis.com/${bucketName}/images/profile/${fileName}`;
        }

        async function checkEmail(email) {
            return new Promise((resolve, reject) => {
                const emailCheckQuery = 'SELECT user_id FROM users WHERE email = ?';
                db.query(emailCheckQuery, [email], (err, results) => {
                    if (err) return reject(err);
                    if (results.length > 0 && results[0].user_id !== userId) {
                        return resolve(false); 
                    }
                    return resolve(true); 
                });
            });
        }

        async function updateProfile(hashedPassword) {
            return new Promise((resolve, reject) => {
                let updateProfileQuery = 'UPDATE users SET';
                let queryParams = [];
                let isFirstField = true;

                if (name) {
                    updateProfileQuery += ` ${isFirstField ? '' : ','} name = ?`;
                    queryParams.push(name);
                    isFirstField = false;
                }

                if (gender) {
                    updateProfileQuery += ` ${isFirstField ? '' : ','} gender = ?`;
                    queryParams.push(gender);
                    isFirstField = false;
                }

                if (email) {
                    updateProfileQuery += ` ${isFirstField ? '' : ','} email = ?`;
                    queryParams.push(email);
                    isFirstField = false;
                }

                if (imageUrl) {
                    updateProfileQuery += ` ${isFirstField ? '' : ','} image_url = ?`;
                    queryParams.push(imageUrl);
                    isFirstField = false;
                }

                if (hashedPassword) {
                    updateProfileQuery += ` ${isFirstField ? '' : ','} password = ?`;
                    queryParams.push(hashedPassword);
                    isFirstField = false;
                }

                updateProfileQuery += ' WHERE user_id = ?';
                queryParams.push(userId);

                db.query(updateProfileQuery, queryParams, (err, result) => {
                    if (err) return reject(err);
                    if (result.affectedRows === 0) {
                        return reject(new Error('Profile not found or not authorized'));
                    }
                    return resolve();
                });
            });
        }

        (async () => {
            try {

                if (email) {
                    const isEmailValid = await checkEmail(email);
                    if (!isEmailValid) {
                        return res.status(409).json({ error: true, message: 'Email already in use' });
                    }
                }

                let hashedPassword;
                if (password) {
                    if (password.length < 8) {
                        return res.status(400).json({ error: true, message: 'Password must be at least 8 characters long' });
                    }
                    hashedPassword = await bcrypt.hash(password, 10);
                }

                if (req.file) {
                    const selectQuery = 'SELECT image_url FROM users WHERE user_id = ?';
                    db.query(selectQuery, [userId], async (err, rows) => {
                        if (err) {
                            console.error(err);
                            return res.status(500).json({ error: true, message: 'Error retrieving previous image URL' });
                        }

                        if (rows.length > 0 && rows[0].image_url && rows[0].image_url !== defaultImageUrl) {
                            await deletePreviousImage(rows[0].image_url);
                        }
                        imageUrl = await uploadNewImage(req.file);
                        await proceedUpdate(hashedPassword);
                    });
                } else {
                    await proceedUpdate(hashedPassword);
                }

                async function proceedUpdate(hashedPassword) {
                    await updateProfile(hashedPassword);

                    res.status(200).json({
                        status: 200,
                        error: false,
                        message: 'Profile updated successfully',
                        data: {
                            user_id: userId,
                            name: name,
                            gender: gender,
                            email: email,
                            image_url: imageUrl
                        }
                    });
                }

            } catch (err) {
                console.error(err);
                res.status(500).json({ error: true, message: 'Internal Server Error' });
            }
        })();
    });
});
router.post('/register', uploadSingle('myImage'), handleMulterError, async (req, res) => {
    try {
        const { email, password, name, gender } = req.body;

        if (!email || !password || !name || !gender) {
            return res.status(400).json({ status: 400, error: true, message: 'Missing required fields. Email, Password, Name, Gender are Required!! ' });
        }

        if (password.length < 8) {
            return res.status(400).json({ status: 400, error: true, message: 'Password must be at least 8 characters long' });
        }

        const checkUserQuery = 'SELECT * FROM users WHERE email = ?';
        db.query(checkUserQuery, [email], async (err, result) => {
            if (err) throw err;

            if (result.length > 0) {
                return res.status(409).json({ status: 409, error: true, message: 'Email already exists' });
            }

            const hashedPassword = await bcrypt.hash(password, 10);

            let imageUrl = defaultImageUrl; 

            if (req.file) {
                const blob = storageClient.bucket(bucketName).file(`images/profile/${uuid.v4()}-${req.file.originalname}`);
                const blobStream = blob.createWriteStream({
                    metadata: {
                        contentType: req.file.mimetype
                    }
                });

                blobStream.on('error', (err) => {
                    console.error(err);
                    res.status(500).json({ message: 'Internal Server Error' });
                });

                blobStream.on('finish', async () => {
                    imageUrl = `https://storage.googleapis.com/${bucketName}/${blob.name}`;

                    const insertUserQuery = 'INSERT INTO users (email, password, name, gender, image_url) VALUES (?, ?, ?, ?, ?)';
                    db.query(insertUserQuery, [email, hashedPassword, name, gender, imageUrl], (err, result) => {
                        if (err) throw err;
                        const userId = result.insertId;
                        const userData = {
                            user_id: userId,
                            email: email,
                            name: name,
                            gender: gender,
                            image_url: imageUrl
                        };
                        res.status(201).json({
                            error: false,
                            status: 201,
                            message: 'User Created Successfully',
                            data: userData
                        });
                    });

                });

                blobStream.end(req.file.buffer);
            } else {

                const insertUserQuery = 'INSERT INTO users (email, password, name, gender, image_url) VALUES (?, ?, ?, ?, ?)';
                db.query(insertUserQuery, [email, hashedPassword, name, gender, imageUrl], (err, result) => {
                    if (err) throw err;
                    res.status(201).json({ error: false, status: 201, message: 'User Created Successfully' });
                });
            }
        });
    } catch (err) {
        console.error(err);
        res.status(500).json({ message: 'Internal Server Error' });
    }
});

router.post('/login', async (req, res) => {
    const { email, password } = req.body;

    if (!email || !password) {
        return res.status(400).json({ status: 400, error: true, message: 'Email and password are required' });
    }

    const sql = 'SELECT * FROM users WHERE email = ?';

    try {
        const user = await new Promise((resolve, reject) => {
            db.query(sql, [email], (err, result) => {
                if (err) reject(err);
                resolve(result);
            });
        });

        if (!user.length) {
            return res.status(401).json({ status: 401, message: 'Invalid email or password' });
        }

        const isMatch = await bcrypt.compare(password, user[0].password);

        if (!isMatch) {
            return res.status(401).json({ status: 401, message: 'Invalid email or password' });
        }

        const payload = { userId: user[0].user_id };
        const token = jwt.sign(payload, config.secret);

        res.json({
            error: false,
            status: 200,
            message: 'Success',
            loginResult: {
                userId: user[0].user_id,
                email: user[0].email,
                token: token
            }
        });
    } catch (err) {
        console.error(err);
        res.status(500).json({ message: 'Error logging in' });
    }
});

router.get('/views/profile', verifyToken, (req, res) => {
    const userId = req.user.user_id; 
    const getUserProfileQuery = `
        SELECT
            user_id,
            email,
            name,
            gender,
            image_url
        FROM users
        WHERE user_id = ?;
    `;
    db.query(getUserProfileQuery, [userId], (err, userProfile) => {
        if (err) {
            console.error(err);
            res.status(500).json({ error: true, message: 'Internal Server Error' });
        } else {
            if (userProfile.length === 0) {
                res.status(404).json({ error: true, message: 'User not found' });
            } else {
                res.status(200).json({ error: false, data: userProfile[0] });
            }
        }
    });
});

router.get('/views/food', (req, res) => {
    const name = req.query.name;
    const calories = req.query.calories;
    const proteins = req.query.proteins;
    const carbohydrate = req.query.carbohydrate;
    const fat = req.query.fat;
    const sort = req.query.sort || 'asc'; 

    let getAllFoodQuery = 'SELECT * FROM foods WHERE 1=1';
    const queryParams = [];

    if (name) {
        getAllFoodQuery += ' AND name LIKE ?';
        queryParams.push(`%${name}%`);
    }

    if (calories) {
        getAllFoodQuery += ' AND calories >= ?';
        queryParams.push(calories);
    }

    if (proteins) {
        getAllFoodQuery += ' AND proteins >= ?';
        queryParams.push(proteins);
    }

    if (carbohydrate) {
        getAllFoodQuery += ' AND carbohydrate >= ?';
        queryParams.push(carbohydrate);
    }

    if (fat) {
        getAllFoodQuery += ' AND fat >= ?';
        queryParams.push(fat);
    }

    if (sort === 'asc') {
        getAllFoodQuery += ' ORDER BY name ASC'; 
    }

    db.query(getAllFoodQuery, queryParams, (err, result) => {
        if (err) {
            console.error(err);
            res.status(500).json({ error: true, message: 'Internal Server Error' });
        } else {
            res.status(200).json({ error: false, message: 'Success', data_makanan: result });
        }
    });
});

router.get('/views/food/:food_id', (req, res) => {
    const foodId = req.params.food_id;

    const getFoodByIdQuery = 'SELECT * FROM foods WHERE food_id = ?';

    db.query(getFoodByIdQuery, [foodId], (err, result) => {
        if (err) {
            console.error(err);
            res.status(500).json({ error: true, message: 'Internal Server Error' });
        } else {
            res.status(200).json({ error: false, message: 'Success', data: result });
        }
    });
});

router.get('/food/:name', (req, res) => {
    const food_name = req.params.name; 
    console.log(food_name);

    const getFoodByNameQuery = 'SELECT * FROM foods WHERE name = ?'; 

    db.query(getFoodByNameQuery, [food_name], (err, result) => {
        if (err) {
            console.error(err);
            res.status(500).json({ error: true, message: 'Internal Server Error' });
        } else {
            res.status(200).json({ error: false, message: 'Success', data: result });
        }
    });
});

router.post('/create/meal', verifyToken, (req, res) => {
    const userId = req.user.user_id; 
    const { meal_title, meal_date, meal_time, meal_items } = req.body; 

    if (!meal_title || !meal_date || !meal_time || !meal_items) {
        return res.status(400).json({ status: 400, error: true, message: 'Meal Title, Meal date, meal time, and meal items are required' });
    }

    if (!Array.isArray(meal_items) || meal_items.length === 0) {
        return res.status(400).json({ status: 400, error: true, message: 'Meal items should be a non-empty array' });
    }

    db.beginTransaction((err) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        }

        const checkExistingMealQuery = 'SELECT * FROM meals WHERE user_id = ? AND meal_date = ? AND meal_time = ?';
        db.query(checkExistingMealQuery, [userId, meal_date, meal_time], (err, existingMeals) => {
            if (err) {
                console.error(err);
                return db.rollback(() => {
                    res.status(500).json({ error: true, message: 'Error checking existing meal entry' });
                });
            }

            if (existingMeals.length > 0) {
                return db.rollback(() => {
                    res.status(409).json({ error: true, message: 'Meal with the same date and time already exists, you can update in update/meal' });
                });
            }

            const insertMealQuery = 'INSERT INTO meals (title, user_id, meal_date, meal_time) VALUES (?, ?, ?, ?)';
            db.query(insertMealQuery, [meal_title, userId, meal_date, meal_time], (err, result) => {
                if (err) {
                    console.error(err);
                    return db.rollback(() => {
                        res.status(500).json({ error: true, message: 'Error creating meal entry' });
                    });
                }

                const mealId = result.insertId; 

                const getFoodNameQuery = 'SELECT name FROM foods WHERE food_id = ?';
                const mealItemsData = [];
                const promises = meal_items.map(item => {
                    return new Promise((resolve, reject) => {
                        db.query(getFoodNameQuery, [item.food_id], (err, foodResult) => {
                            if (err) {
                                return reject(err);
                            }
                            if (foodResult.length === 0) {
                                return reject(new Error(`Food item with id ${item.food_id} not found`));
                            }
                            const foodName = foodResult[0].name;
                            mealItemsData.push({
                                food_id: item.food_id,
                                name: foodName,
                                quantity: item.quantity
                            });
                            resolve();
                        });
                    });
                });

                Promise.all(promises)
                    .then(() => {

                        const insertMealItemsQuery = 'INSERT INTO meal_items (meal_id, food_id, quantity) VALUES (?, ?, ?)';
                        const mealItemsInsertPromises = mealItemsData.map(item => {
                            return new Promise((resolve, reject) => {
                                db.query(insertMealItemsQuery, [mealId, item.food_id, item.quantity], (err, result) => {
                                    if (err) {
                                        console.error(err);
                                        return db.rollback(() => {
                                            res.status(500).json({ error: true, message: 'Error creating meal item entry' });
                                        });
                                    }
                                    item.meal_item_id = result.insertId; 
                                    resolve();
                                });
                            });
                        });

                        Promise.all(mealItemsInsertPromises)
                            .then(() => {
                                db.commit((err) => {
                                    if (err) {
                                        console.error(err);
                                        return db.rollback(() => {
                                            res.status(500).json({ error: true, message: 'Error committing transaction' });
                                        });
                                    }

                                    const successMessage = `Meal entry created successfully, meal item ${mealItemsData.map(item => item.meal_item_id).join(', ')} created successfully`;

                                    res.status(201).json({
                                        status: 201,
                                        error: false,
                                        message: successMessage,
                                        data: {
                                            meal_id: mealId,
                                            userId: userId,
                                            meal_title: meal_title,
                                            meal_date: meal_date,
                                            meal_time: meal_time,
                                            meal_items: mealItemsData
                                        }

                                    });
                                });
                            })
                            .catch((err) => {
                                console.error(err);
                                db.rollback(() => {
                                    res.status(500).json({ error: true, message: 'Error inserting meal items' });
                                });
                            });
                    })
                    .catch((err) => {
                        console.error(err);
                        db.rollback(() => {
                            res.status(500).json({ error: true, message: 'Error retrieving food names' });
                        });
                    });
            });
        });
    });
});

router.get('/views/meal', verifyToken, (req, res) => {
    const userId = req.user.user_id; 

    let getAllMealsQuery = `
     SELECT
            meals.meal_id,
            meals.title,
            meals.user_id,
            users.name AS user_name,
            meals.meal_date,
            meals.meal_time,
            meal_items.food_id,
            meal_items.quantity,
            foods.name AS food_name,
            foods.calories,
            foods.proteins,
            foods.carbohydrate,
            foods.fat,
            meal_items.meal_item_id,
            (foods.calories * meal_items.quantity) AS total_calories,
            (foods.proteins * meal_items.quantity) AS total_proteins,
            (foods.carbohydrate * meal_items.quantity) AS total_carbohydrate,
            (foods.fat * meal_items.quantity) AS total_fats
        FROM meals
        INNER JOIN meal_items ON meals.meal_id = meal_items.meal_id
        INNER JOIN foods ON meal_items.food_id = foods.food_id
        INNER JOIN users ON meals.user_id = users.user_id
        WHERE meals.user_id = ? `;

    const queryParams = [userId];

    if (req.query.date) {
        getAllMealsQuery += ' AND meals.meal_date = ?';
        queryParams.push(req.query.date);
    }

    if (req.query.food_id) {
        getAllMealsQuery += ' AND meal_items.food_id = ?';
        queryParams.push(req.query.food_id);
    }

    if (req.query.food_name) {
        getAllMealsQuery += ' AND foods.name LIKE ?';
        queryParams.push('%' + req.query.food_name + '%');
    }

    if (req.query.time) {
        getAllMealsQuery += ' AND meals.meal_time = ?';
        queryParams.push(req.query.time);
    }

    if (req.query.calories) {
        getAllMealsQuery += ' AND foods.calories >= ?';
        queryParams.push(req.query.calories);
    }

    if (req.query.protein) {
        getAllMealsQuery += ' AND foods.proteins >= ?';
        queryParams.push(req.query.protein);
    }

    if (req.query.carbs) {
        getAllMealsQuery += ' AND foods.carbohydrate >= ?';
        queryParams.push(req.query.carbs);
    }

    if (req.query.fats) {
        getAllMealsQuery += ' AND foods.fat >= ?';
        queryParams.push(req.query.fats);
    }

    if (req.query.sort) {
        if (req.query.sort === 'older') {
            getAllMealsQuery += ' ORDER BY meals.meal_date ASC, meals.meal_time ASC';
        } else if (req.query.sort === 'newer') {
            getAllMealsQuery += ' ORDER BY meals.meal_date DESC, meals.meal_time DESC';
        }

    }

    db.query(getAllMealsQuery, queryParams, (err, result) => {
        if (err) {
            console.error(err);
            res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        } else {
            const mealDetails = [];
            let currentMealId = null;
            let currentMealIndex = -1;

            result.forEach(row => {
                if (row.meal_id !== currentMealId) {
                    currentMealId = row.meal_id;
                    currentMealIndex++;
                    mealDetails[currentMealIndex] = {
                        meal_id: row.meal_id,
                        title: row.title,
                        user_id: row.user_id,
                        name: row.user_name,
                        meal_date: row.meal_date,
                        meal_time: row.meal_time,
                        meal_items: []
                    };
                }

                mealDetails[currentMealIndex].meal_items.push({
                    meal_items: row.meal_item_id,
                    food_id: row.food_id,
                    food_name: row.food_name,
                    quantity: row.quantity,
                    calories: row.calories,
                    proteins: row.proteins,
                    carbohydrate: row.carbohydrate,
                    fat: row.fat,
                    total_calories: row.total_calories,
                    total_proteins: row.total_proteins,
                    total_carbohydrate: row.total_carbohydrate,
                    total_fats: row.total_fats
                });
            });

            res.status(200).json({ status: 200, error: false, message: 'Success', meal_details: mealDetails });
        }
    });
});

router.get('/views/meal/:meal_id', verifyToken, (req, res) => {
    const userId = req.user.user_id; 
    const mealId = req.params.meal_id; 

    const getMealDetailsQuery = `
        SELECT
            meals.meal_id,
            meals.title,
            meals.user_id,
            users.name AS user_name,
            meals.meal_date,
            meals.meal_time,
            meal_items.food_id,
            meal_items.quantity,
            foods.name AS food_name,
            foods.calories,
            foods.proteins,
            foods.carbohydrate,
            foods.fat,
            (foods.calories * meal_items.quantity) AS total_calories,
            (foods.proteins * meal_items.quantity) AS total_proteins,
            (foods.carbohydrate * meal_items.quantity) AS total_carbohydrate,
            (foods.fat * meal_items.quantity) AS total_fats
        FROM meals
        INNER JOIN meal_items ON meals.meal_id = meal_items.meal_id
        INNER JOIN foods ON meal_items.food_id = foods.food_id
        INNER JOIN users ON meals.user_id = users.user_id
        WHERE meals.meal_id = ? AND meals.user_id = ?`;

    db.query(getMealDetailsQuery, [mealId, userId], (err, result) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        }

        if (result.length === 0) {
            return res.status(404).json({ status: 404, error: true, message: 'Meal not found or you do not have access to this meal or different user' });
        }

        const mealDetails = {
            meal_id: result[0].meal_id,
            title: result[0].title,
            user_id: result[0].user_id,
            user_name: result[0].user_name,
            meal_date: result[0].meal_date,
            meal_time: result[0].meal_time,
            meal_items: result.map(item => ({
                food_id: item.food_id,
                food_name: item.food_name,
                quantity: item.quantity,
                calories: item.calories,
                proteins: item.proteins,
                carbohydrate: item.carbohydrate,
                fat: item.fat,
                total_calories: item.total_calories,
                total_proteins: item.total_proteins,
                total_carbohydrate: item.total_carbohydrate,
                total_fats: item.total_fats
            }))
        };

        res.status(200).json({ status: 200, error: false, message: 'Success', meal_details: mealDetails });
    });
});

router.get('/views/meal/item/:id', verifyToken, (req, res) => {
    const userId = req.user.user_id; 
    const itemId = req.params.id; 

    const getItemDetailsQuery = `
        SELECT
        meal_items.meal_item_id,
        meal_items.food_id,
        foods.name AS food_name,
        meal_items.quantity,
        foods.calories,
        foods.proteins,
        foods.carbohydrate,
        foods.fat,
        meals.meal_id,
        (foods.calories * meal_items.quantity) AS total_calories,
        (foods.proteins * meal_items.quantity) AS total_proteins,
        (foods.carbohydrate * meal_items.quantity) AS total_carbohydrate,
        (foods.fat * meal_items.quantity) AS total_fats
    FROM meal_items
    INNER JOIN foods ON meal_items.food_id = foods.food_id
    INNER JOIN meals ON meal_items.meal_id = meals.meal_id
    WHERE meal_items.meal_item_id = ? AND meals.user_id = ?`;

    db.query(getItemDetailsQuery, [itemId, userId], (err, result) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        }

        if (result.length === 0) {
            return res.status(404).json({ status: 404, error: true, message: 'Item not found or you do not have access to this item or different user' });
        }

        const itemDetails = {
            meal_item_id: result[0].meal_item_id,
            meal_id: result[0].meal_id,
            food_id: result[0].food_id,
            food_name: result[0].food_name,
            quantity: result[0].quantity,
            calories: result[0].calories,
            proteins: result[0].proteins,
            carbohydrate: result[0].carbohydrate,
            fat: result[0].fat,
            total_calories: result[0].total_calories,
            total_proteins: result[0].total_proteins,
            total_carbohydrate: result[0].total_carbohydrate,
            total_fats: result[0].total_fats
        };

        res.status(200).json({ status: 200, error: false, message: 'Success', item_details: itemDetails });
    });
});

router.get('/views/notes/:note_id?', verifyToken, (req, res) => {
    const userId = req.user.user_id; 
    const noteId = req.params.note_id; 

    let getNotesQuery;
    let queryParams;

    if (noteId) {

        getNotesQuery = `
            SELECT
                notes.note_id,
                notes.user_id,
                users.name,
                notes.title,
                notes.content,
                notes.image_url,
                notes.created_at,
                notes.updated_at
            FROM notes
            INNER JOIN users ON notes.user_id = users.user_id
            WHERE notes.user_id = ? AND notes.note_id = ?;
        `;
        queryParams = [userId, noteId];
    } else {

        getNotesQuery = `
            SELECT
                notes.note_id,
                notes.user_id,
                users.name,
                notes.title,
                notes.content,
                notes.image_url,
                notes.created_at,
                notes.updated_at
            FROM notes
            INNER JOIN users ON notes.user_id = users.user_id
            WHERE notes.user_id = ?;
        `;
        queryParams = [userId];
    }

    db.query(getNotesQuery, queryParams, (err, notes) => {
        if (err) {
            console.error(err);
            res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        } else {
            res.status(200).json({ status: 200, error: false, notes: notes, message: 'Success' });
        }
    });
});

router.get('/views/history', verifyToken, (req, res) => {
    const userId = req.user.user_id;
    let getMealsQuery = `
        SELECT
            meals.meal_date,
            foods.food_id,
            foods.name AS food_name,
            SUM(meal_items.quantity) AS total_quantity,
            SUM(foods.calories * meal_items.quantity) AS total_calories,
            SUM(foods.proteins * meal_items.quantity) AS total_proteins,
            SUM(foods.carbohydrate * meal_items.quantity) AS total_carbohydrate,
            SUM(foods.fat * meal_items.quantity) AS total_fats
        FROM meals
        INNER JOIN meal_items ON meals.meal_id = meal_items.meal_id
        INNER JOIN foods ON meal_items.food_id = foods.food_id
        WHERE meals.user_id = ? AND meals.meal_date <= CURDATE()`;

    const queryParams = [userId];

    if (req.query.date) {
        getMealsQuery += ' AND meals.meal_date = ?';
        queryParams.push(req.query.date);
    }

    if (req.query.time) {
        getMealsQuery += ' AND meals.meal_time = ?';
        queryParams.push(req.query.time);
    }

    getMealsQuery += ' GROUP BY meals.meal_date, foods.food_id, foods.name';

    if (req.query.sort) {
        if (req.query.sort === 'older') {
            getMealsQuery += ' ORDER BY meals.meal_date ASC';
        } else if (req.query.sort === 'newer') {
            getMealsQuery += ' ORDER BY meals.meal_date DESC';
        }
    } else {
        getMealsQuery += ' ORDER BY meals.meal_date DESC';
    }

    db.query(getMealsQuery, queryParams, (err, meals) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        }

        const journal = {};
        meals.forEach(meal => {
            const date = meal.meal_date.toISOString().split('T')[0];
            if (!journal[date]) {
                journal[date] = {
                    summary: []
                };
            }

            journal[date].summary.push({
                food_id: meal.food_id,
                food_name: meal.food_name,
                times_eaten: meal.total_quantity,
                total_calories: meal.total_calories,
                total_proteins: meal.total_proteins,
                total_carbohydrate: meal.total_carbohydrate,
                total_fats: meal.total_fats
            });
        });

        res.status(200).json({
            status: 200,
            error: false,
            userId: userId,
            history: journal
        });
    });
});

router.put('/update/meal/:meal_id', verifyToken, (req, res) => {
    const userId = req.user.user_id;
    const mealId = req.params.meal_id;
    const { meal_title, meal_date, meal_time, meal_items } = req.body;

    if (!meal_title || !meal_date || !meal_time || !meal_items) {
        return res.status(400).json({ status: 400, error: true, message: 'Meal title, date, time, and items are required' });
    }

    if (!Array.isArray(meal_items) || meal_items.length === 0) {
        return res.status(400).json({ status: 400, error: true, message: 'Meal items should be a non-empty array' });
    }

    db.beginTransaction((err) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        }

        const updateMealQuery = 'UPDATE meals SET title = ?, meal_date = ?, meal_time = ? WHERE meal_id = ? AND user_id = ?';
        db.query(updateMealQuery, [meal_title, meal_date, meal_time, mealId, userId], (err, result) => {
            if (err) {
                console.error(err);
                return db.rollback(() => {
                    res.status(500).json({ error: true, message: 'Error updating meal entry' });
                });
            }

            const deleteMealItemsQuery = 'DELETE FROM meal_items WHERE meal_id = ?';
            db.query(deleteMealItemsQuery, [mealId], (err, result) => {
                if (err) {
                    console.error(err);
                    return db.rollback(() => {
                        res.status(500).json({ error: true, message: 'Error deleting old meal items' });
                    });
                }

                const insertMealItemsQuery = 'INSERT INTO meal_items (meal_id, food_id, quantity) VALUES (?, ?, ?)';
                const mealItemsPromises = meal_items.map(item => {
                    return new Promise((resolve, reject) => {
                        db.query(insertMealItemsQuery, [mealId, item.food_id, item.quantity], (err, result) => {
                            if (err) {
                                return reject(err);
                            }

                            const mealItemId = result.insertId;

                            const getFoodNameQuery = 'SELECT name FROM foods WHERE food_id = ?';
                            db.query(getFoodNameQuery, [item.food_id], (err, foodResult) => {
                                if (err) {
                                    return reject(err);
                                }

                                resolve({
                                    meal_item_id: mealItemId,
                                    food_id: item.food_id,
                                    food_name: foodResult[0].name,
                                    quantity: item.quantity
                                });
                            });
                        });
                    });
                });

                Promise.all(mealItemsPromises)
                    .then(mealItemsResults => {
                        db.commit((err) => {
                            if (err) {
                                console.error(err);
                                return db.rollback(() => {
                                    res.status(500).json({ error: true, message: 'Error committing transaction' });
                                });
                            }
                            res.status(200).json({
                                status: 200,
                                error: false,
                                message: 'Meal entry updated successfully',
                                meal_updates: {
                                    user_id: userId,
                                    meal_id: mealId,
                                    meal_title,
                                    meal_date,
                                    meal_time,
                                    meal_items: mealItemsResults
                                }
                            });
                        });
                    })
                    .catch(err => {
                        console.error(err);
                        db.rollback(() => {
                            res.status(500).json({ error: true, message: 'Error creating new meal item entries' });
                        });
                    });
            });
        });
    });
});

router.delete('/delete/meal/:meal_id', verifyToken, (req, res) => {
    const userId = req.user.user_id;
    const mealId = req.params.meal_id;

    db.beginTransaction((err) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        }

        const deleteMealItemsQuery = 'DELETE FROM meal_items WHERE meal_id = ?';
        db.query(deleteMealItemsQuery, [mealId], (err, result) => {
            if (err) {
                console.error(err);
                return db.rollback(() => {
                    res.status(500).json({ error: true, message: 'Error deleting meal items' });
                });
            }

            const deleteMealQuery = 'DELETE FROM meals WHERE meal_id = ? AND user_id = ?';
            db.query(deleteMealQuery, [mealId, userId], (err, result) => {
                if (err) {
                    console.error(err);
                    return db.rollback(() => {
                        res.status(500).json({ error: true, message: 'Error deleting meal entry' });
                    });
                }

                db.commit((err) => {
                    if (err) {
                        console.error(err);
                        return db.rollback(() => {
                            res.status(500).json({ error: true, message: 'Error committing transaction' });
                        });
                    }
                    res.status(200).json({ status: 200, error: false, userId: userId, Name: req.user.name, message: 'Meal entry deleted successfully' });
                });
            });
        });
    });
});

router.post('/create/note', verifyToken, uploadSingle('myImage'), handleMulterError, async (req, res) => {
    const userId = req.user.user_id;
    const { title, content } = req.body;

    if (!title || !content) {
        return res.status(400).json({ status: 400, error: true, message: 'Title and content are required' });
    }

    try {
        let imageUrlToSave = '';

        if (req.file) {
            const file = req.file;
            const fileName = `${uuid.v4()}${path.extname(file.originalname)}`;
            const fileBuffer = file.buffer;

            await storageClient.bucket(bucketName).file(`images/note/${fileName}`).save(fileBuffer);

            imageUrlToSave = `https://storage.googleapis.com/${bucketName}/${blob.name}`;
        }

        const insertNoteQuery = 'INSERT INTO notes (user_id, title, content, image_url, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())';

        db.query(insertNoteQuery, [userId, title, content, imageUrlToSave], (err, result) => {
            if (err) {
                console.error(err);
                return res.status(500).json({ error: true, message: 'Error creating note entry' });
            }

            const noteId = result.insertId; 

            const selectNoteQuery = 'SELECT user_id, note_id, title, content, image_url, created_at, updated_at FROM notes WHERE note_id = ?';
            db.query(selectNoteQuery, [noteId], (err, rows) => {
                if (err) {
                    console.error(err);
                    return res.status(500).json({ error: true, message: 'Error retrieving note entry' });
                }
                if (rows.length === 0) {
                    return res.status(404).json({ error: true, message: 'Note not found' });
                }
                const note = rows[0]; 
                res.status(201).json({
                    status: 201,
                    error: false,
                    message: 'Note created successfully',
                    note: {
                        user_id: note.user_id,
                        note_id: note.note_id,
                        title: note.title,
                        content: note.content,
                        image_url: note.image_url,
                        created_at: note.created_at,
                        updated_at: note.updated_at
                    }
                });
            });
        });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ error: true, message: 'Error uploading image to Google Cloud Storage' });
    }
});

router.put('/update/note/:note_id', verifyToken, uploadSingle('myImage'), handleMulterError, async (req, res) => {
    const userId = req.user.user_id; 
    const noteId = req.params.note_id;
    const { title, content } = req.body; 

    try {
        let imageUrlToSave = '';

        const getPreviousImageUrlQuery = 'SELECT image_url FROM notes WHERE note_id = ? AND user_id = ?';
        db.query(getPreviousImageUrlQuery, [noteId, userId], async (err, result) => {
            if (err) {
                console.error('Error getting previous note image URL:', err);
                return res.status(500).json({ error: true, message: 'Error getting previous note image URL' });
            }

            const previousImageUrl = result.length > 0 ? result[0].image_url : null;

            if (req.file) {
                const file = req.file;
                const fileName = `${uuid.v4()}${path.extname(file.originalname)}`;
                const fileBuffer = file.buffer;

                await storageClient.bucket(bucketName).file(`images/note/${fileName}`).save(fileBuffer);

                imageUrlToSave =  `https://storage.googleapis.com/${bucketName}/${blob.name}`;
            }

            let updateColumns = [];
            let updateValues = [];
            if (title) {
                updateColumns.push('title');
                updateValues.push(title);
            }
            if (content) {
                updateColumns.push('content');
                updateValues.push(content);
            }
            if (imageUrlToSave || !title && !content) {
                updateColumns.push('image_url');
                updateValues.push(imageUrlToSave || previousImageUrl);
            }

            db.beginTransaction((err) => {
                if (err) {
                    console.error(err);
                    return res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
                }

                const updateNoteQuery = `UPDATE notes SET ${updateColumns.map(col => `${col} = ?`).join(', ')}, updated_at = NOW() WHERE note_id = ? AND user_id = ?`;
                db.query(updateNoteQuery, [...updateValues, noteId, userId], async (err, result) => {
                    if (err) {
                        console.error(err);
                        return db.rollback(() => {
                            res.status(500).json({ error: true, message: 'Error updating note' });
                        });
                    }

                    if (result.affectedRows === 0) {

                        return db.rollback(() => {
                            res.status(404).json({ error: true, message: 'Note not found or not authorized' });
                        });
                    }

                    if (previousImageUrl && previousImageUrl !== imageUrlToSave) {
                        const urlParts = previousImageUrl.split('/');
                        const previousImagePath = urlParts.slice(urlParts.indexOf('images')).join('/');
                        try {
                            await storageClient.bucket(bucketName).file(previousImagePath).delete();
                            console.log('Previous note image deleted successfully.');
                        } catch (deleteErr) {
                            if (deleteErr.code === 404) {
                                console.log('Previous note image not found, skipping deletion.');
                            } else {
                                console.error('Error deleting previous note image:', deleteErr);
                            }
                        }
                    }

                    db.commit((err) => {
                        if (err) {
                            console.error(err);
                            return db.rollback(() => {
                                res.status(500).json({ error: true, message: 'Error committing transaction' });
                            });
                        }
                        res.status(200).json({
                            status: 200,
                            error: false,
                            message: 'Note updated successfully',
                            data: {
                                note_id: noteId,
                                user_id: userId,
                                title: title,
                                content: content,
                                image_url: imageUrlToSave,
                                updated_at: new Date()
                            }
                        });
                    });
                });
            });
        });
    } catch (err) {
        console.error(err);
        return res.status(500).json({ error: true, message: 'Error uploading image to Google Cloud Storage' });
    }
});

router.delete('/delete/note/:note_id', verifyToken, (req, res) => {
    const userId = req.user.user_id; 
    const noteId = req.params.note_id; 

    const getImageUrlQuery = 'SELECT image_url FROM notes WHERE note_id = ? AND user_id = ?';

    db.query(getImageUrlQuery, [noteId, userId], (err, result) => {
        if (err) {
            console.error('Error getting note image URL:', err);
            return res.status(500).json({ status: 500, error: true, message: 'Error getting note image URL' });
        }

        if (result.length === 0) {
            return res.status(404).json({ status: 404, error: true, message: 'Note not found or not authorized' });
        }

        const imageUrl = result[0].image_url;

        const deleteNoteQuery = 'DELETE FROM notes WHERE note_id = ? AND user_id = ?';

        db.query(deleteNoteQuery, [noteId, userId], async (err, result) => {
            if (err) {
                console.error('Error deleting note:', err);
                return res.status(500).json({ status: 500, error: true, message: 'Error deleting note' });
            }

            if (result.affectedRows === 0) {

                return res.status(404).json({ status: 404, error: true, message: 'Note not found or not authorized or note different user' });
            }

            if (imageUrl) {
                const urlParts = imageUrl.split('/');
                const imagePath = urlParts.slice(urlParts.indexOf('images')).join('/');
                console.log(urlParts + imagePath);
                try {
                    await storageClient.bucket(bucketName).file(imagePath).delete();
                    console.log('Note image deleted successfully.');
                } catch (deleteErr) {
                    if (deleteErr.code === 404) {
                        console.log('Note image not found, skipping deletion.');
                    } else {
                        console.error('Error deleting note image:', deleteErr);
                    }
                }
            }

            res.status(200).json({
                status: 200,
                error: false,
                message: 'Note deleted successfully',
                data: {
                    user_id: userId,
                }
            });
        });
    });
});

router.get('/views/bulking', (req, res) => {
    const getBulkingItemsQuery = `
        SELECT bulking_item_id, food_name, description, image_url 
        FROM bulking_items`;

    db.query(getBulkingItemsQuery, (err, results) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        }
        res.status(200).json({ status: 200, error: false, data: results });
    });
});
router.get('/views/bulking/:bulking_item_id', (req, res) => {
    const bulkingItemId = req.params.bulking_item_id;

    const getBulkingItemDetailsQuery = `
        SELECT 
            bi.bulking_item_id,
            bi.food_name AS bulking_food_name, 
            bi.description, 
            bi.image_url AS bulking_image_url,
            bid.bulking_item_detail_id,
            bid.food_id, 
            f.name AS food_name,
            f.calories,
            f.proteins,
            f.carbohydrate,
            f.fat,
            f.image_url AS food_image_url,
            bid.quantity,
            (f.calories * bid.quantity) AS total_calories,
            (f.proteins * bid.quantity) AS total_proteins,
            (f.carbohydrate * bid.quantity) AS total_carbohydrate,
            (f.fat * bid.quantity) AS total_fat
        FROM bulking_items bi
        INNER JOIN bulking_item_details bid ON bi.bulking_item_id = bid.bulking_item_id
        INNER JOIN foods f ON bid.food_id = f.food_id
        WHERE bi.bulking_item_id = ?`;

    db.query(getBulkingItemDetailsQuery, [bulkingItemId], (err, results) => {
        if (err) {
            console.error(err);
            return res.status(500).json({ status: 500, error: true, message: 'Internal Server Error' });
        }

        if (results.length === 0) {
            return res.status(404).json({ status: 404, error: true, message: 'Bulking item not found' });
        }

        const bulkingItem = {
            bulking_item_id: results[0].bulking_item_id,
            food_name: results[0].bulking_food_name,
            description: results[0].description,
            image_url: results[0].bulking_image_url,
            bulking_item_details: [],
            total_all_calories: 0,
            total_all_proteins: 0,
            total_all_carbohydrate: 0,
            total_all_fat: 0
        };

        results.forEach(detail => {
            bulkingItem.bulking_item_details.push({
                bulking_item_detail_id: detail.bulking_item_detail_id,
                food_id: detail.food_id,
                food_name: detail.food_name,
                image_url: detail.food_image_url,
                calories: detail.calories,
                proteins: detail.proteins,
                carbohydrate: detail.carbohydrate,
                fat: detail.fat,
                quantity: detail.quantity,
                total_calories: detail.total_calories,
                total_proteins: detail.total_proteins,
                total_carbohydrate: detail.total_carbohydrate,
                total_fat: detail.total_fat
            });

            bulkingItem.total_all_calories += detail.total_calories;
            bulkingItem.total_all_proteins += detail.total_proteins;
            bulkingItem.total_all_carbohydrate += detail.total_carbohydrate;
            bulkingItem.total_all_fat += detail.total_fat;
        });

        res.status(200).json({
            status: 200,
            error: false,
            data: bulkingItem
        });
    });
});
module.exports = router;
