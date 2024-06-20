# CalorieWise

CalorieWise is designed to address the increasing challenges people face regarding nutrition and calorie management. With obesity rates doubling over the past two decades, both adults and children are at risk of significant health and educational setbacks. The primary cause is irregular eating patterns and unhealthy lifestyles, particularly in calorie consumption. CalorieWise aims to simplify monitoring and managing calorie intake to promote healthier lifestyles.

## Team

| Nama                      | Bangkit-ID   | Path               |
|---------------------------|--------------|--------------------|
| Muhammad Raditya Putra    | M295D4KY2274 | Machine Learning   |
| Malique Abdul Aziz        | M295D4KY2272 | Machine Learning   |
| Muhammad Fatih Bagaskara  | M001D4KY1676 | Machine Learning   |
| Daviana Dwitasari. E. F   | C269D4KX0433 | Cloud Computing    |
| Rahmat Wahyudi            | C133D4KY0998 | Cloud Computing    |
| Muhammad Fawwaz Naufal    | A001D4KY3890 | Mobile Development |
| Akhdan Ravi Andaman       | A001D4KY3653 | Mobile Development |

## Mobile Development


### Features


- Login and Register  
![Login](./Mobile%20Development/images_mockup/Login.png)

- Adding your daily note. On this feature, user can make a daily note on what they been eating. And the note will be featured in note page  
![Note](./Mobile%20Development/images_mockup/Note.png)

- Food Searching. finding food and ingredients simply by typing the name of the food, you can find out the nutritional content of the food, namely calories, protein, fat, carbohydrate.  
![Searching](./Mobile%20Development/images_mockup/Searching.png)

- Food Analyze. Pick image from camera, it will detect what is your food and the nutritions.  
![ML](./Mobile%20Development/images_mockup/ML.png)

- Food Recommendation, there are 2 options which is bulking for gain weight or cutting for lose weight  
![Recommendation](./Mobile%20Development/images_mockup/recommend.png)

- Profile Views. with this feature you can change your profile photo, your name, your email.  
![Profile](./Mobile%20Development/images_mockup/Profile2.png)

- Food History. you can see the history of the nutritions of your meals by date  
![History](./Mobile%20Development/images_mockup/history.png)


### Tools
- Android Studio
- JRE (Java Runtime Environment) or JDK (Java Development Kit).

# Machine Learning
We developed a highly efficient food image classification model using custom convolutional neural networks (CNN) combined with transfer learning using VGG16. Utilizing the VGG16 architecture, a well-known and highly effective deep learning model, we significantly enhanced our model's performance. We trained this model with thousands of food images across 14 class labels, achieving an accuracy rate of over 80%. This ensures our application can reliably provide users with precise nutritional information based on their uploaded food images.


![History](./Machine%20Learning/.images/models.png)

