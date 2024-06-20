const express = require('express');
const bodyParser = require('body-parser');
const routes = require('./routes');

const app = express();
const port = process.env.PORT || 4000;

// Middleware
app.use(bodyParser.json());

// Routes
app.use('/', routes);

// Start server
app.listen(port, () => {
    console.log(`Server running on port ${port}`);
});