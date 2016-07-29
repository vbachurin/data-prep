module.exports = require('./webpack.config.js')({
    env: 'prod',
    devServer: true,
    minify: true,
    stripComments: true,
});