## Datasets
This is our [Dataset](https://drive.google.com/drive/folders/1gVJCdNCKionhBdR5HBitFLLD7O0DSGHj?usp=sharing)
For the datasets, we search from various sources on Kaggle and merge them together. So that 14 labels are collected, which are:
1. Bakso
2. Burger
3. Caesar Salad
4. Cumi Goreng Tepung
5. Kerang Tiram
6. Nasi
7. Nasi goreng
8. Omelette
9. Sate Ayam
10. Sayap Ayam Goreng
11. Siomay
12. Spaghetti
13. Steak
14. Yoghurt

## Model
This is our [Model]([https://drive.google.com/drive/folders/1gVJCdNCKionhBdR5HBitFLLD7O0DSGHj?usp=sharing](https://drive.google.com/drive/folders/1mk_ummae3FHSwoSOdjGgohM9Jd1VmCQZ?usp=sharing))

```python
base_model = VGG16(weights='imagenet', include_top=False, input_shape=(224, 224, 3))
model = models.Sequential([
    base_model,
    layers.Flatten(),
    layers.Dense(512, activation='relu', kernel_regularizer=regularizers.l2(0.001)),
    layers.Dropout(0.6),
    layers.Dense(train_dataset.num_classes, activation='softmax')
])
```

The model architecture using transfer learning with a pre-trained VGG16 model as a feature extractor, combined with custom layers for classification. Below is a brief explanation for our Image Classification Model Architecture:

- Base Model: VGG16
    - Weights: Pre-trained on ImageNet.
    - Top Layers: Excluded.
    - Input Shape: (224, 224, 3), matching the input image dimensions.
    - Purpose: Acts as a powerful feature extractor, leveraging its learned convolutional filters.
      
- Custom Layers

    - Flatten Layer: Converts the 3D feature maps from the VGG16 model into a 1D feature vector.
    - Dense Layer:
          - Units: 512 neurons.
          - Activation: ReLU (Rectified Linear Unit).
          - Regularization: L2 regularization with a penalty factor of 0.001 to prevent overfitting.
    - Dropout Layer:
          - Rate: 0.6, dropping 60% of the units to further mitigate overfitting.
    - Output Dense Layer:
          - Units: Equal to the number of classes in the train_dataset.
          - Activation: Softmax, providing probabilities for each class.

## Training
The model is trained using the model.fit() method on the train_dataset for 30 epochs, with validation_generator providing the validation data. To enhance training efficiency and model performance, two key callbacks are employed: EarlyStopping and ReduceLROnPlateau.

```python
early_stopping = EarlyStopping(monitor='val_loss', patience=10, restore_best_weights=True)
reduce_lr = ReduceLROnPlateau(monitor='val_loss', factor=0.2, patience=5, min_lr=0.00001)

history = model.fit(
    train_dataset,
    epochs=30,
    validation_data=validation_generator,
    callbacks=[reduce_lr, early_stopping]
)
```

- Model Training
    - Method: model.fit()
    - Training Data: train_dataset
    - Epochs: 30
    - Validation Data: validation_generator
    - Callbacks: reduce_lr, early_stopping - Utilizes both callbacks for optimized training.

- Callbacks
    - EarlyStopping
        - Monitor: val_loss - Stops training when validation loss stops improving.
        - Patience: 10 epochs - Waits for 10 epochs before stopping if no improvement.
        - Restore Best Weights: Restores model weights from the epoch with the best validation loss.
    - ReduceLROnPlateau
        - Monitor: val_loss - Reduces learning rate when validation loss plateaus.
        - Factor: 0.2 - Reduces the learning rate by a factor of 0.2.
        - Patience: 5 epochs - Waits for 5 epochs before reducing learning rate.
        - Min LR: 0.00001 - Ensures the learning rate doesn't go below this threshold.

## ML API Documentation 
### URL
https://backend-ml-api-4-gsmsrhmyzq-et.a.run.app/
## Predict Image

#### URL
`/predict`

#### Method
POST


#### Parameters
- `photo` : file multipartbody


#### Request Body and Response JSON
##### Request Body 
-
  ```json
  {
    "photo": file(image/jpeg, image/png) 
  }

- Response 200 OK
  ```json
    {
    "confidenceScore": 85.97714900970459,
    "data": [
        {
            "calories": 76,
            "carbohydrate": 9.2,
            "fat": 2.5,
            "food_id": 64,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/64.jpg",
            "name": "Bakso",
            "proteins": 4.1
        }
    ],
    "error": false,
    "isAboveThreshold": true,
    "message": "Model is predicted successfully.",
    "status": 200
    }



## Cloud Computing

### Cloud Architecture

![CC](./Cloud%20Computing/Images/cloud.jpg)

### Database Design
![db](./Cloud%20Computing/Images/db.png)



## API Documentation

### URL
https://api-backend-dot-caloriewise-425017.et.r.appspot.com/

## Register Profile



#### URL
`/register`

#### Method
POST


#### Parameters
- `email` (string): New user email (required)
- `password` (string): New user password must be at least 8 characters (required)
- `name` (string): New user name  (required)
- `gender` (string): New user gender  (required, must "Male" or "Female")

#### Request Body and Response JSON
##### 1. Request Without Input Image
- Request Body 
  ```json
  {
  "email": "example.johndoe@gmail.com",
  "password": "johndoe123",
  "name": "John Doe",
  "gender": "Male"
  }
- Response 201 Created 
  ```json
  {
    "error": false,
    "status": 201,
    "message": "User Created Successfully",
    "data": {
        "user_id": 9,
        "email": "example.johndoe@gmail.com",
        "name": "John Doe",
        "gender": "Male"
            }
    }

##### 2. Request With Input Image
- Request Body form-data
  ```json
  {
  "email": "example.johndoe@gmail.com",
  "password": "johndoe123",
  "name": "John Doe",
  "gender": "Male",
  "myImage": "file.jpg/png/bmp" //maks 5MB
  }
- Response 201 Created 
  ```json
  {
    "error": false,
    "status": 201,
    "message": "User Created Successfully",
    "data": {
        "user_id": 9,
        "email": "example.johndoe@gmail.com",
        "name": "John Doe",
        "gender": "Male",
        "image_url": "https://storage.googleapis.com/caloriewise-bucket/images/profile/default.jpeg"
            }
    }
##### 3. Request With Input Image >5MB
- Request Body form-data
  ```json
  {
  "email": "example.johndoe@gmail.com",
  "password": "johndoe123",
  "name": "John Doe",
  "gender": "Male",
  "myImage": "file.jpg/png/bmp" //maks 5MB
  }
- Response 413 Payload too Large
  ```json

  {
    "error": true,
    "message": "File too large. Maximum size is 5MB."
  }

##### 4. Request With Email Already Exists
- Request Body form-data
  ```json
  {
  "email": "example.johndoe@gmail.com",
  "password": "johndoe123",
  "name": "John Doe",
  "gender": "Male",
  "myImage": "file.jpg/png/bmp" //maks 5MB
  }
- Response 409 Conflict
  ```json

  {
    "status": 409,
    "error": true,
    "message": "Email already exists"
  }
## Login Profile

### URL
`/login`

### Method
POST


### Parameters
- `email` (string): email (required)
- `password` (string): password (required)

#### Request Body and Response JSON

- Request Body 
  ```json
  {
  "email": "example.johndoe@gmail.com",
  "password": "Rahmadcapstone"
  }
- Response 200 OK 
  ```json
  {
    "error": false,
    "status": 200,
    "message": "Success",
    "loginResult": {
        "userId": 9,
        "email": "example.johndoe@gmail.com",
        "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjksImlhdCI6MTcxNzI1NzExOX0.Ev-zFxE0CVFU2OhBdhBcI_DWsGV1vR9HTbsaP2T5G5"
    }
  }

## View Profile by Retrieved User

### URL
`/views/profile`

### Method
GET

### Headers Authorization
Required (Bearer token)

### Parameters
- `email` (string): user email
- `name` (string): user name
- `gender` (string): user gender
- `image_url` (string): user image

#### Request Body and Response JSON

- Response 200 OK 
  ```json
  {
    "error": false,
    "data": {
        "user_id": 9,
        "email": "john.doe@gmail.com",
        "name": "Joh Doe",
        "gender": "Male",
        "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/profile/91d489d8-a2d3-48ab-b965-d6e005b335ac.jpeg"
    }
  }



## Update Profile
### URL
`/update/profile`

### Method
PUT

### Headers Authorization
Required (Bearer token)

### Parameters
- `name` (string): New user name (optional)
- `gender` (string): New user gender (optional)
- `email` (string): New user email (optional)
- `password` (string): New user password (optional)
- `myImage` (file): New profile image (optional)

#### Request Body and Response JSON
##### 1. Request Without Input Image
- Request Body 
  ```json
  {
  "name": "John Doe",
  "password": "rahmat9872",
  "email": "example.johndoe@gmail.com",
  "gender": "Male",
  }
- Response 200 OK
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Profile updated successfully",
    "data": {
        "user_id": 9,
        "name": "Rahmat",
        "gender": "Male",
        "email": "example.johndoe@gmail.com"
    }
  }
##### 2. Request With Input Image
- Request Body form-data
  ```json
  {
  "name": "John Doe",
  "password": "rahmat9872",
  "email": "example.johndoe@gmail.com",
  "gender": "Male",
  "myImage": "file.jpg/png/bmp" //maks 5MB
  }
- Response 200 OK
  ```json
  {
    "error": false,
    "status": 201,
    "message": "User Created Successfully",
    "data": {
        "user_id": 9,
        "email": "example.johndoe@gmail.com",
        "name": "John Doe",
        "gender": "Male",
        "image_url": "https://storage.googleapis.com/caloriewise-bucket/images/profile/default.jpeg"
            }
    }
##### 3. Request With Input Image >5MB
- Request Body form-data
  ```json
  {
  "name": "John Doe",
  "password": "rahmat9872",
  "email": "example.johndoe@gmail.com",
  "gender": "Male",
  "myImage": "file.jpg/png/bmp" // >5mb
  }
- Response 413 Payload too Large
  ```json

  {
    "error": true,
    "message": "File too large. Maximum size is 5MB."
  }

##### 4. Request With Email Already Exists
- Request Body form-data
  ```json
  {
  "email": "example.johndoe@gmail.com",
  "password": "johndoe123",
  "name": "John Doe",
  "gender": "Male",
  "myImage": "file.jpg/png/bmp" //maks 5MB
  }
- Response 409 Conflict
  ```json

  {
    "status": 409,
    "error": true,
    "message": "Email already in use"
  }

##### 5. Request With only Image
- Request Body form-data
  ```json
  {
  "myImage": "file.jpg/png/bmp" //maks 5MB
  }
- Response 200 OK
  ```json

  {
    "status": 200,
    "error": false,
    "message": "Profile updated successfully",
    "data": {
        "user_id": 9,
        "image_url": "https://storage.googleapis.com/caloriewise-bucket/images/profile/2603e0df-e74c-4421-9e7b-9f51881f8142.jpeg"
    }
  }
## Views All Food
### URL
to see all Food : 
`/views/food`


### Method
GET

### Parameters
- `name` (string): Name of Food
- `calories` (float): Calories of food 
- `proteins` (float): Protein of food 
- `carbohydrate` (float): Carbohydrate of food 
- `fat` (float): Fats of food 

### Response
- 200 OK: Success
  ```json
  {
 
    "error": false,
    "message": "Success",
    "data_makanan": [
        {
            "food_id": 1,
            "name": "Abon",
            "calories": 280,
            "proteins": 9.2,
            "fat": 28.4,
            "carbohydrate": 0,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1.jpg"
        },
        {
            "food_id": 2,
            "name": "Abon haruwan",
            "calories": 513,
            "proteins": 23.7,
            "fat": 37,
            "carbohydrate": 21.3,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/2.jpg"
        },
        {
            "food_id": 3,
            "name": "Agar-agar",
            "calories": 0,
            "proteins": 0,
            "fat": 0.2,
            "carbohydrate": 0,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/3.jpg"
        },
        {
            "food_id": 4,
            "name": "Akar tonjong segar",
            "calories": 45,
            "proteins": 1.1,
            "fat": 0.4,
            "carbohydrate": 10.8,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/4.jpg"
        }]
    }
## Views by Catogeries
### URL

by Categories(name, calories, protein, carbs, fats)
`/views/food?name=nasi&calories=300&proteins=20&carbohydrate=30&fat=10` or 
 `/views/food?name=nasi` or 
 `/views/food?calories=300` or 
 `/views/food?proteins=300` or 
 `/views/food?name=nasi&calories=300&protein=20&carbohydrate=30&fat=10` 

you can customize the categories
### Method
GET

### Parameters
- `name` (string): Name of Food
- `calories` (float): Calories of food 
- `proteins` (float): Protein of food 
- `carbohydrate` (float): Carbohydrate of food 
- `fat` (float): Fats of food 

### Response
- 200 OK: Success
  ```json
  {
 
    "error": false,
    "message": "Success",
    "data_makanan": [
        {
            "food_id": 1,
            "name": "Abon",
            "calories": 280,
            "proteins": 9.2,
            "fat": 28.4,
            "carbohydrate": 0,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1.jpg"
        },
        {
            "food_id": 2,
            "name": "Abon haruwan",
            "calories": 513,
            "proteins": 23.7,
            "fat": 37,
            "carbohydrate": 21.3,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/2.jpg"
        },
        {
            "food_id": 3,
            "name": "Agar-agar",
            "calories": 0,
            "proteins": 0,
            "fat": 0.2,
            "carbohydrate": 0,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/3.jpg"
        },
        {
            "food_id": 4,
            "name": "Akar tonjong segar",
            "calories": 45,
            "proteins": 1.1,
            "fat": 0.4,
            "carbohydrate": 10.8,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/4.jpg"
        }]
    }
## View Food by food_id
### URL
`/views/food/{food_id}`


### Method
GET

### Parameters
- `name` (string): Name of Food
- `calories` (float): Calories of food 
- `proteins` (float): Protein of food 
- `carbohydrate` (float): Carbohydrate of food 
- `fat` (float): Fats of food 

### Response
URL
`/views/food/1`
- 200 OK: Success.
  ```json
  {
    "error": false,
    "message": "Success",
    "data": [
        {
            "food_id": 1,
            "name": "Abon",
            "calories": 280,
            "proteins": 9.2,
            "fat": 28.4,
            "carbohydrate": 0,
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1.jpg"
        }
    ]
  }




## Create Meal

### URL
`/create/meal`

### Method
POST

### Parameters
- `meal_title` (string): Title of Meal (required)
- `meal_date` (string): Date of Meal (required)
- `meal_time` (string): Time of Meal  (required)
- `meal_items` (array of objects): Detail makanan  (required)
  - `food_id` (integer): ID makanan  (required)
  - `quantity` (integer): Jumlah makanan  (required)

### Headers Authorization
Required (Bearer token)

### Request Body


- Raw body JSON
  ```json
  {
    "meal_title": "Makanan Siang Ku",
    "meal_date": "2024-06-03",
    "meal_time": "12:30:00",
    "meal_items": [
        {
            "food_id": 900,
            "quantity": 1
        },
        {
            "food_id": 3,
            "quantity": 1
        }
    ]
  }
## Response
- 201 Created: Meal entry created successfully.

  ```json
  {

    "status": 201,
    "error": false,
    "data": {
        "meal_id": 12,
        "userId": 9,
        "meal_title": "Makanan Siang Ku",
        "meal_date": "2024-06-03",
        "meal_time": "12:30:00",
        "meal_items": [
            {
                "food_id": 900,
                "name": "Mie pangsit basah",
                "quantity": 1,
                "meal_item_id": 31
            },
            {
                "food_id": 3,
                "name": "Agar-agar",
                "quantity": 1,
                "meal_item_id": 32
            }
        ]
    },
    "message": "Meal entry created successfully, meal item 31 and 32 created successfully"
  }

## Views Meals of User and by Categories of Retrivied User ID

### URL
- to see All Meal of User 
`/views/meal`

- by Categories
(food_name,food_id,meal_time,meal_date,calories,proteins,calories,carbohydrate,fat, older,newer)


### Method
GET

### Headers
Authorization: Bearer token

### Parameters

### Parameters
- `meal_title` (string): Title of Meal (required)
- `meal_date` (string): Date of Meal (required)
- `meal_time` (string): Time of Meal  (required)
- `meal_items` (array of objects): Detail makanan  (required)
  - `food_id` (integer): ID makanan  (required)
  - `quantity` (integer): Jumlah makanan  (required)
  - `food_name` (string): Name of the food (optional)
  - `time` (string): Time of the meal (optional)
  - `calories` (integer): Minimum calories (optional)
  - `proteins` (integer): Minimum protein (optional)
  - `carbohydrate` (integer): Minimum carbs (optional)
  - `fat` (integer): Minimum fats (optional)
  - `sort` (string): Sort order ('older' or 'newer') (optional)
### Response Body
#### 1. View All Meals
`/views/meal`
- 200 OK: Success.
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Success",
     "meal_details": [
        {
            "meal_id": 9,
            "meal_title": "Makanan Siang Ku",
            "user_id": 9,
            "user_name": "Rahmat",
            "meal_date": "2024-05-27T00:00:00.000Z",
            "meal_time": "12:30:00",
            "meal_items": [
                {
                    "food_id": 900,
                    "food_name": "Mie pangsit basah",
                    "quantity": 1,
                    "calories": 105,
                    "proteins": 5.9,
                    "carbohydrate": 9.4,
                    "fat": 4.9,
                    "total_calories": 105,
                    "total_proteins": 5.900000095367432,
                    "total_carbohydrate": 9.399999618530273,
                    "total_fats": 4.900000095367432
                },
                {
                    "food_id": 900,
                    "food_name": "Mie pangsit basah",
                    "quantity": 1,
                    "calories": 105,
                    "proteins": 5.9,
                    "carbohydrate": 9.4,
                    "fat": 4.9,
                    "total_calories": 105,
                    "total_proteins": 5.900000095367432,
                    "total_carbohydrate": 9.399999618530273,
                    "total_fats": 4.900000095367432
                }
        }]
  }

#### 2. View All Meals by Categories 

- by food name like
`/views/meal?food_name=mie`
200 OK: Success.
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Success",
    "meal_details": [
        {
            "meal_id": 13,
            "meal_title": "Makanan Siang Ku",
            "user_id": 9,
            "name": "Rahmat",
            "meal_date": "2024-06-03T00:00:00.000Z",
            "meal_time": "12:30:00",
            "meal_items": [
                {
                    "meal_items": 33,
                    "food_id": 900,
                    "food_name": "Mie pangsit basah",
                    "quantity": 2,
                    "calories": 105,
                    "proteins": 5.9,
                    "carbohydrate": 9.4,
                    "fat": 4.9,
                    "total_calories": 210,
                    "total_proteins": 11.800000190734863,
                    "total_carbohydrate": 18.799999237060547,
                    "total_fats": 9.800000190734863
                }
            ]
        },
        {
            "meal_id": 14,
            "title": "Makanan Siang Ku",
            "user_id": 9,
            "name": "Rahmat",
            "meal_date": "2024-06-05T00:00:00.000Z",
            "meal_time": "12:30:00",
            "meal_items": [
                {
                    "meal_items": 35,
                    "food_id": 900,
                    "food_name": "Mie pangsit basah",
                    "quantity": 2,
                    "calories": 105,
                    "proteins": 5.9,
                    "carbohydrate": 9.4,
                    "fat": 4.9,
                    "total_calories": 210,
                    "total_proteins": 11.800000190734863,
                    "total_carbohydrate": 18.799999237060547,
                    "total_fats": 9.800000190734863
                }
            ]
        }
    ]
  }

- by newer
`/views/meal?food_name=Mie&sort=newer`
200 OK: Success.
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Success",
    "meal_details": [
        {
            "meal_id": 14,
            "meal_title": "Makanan Siang Ku",
            "user_id": 9,
            "name": "Rahmat",
            "meal_date": "2024-06-05T00:00:00.000Z",
            "meal_time": "12:30:00",
            "meal_items": [
                {
                    "meal_items": 35,
                    "food_id": 900,
                    "food_name": "Mie pangsit basah",
                    "quantity": 2,
                    "calories": 105,
                    "proteins": 5.9,
                    "carbohydrate": 9.4,
                    "fat": 4.9,
                    "total_calories": 210,
                    "total_proteins": 11.800000190734863,
                    "total_carbohydrate": 18.799999237060547,
                    "total_fats": 9.800000190734863
                }
            ]
        },
        {
            "meal_id": 13,
            "title": "Makanan Siang Ku",
            "user_id": 9,
            "name": "Rahmat",
            "meal_date": "2024-06-03T00:00:00.000Z",
            "meal_time": "12:30:00",
            "meal_items": [
                {
                    "meal_items": 33,
                    "food_id": 900,
                    "food_name": "Mie pangsit basah",
                    "quantity": 2,
                    "calories": 105,
                    "proteins": 5.9,
                    "carbohydrate": 9.4,
                    "fat": 4.9,
                    "total_calories": 210,
                    "total_proteins": 11.800000190734863,
                    "total_carbohydrate": 18.799999237060547,
                    "total_fats": 9.800000190734863
                }
            ]
        }
    ]
  }

- by Date
`/views/meal?food_name=Mie&date=2024-06-05&sort=newer`
200 OK: Success.
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Success",
    "meal_details": [
        {
            "meal_id": 14,
            "meal_title": "Makanan Siang Ku",
            "user_id": 9,
            "name": "Rahmat",
            "meal_date": "2024-06-05T00:00:00.000Z",
            "meal_time": "12:30:00",
            "meal_items": [
                {
                    "meal_items": 35,
                    "food_id": 900,
                    "food_name": "Mie pangsit basah",
                    "quantity": 2,
                    "calories": 105,
                    "proteins": 5.9,
                    "carbohydrate": 9.4,
                    "fat": 4.9,
                    "total_calories": 210,
                    "total_proteins": 11.800000190734863,
                    "total_carbohydrate": 18.799999237060547,
                    "total_fats": 9.800000190734863
                }
            ]
        }
    ]
  }

- NOTE:  You can Actually can choose categories each one or all of that for food_id proteins, calories,carbohydrate, fat. just use
`/views/meal?date=2024-06-05&food_id=900&calories=17&proteins=1&carbohydrate=34&fat=9`

## Views Meal by Meal id of Retrivied User ID

### URL
to see All Meal of User
`/views/meal/{meal_id}`


### Method
GET

### Headers
Authorization: Bearer token

### Parameters
- `meal_title` (string): Title of Meal (required)
- `meal_date` (string): Date of Meal (required)
- `meal_time` (string): Time of Meal  (required)
- `meal_items` (array of objects): Detail makanan  (required)
  - `food_id` (integer): ID makanan  (required)
  - `quantity` (integer): Jumlah makanan  (required)
  - `food_name` (string): Name of the food (optional)
  - `time` (string): Time of the meal (optional)
  - `calories` (integer): Minimum calories (optional)
  - `proteins` (integer): Minimum protein (optional)
  - `carbohydrate` (integer): Minimum carbs (optional)
  - `fat` (integer): Minimum fats (optional)
  - `sort` (string): Sort order ('older' or 'newer') (optional)


### Response Body
#### 1. View All Meals
`/views/meal/13`
- 200 OK: Success.
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Success",
    "meal_details": {
        "meal_id": 13,
        "meal_title": "Makanan Siang Ku",
        "user_id": 9,
        "user_name": "Rahmat",
        "meal_date": "2024-06-03T00:00:00.000Z",
        "meal_time": "12:30:00",
        "meal_items": [
            {
                "food_id": 900,
                "food_name": "Mie pangsit basah",
                "quantity": 2,
                "calories": 105,
                "proteins": 5.9,
                "carbohydrate": 9.4,
                "fat": 4.9,
                "total_calories": 210,
                "total_proteins": 11.800000190734863,
                "total_carbohydrate": 18.799999237060547,
                "total_fats": 9.800000190734863
            },
            {
                "food_id": 3,
                "food_name": "Agar-agar",
                "quantity": 1,
                "calories": 0,
                "proteins": 0,
                "carbohydrate": 0,
                "fat": 0.2,
                "total_calories": 0,
                "total_proteins": 0,
                "total_carbohydrate": 0,
                "total_fats": 0.20000000298023224
            }
        ]
    }
  }
## Get Specific Meal Item by Meal Item Id of Retrivied User ID 

### URL
Get a specific meal item by its meal_item_id:
`/views/meal/item/{:meal_item_id}`

### Method
GET

### Headers
Authorization: Bearer token

### Parameters
- `meal_item_id` (integer): ID of the meal item to retrieve

### Response Body
`/views/meal/item/33`
#### 200 OK: Success

- Raw body JSON
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Success",
    "item_details": {
        "meal_item_id": 33,
        "meal_id": 13,
        "food_id": 900,
        "food_name": "Mie pangsit basah",
        "quantity": 2,
        "calories": 105,
        "proteins": 5.9,
        "carbohydrate": 9.4,
        "fat": 4.9,
        "total_calories": 210,
        "total_proteins": 11.800000190734863,
        "total_carbohydrate": 18.799999237060547,
        "total_fats": 9.800000190734863
    }
  }

## Update Meal by Meal_id of Retrivied User

### URL
`/update/meal{:meal_id}`

### Method
POST

### Parameters
- `title` (string): Title of Meal (required)
- `meal_date` (string): Date of Meal (required)
- `meal_time` (string): Time of Meal  (required)
- `meal_items` (array of objects): Detail makanan  (required)
  - `food_id` (integer): ID makanan  (required)
  - `quantity` (integer): Jumlah makanan  (required)

### Headers Authorization
Required (Bearer token)

### Request Body
`/update/meal/13`

- Raw body JSON
  ```json
  {
   "meal_title": "Makanan Siang Ku",
    "meal_date": "2024-06-27",
    "meal_time": "13:30:00",
    "meal_items": [
        {
            "food_id": 800,
            "quantity": 2
        },
        {
            "food_id": 5,
            "quantity": 2
        }
    ]
  }

### Response
- 200 OK: Meal entry updated successfully.

  ```json
  {
    "status": 200,
    "error": false,
    "message": "Meal entry updated successfully",
    "meal_updates": {
        "user_id": 9,
        "meal_id": "13",
        "meal_title": "Makanan Siang Ku",
        "meal_date": "2024-06-27",
        "meal_time": "13:30:00",
        "meal_items": [
            {
                "meal_item_id": 37,
                "food_id": 800,
                "food_name": "Kue lumpur",
                "quantity": 2
            },
            {
                "meal_item_id": 38,
                "food_id": 5,
                "food_name": "Aletoge segar",
                "quantity": 2
            }
        ]
    }
  }



## Delete Meal by meal_id of Retrieved User ID

### URL
`/delete/meal/{:meal_id}`

### Method
DELETE

### Headers
Authorization: Bearer token

### URL Parameter
meal_id (integer): The ID of the meal to be deleted.



### Response
- 200 OK: Meal entry DELETED successfully.

  ```json
  {
    "status": 200,
    "error": false,
    "userId": 9,
    "Name": "Rahmat",
    "message": "Meal entry deleted successfully"
  }
## Create Note of Retrieved User ID

### URL
`/create/note`

### Method
POST

### Parameters
- `title` (string): Title of the note (required)
- `content` (string): Content of the note (required)
- `myImage`(file): Image file to be uploaded (optional)


### Headers Authorization
Required (Bearer token)

### Request Body

#### 1. Without Input Image
`/create/note`
- Raw body JSON
  ```json
  {
    "title": "Catatan 221",
    "content": "Test"
  }

#### 2. With Input Image
`/create/note`
- Raw body JSON
  ```json
  {
    "title": "With Image",
    "content": "Test Create Note With Image"
    "myImage": "base64_encoded_image_data" // File Image(jpeg,png,jpg) max 5MB
  }


### Response Body

#### 1. Without Image
- 201 Created: Note created successfully.

  ```json
  {
    "status": 201,
    "error": false,
    "message": "Note created successfully",
    "note": {
        "user_id": 2,
        "note_id": 10,
        "title": "Catatan 221",
        "content": "Test",
        "image_url": "",
        "created_at": "2024-05-26T10:12:36.000Z",
        "updated_at": "2024-05-26T10:12:36.000Z"
    }
  }


#### 2. With Image
- 201 Created: Note created successfully.

  ```json
  {
    "status": 201,
    "error": false,
    "message": "Note created successfully",
    "note": {
        "user_id": 2,
        "note_id": 11,
        "title": "With Image",
        "content": "Test Create Note With Image",
        "image_url": "https://storage.googleapis.com/caloriewise-bucket/images/note/6b2d5dd4-0cef-4a67-a57d-487da8a0f696.png",
        "created_at": "2024-05-26T10:20:27.000Z",
        "updated_at": "2024-05-26T10:20:27.000Z"
    }
  }


## Views Notes of Retrieved User ID
 
### URL
To view all notes or details of a specific note: 
`/views/notes/`

### Method
GET

### Headers
Authorization: Bearer token

### Response Body
`/views/notes/`

- 200 OK: Success.
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Success",
    "notes": [
        {
            "note_id": 3,
            "user_id": 2,
            "name": "Kevin Mcalister",
            "title": "Reminder Meeting",
            "content": "Jangan lupa ada rapat tim pukul 14:00 hari ini. Persiapkan materi presentasi dengan baik.",
            "image_url": null,
            "created_at": "2024-05-26T01:00:00.000Z",
            "updated_at": "2024-05-26T01:00:00.000Z"
        },
        {
            "note_id": 4,
            "user_id": 2,
            "name": "Kevin Mcalister",
            "title": "Ide Proyek Baru",
            "content": "Saya punya ide bagus untuk proyek baru! Ayo diskusikan lebih lanjut dalam rapat nanti.",
            "image_url": null,
            "created_at": "2024-05-26T08:00:00.000Z",
            "updated_at": "2024-05-26T08:00:00.000Z"
        },
        {
            "note_id": 10,
            "user_id": 2,
            "name": "Kevin Mcalister",
            "title": "u8989",
            "content": "vbvbgffdffdfdgf",
            "image_url": "https://storage.googleapis.com/caloriewise-bucket/images/note/1053de10-a506-4693-8e58-5a39a087302a.png",
            "created_at": "2024-05-26T10:12:36.000Z",
            "updated_at": "2024-05-26T10:13:52.000Z"
        },
        {
            "note_id": 11,
            "user_id": 2,
            "name": "Kevin Mcalister",
            "title": "With Image",
            "content": "Test Create Note With Image",
            "image_url": "https://storage.googleapis.com/caloriewise-bucket/images/note/6b2d5dd4-0cef-4a67-a57d-487da8a0f696.png",
            "created_at": "2024-05-26T10:20:27.000Z",
            "updated_at": "2024-05-26T10:20:27.000Z"
        }
    ],
    "message": "Success"
  }

## Views Notes by Note_id of Retrieved User ID

### URL
To view all notes or details of a specific note: 
`/views/notes/{note_id}`

### Method
GET

### Headers
Authorization: Bearer token

### Parameters
- `note_id` (integer): ID of the note.

### Response Body
`/views/notes/3`
- 200 OK: Success.
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Success",
        "notes": [
        {
            "note_id": 3,
            "user_id": 2,
            "name": "Kevin Mcalister",
            "title": "Reminder Meeting",
            "content": "Jangan lupa ada rapat tim pukul 14:00 hari ini. Persiapkan materi presentasi dengan baik.",
            "image_url": null,
            "created_at": "2024-05-26T01:00:00.000Z",
            "updated_at": "2024-05-26T01:00:00.000Z"
        }
    ],
    "message": "Success"
  }

