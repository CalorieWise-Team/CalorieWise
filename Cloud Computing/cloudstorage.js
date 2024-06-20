const { Storage } = require('@google-cloud/storage');
const multer = require('multer');
const path = require('path');
const uuid = require('uuid');

const storageClient = new Storage({
    projectId: 'caloriewise-425017',
    credentials: require('./ServiceAccount.json')
});

const bucketName = ''; 

const storage = multer.memoryStorage();

const upload = multer({
    storage: storage,
    limits: { fileSize: 5 * 1024 * 1024 }, // 5MB limit
    fileFilter: (req, file, cb) => {
        const allowedTypes = ['image/jpeg', 'image/png', 'image/jpg'];
        if (!allowedTypes.includes(file.mimetype)) {
            const error = new Error('Only JPG, JPEG, and PNG files are allowed');
            error.code = 'UNSUPPORTED_MEDIA_TYPE';
            return cb(error, false);
        }
        cb(null, true);
    }
});

const handleMulterError = (err, req, res, next) => {
    if (err) {
        if (err.code === 'LIMIT_FILE_SIZE') {
            return res.status(413).json({ error: true, message: 'File too large. Maximum size is 5MB.' });
        } else if (err.code === 'UNSUPPORTED_MEDIA_TYPE') {
            return res.status(415).json({ error: true, message: 'Only JPG, JPEG, and PNG files are allowed.' });
        } else {
            return res.status(500).json({ error: true, message: 'Internal Server Error' });
        }
    }
    next();
};

const uploadSingle = (fieldName) => upload.single(fieldName);

const uploadMultiple = (fieldName, count) => upload.array(fieldName, count);

module.exports = {
    storageClient, bucketName, upload, uploadSingle, uploadMultiple, handleMulterError
};
