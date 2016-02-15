'use strict';

var path = require('path');
var gulp = require('gulp');
var conf = require('./conf');

var karma = require('karma');

function runTests(singleRun, done, karmaConfPath) {

    var localConfig = {
        configFile: karmaConfPath || path.join(__dirname, '/../karma.conf.js'),
        singleRun: singleRun,
        autoWatch: !singleRun
    };

    var server = new karma.Server(localConfig, function (failCount) {
        done(failCount ? new Error("Failed " + failCount + " tests.") : null);
    });
    server.start();
}

gulp.task('test', function (done) {
    runTests(true, done);
});

gulp.task('test:auto', function (done) {
    runTests(false, done);
});

gulp.task('test:ci', function (done) {
    runTests(true, done, path.join(__dirname, '/../karma.conf.ci.js'));
});