## Update Notes by Note_id of Retrieved user
### URL
`/update/note/{:note_id}`

### Method
PUT

### Headers Authorization
Required (Bearer token)

### Parameters
- `title` (string): Title of the note (required)
- `content` (string): Content of the note (required)
- `myImage`(file): Image file to be uploaded (optional)


#### Request Body and Response JSON
##### 1. Request Without Input Image
- Request Body 
  ```json
  {
  "title": "John Doe",
  "content": "rahmat9872"
  }
- Response 200 OK
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Note updated successfully",
    "data": {
        "note_id": "11",
        "user_id": 9,
        "title": "u8989",
        "content": "vbvbgffdffdfdgf",
        "image_url": "",
        "updated_at": "2024-06-01T02:02:58.431Z"
    }
  }
##### 2. Request With Input Image
- Request Body form-data
  ```json
  {
    {
    "title": "With Image",
    "content": "Test Create Note With Image",
    "myImage": "image.jpg", // File Image(jpeg,png,jpg) max 5MB
    }
  }
- Response 200 OK
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Note updated successfully",
    "data": {
        "note_id": "11",
        "user_id": 9,
        "title": "u8989",
        "content": "vbvbgffdffdfdgf",
        "image_url": "https://storage.googleapis.com/caloriewise-bucket/images/note/0704296c-4135-466f-8799-2e55e41a9943.jpeg",
        "updated_at": "2024-06-01T02:04:43.352Z"
    }
  }
