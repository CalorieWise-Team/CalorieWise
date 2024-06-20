const mysql = require('mysql');
// Database connection
const config = {
    secret: '',
    host: '0.0.0.0',
    user: 'root',
    password: '',
    database: 'caloriewise'

};

const db = mysql.createConnection({
    host: config.host,
    user: config.user,
    password: config.password,
    database: config.database,
    multipleStatements: true
});

db.connect(err => {
    if (err) throw err;
    console.log('Connected to CloudSQL Database');
});
module.exports = db;
module.exports = {
    db,
    config
};
