'use strict';

var gulp = require('gulp');
var jsdoc = require("gulp-jsdoc");
var gulpDocs = require('gulp-ngdocs');

gulp.task('ngdoc', function () {
    var options = {
        html5Mode: false,
        title: "Data-prep webapp",
        titleLink: "/api"
    };

    return gulp.src([
            'src/**/*.js',
            '!src/**/*.spec.js'
        ])
        .pipe(gulpDocs.process(options))
        .pipe(gulp.dest('./ngdoc'));
});