##### 3. Request With Input Image >5MB
- Request Body form-data
  ```json
  {
    {
    "title": "With Image",
    "content": "Test Create Note With Image",
    "myImage": "image.jpg", // File Image(jpeg,png,jpg) > 5MB
    }
  }
- Response 413 Payload too Large
  ```json

  {
    "error": true,
    "message": "File too large. Maximum size is 5MB."
  }

## Delete Note by Note_ID of Retrieved User

### URL
`/delete/note/{:note_id}`

### Method
DELETE

### Headers
Authorization: Bearer token

### Parameters
- `note_id` (integer): ID of the note to delete

### Response Body
`/delete/note/3`
- 200 OK: Note deleted successfully.
  ```json
  {
    "status": 200,
    "error": false,
    "message": "Note deleted successfully",
    "data": {
        "user_id": 2
    }
  }
## Views History of Retrieved User
 To view History entries from today's date and previous dates
### URL
`/history/meals/`

### Method
GET

### Headers
Authorization: Bearer token

### Response Body
`/views/notes/`

- 200 OK
  ```json
  {
    "status": 200,
    "error": false,
    "userId": 1,
    "history": {
        "2024-05-28": {
            "summary": [
                {
                    "food_id": 4,
                    "food_name": "Akar tonjong segar",
                    "times_eaten": 7,
                    "total_calories": 315,
                    "total_proteins": 7.700000166893005,
                    "total_carbohydrate": 75.60000133514404,
                    "total_fats": 2.8000000417232513
                },
                {
                    "food_id": 14,
                    "food_name": "Andaliman segar",
                    "times_eaten": 4,
                    "total_calories": 396,
                    "total_proteins": 18.399999618530273,
                    "total_carbohydrate": 72,
                    "total_fats": 4
                }
            ]
        },
        "2024-05-27": {
            "summary": [
                {
                    "food_id": 2,
                    "food_name": "Abon haruwan",
                    "times_eaten": 2,
                    "total_calories": 1026,
                    "total_proteins": 47.400001525878906,
                    "total_carbohydrate": 42.599998474121094,
                    "total_fats": 74
                }
            ]
        }
    }
  }

