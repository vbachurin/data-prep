'use strict';

var gulp = require('gulp');

var $ = require('gulp-load-plugins')();

var wiredep = require('wiredep');

function runTests(singleRun, done) {
    var bowerDeps = wiredep({
        directory: 'bower_components',
        exclude: ['bootstrap-sass-official'],
        dependencies: true,
        devDependencies: true
    });

    var testFiles = bowerDeps.js.concat([
        'src/*.js',
        'src/{app,components}/**/*.js',
        'src/{app,components}/**/*.html'
    ]);

    return gulp.src(testFiles)
        .pipe($.karma({
            configFile: 'karma.conf.js',
            action: (singleRun) ? 'run' : 'watch'
        }))
        .on('error', function (err) {
            // Make sure failed tests cause gulp to exit non-zero
            throw err;
        });
}

gulp.task('test', function (done) {
    runTests(true /* singleRun */, done)
});
gulp.task('test:auto', function (done) {
    runTests(false /* singleRun */, done)
});