## Views Bulking Food of Retrieved User

### URL
`/views/bulking`

### Method
GET

### Headers
Authorization: Bearer token

### Response Body
`/views/bulking/`
to view all Bulking Foods
- 200 OK
  ```json
  {
       "status": 200,
    "error": false,
    "data": [
        {
            "bulking_item_id": 1,
            "food_name": "Ayam Dada Lalapan",
            "description": "Ayam dada lalapan adalah hidangan khas Indonesia berupa dada ayam yang digoreng atau dibakar, disajikan dengan lalapan segar, sambal pedas, dan nasi putih. Nikmat dan segar untuk dinikmati.",
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/bulking/1.jpg"
        },
        {
            "bulking_item_id": 2,
            "food_name": "Pecel Lele",
            "description": "Pecel lele adalah hidangan khas Indonesia yang terdiri dari lele goreng renyah, disajikan dengan sambal pedas, lalapan segar seperti mentimun, kemangi, kol, dan nasi putih hangat. Lezat dan gurih!",
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/bulking/2.jpg"
        }
    ]
  }

`/views/bulking/{id}` to view bulking specific food by bulking id
`/views/bulking/1`
- 200 OK
  ```json
  {
    "status": 200,
    "error": false,
    "data": {
        "bulking_item_id": 1,
        "food_name": "Ayam Dada Lalapan",
        "description": "Ayam dada lalapan adalah hidangan khas Indonesia berupa dada ayam yang digoreng atau dibakar, disajikan dengan lalapan segar, sambal pedas, dan nasi putih. Nikmat dan segar untuk dinikmati.",
        "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/bulking/1.jpg",
        "bulking_item_details": [
            {
                "bulking_item_detail_id": 1,
                "food_id": 1351,
                "food_name": "Dada Ayam",
                "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1351.jpg",
                "calories": 164,
                "proteins": 31,
                "carbohydrate": 0,
                "fat": 3.6,
                "quantity": 1,
                "total_calories": 164,
                "total_proteins": 31,
                "total_carbohydrate": 0,
                "total_fat": 3.5999999046325684
            },
            {
                "bulking_item_detail_id": 2,
                "food_id": 921,
                "food_name": "Nasi",
                "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/921.jpg",
                "calories": 180,
                "proteins": 3,
                "carbohydrate": 39.8,
                "fat": 0.3,
                "quantity": 1,
                "total_calories": 180,
                "total_proteins": 3,
                "total_carbohydrate": 39.79999923706055,
                "total_fat": 0.30000001192092896
            },
            {
                "bulking_item_detail_id": 3,
                "food_id": 1354,
                "food_name": "Kol",
                "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1354.jpg",
                "calories": 22,
                "proteins": 1,
                "carbohydrate": 3.6,
                "fat": 0.1,
                "quantity": 1,
                "total_calories": 22,
                "total_proteins": 1,
                "total_carbohydrate": 3.5999999046325684,
                "total_fat": 0.10000000149011612
            },
            {
                "bulking_item_detail_id": 4,
                "food_id": 1353,
                "food_name": "Tempe",
                "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1244.jpg",
                "calories": 150,
                "proteins": 14,
                "carbohydrate": 9,
                "fat": 7.7,
                "quantity": 1,
                "total_calories": 150,
                "total_proteins": 14,
                "total_carbohydrate": 9,
                "total_fat": 7.699999809265137
            },
            {
                "bulking_item_detail_id": 5,
                "food_id": 1355,
                "food_name": "Timun",
                "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1355.jpg",
                "calories": 45,
                "proteins": 2,
                "carbohydrate": 11,
                "fat": 0,
                "quantity": 1,
                "total_calories": 45,
                "total_proteins": 2,
                "total_carbohydrate": 11,
                "total_fat": 0
            }
        ],
        "total_all_calories": 561,
        "total_all_proteins": 51,
        "total_all_carbohydrate": 63.399999141693115,
        "total_all_fat": 11.69999972730875
    }
  }
    

## Views Cutting Food of Retrieved User

### URL
`/views/cutting`

### Method
GET

### Headers
Authorization: Bearer token

### Response Body
`/views/cutting/`
to view all Cutting Foods
- 200 OK
  ```json
  {
    "status": 200,
    "error": false,
    "data": [
        {
            "cutting_item_id": 1,
            "food_name": "Salad Buah",
            "description": "Salad buah adalah kombinasi segar buah-buahan seperti pisang, apel, dan anggur, disajikan dengan yoghurt. Rasa manis alami dipadukan dengan kelembutan yoghurt, menciptakan hidangan menyegarkan yang lezat.",
            "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/cutting/1.jpg"
        }
    ]
  }

`/views/cutting/{id}` to view cutting specific food by cutting id
`/views/cutting/1`
- 200 OK
  ```json
  {
    "status": 200,
    "error": false,
    "data": {
        "cutting_item_id": 1,
        "food_name": "Salad Buah",
        "description": "Salad buah adalah kombinasi segar buah-buahan seperti pisang, apel, dan anggur, disajikan dengan yoghurt. Rasa manis alami dipadukan dengan kelembutan yoghurt, menciptakan hidangan menyegarkan yang lezat.",
        "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/cutting/1.jpg",
        "cutting_item_details": [
            {
                "cutting_item_detail_id": 1,
                "food_id": 1356,
                "food_name": "Pisang Cavendish",
                "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1356.jpg",
                "calories": 105,
                "proteins": 1.3,
                "carbohydrate": 27,
                "fat": 0.4,
                "quantity": 1,
                "total_calories": 105,
                "total_proteins": 1.2999999523162842,
                "total_carbohydrate": 27,
                "total_fat": 0.4000000059604645
            },
            {
                "cutting_item_detail_id": 2,
                "food_id": 19,
                "food_name": "Apel",
                "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/19.jpg",
                "calories": 58,
                "proteins": 0.3,
                "carbohydrate": 14.9,
                "fat": 0.4,
                "quantity": 1,
                "total_calories": 58,
                "total_proteins": 0.30000001192092896,
                "total_carbohydrate": 14.899999618530273,
                "total_fat": 0.4000000059604645
            },
            {
                "cutting_item_detail_id": 3,
                "food_id": 1357,
                "food_name": "Anggur",
                "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1357.jpg",
                "calories": 69,
                "proteins": 0.7,
                "carbohydrate": 18,
                "fat": 0.2,
                "quantity": 1,
                "total_calories": 69,
                "total_proteins": 0.699999988079071,
                "total_carbohydrate": 18,
                "total_fat": 0.20000000298023224
            },
            {
                "cutting_item_detail_id": 4,
                "food_id": 1346,
                "food_name": "Yoghurt",
                "image_url": "https://storage.googleapis.com/caloriewise-bucket-c241-ps066/images/foods/1346.jpg",
                "calories": 52,
                "proteins": 3.3,
                "carbohydrate": 4,
                "fat": 2.5,
                "quantity": 1,
                "total_calories": 52,
                "total_proteins": 3.299999952316284,
                "total_carbohydrate": 4,
                "total_fat": 2.5
            }
        ],
        "total_all_calories": 284,
        "total_all_proteins": 5.599999904632568,
        "total_all_carbohydrate": 63.89999961853027,
        "total_all_fat": 3.500000014901161
    }
  }
    

