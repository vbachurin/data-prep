'use strict';

var gulp = require('gulp');
var $ = require('gulp-load-plugins')();
var karma = require('karma');
var wiredep = require('wiredep');
var runSequence = require('run-sequence');
var path = require('path');

var fullTestsFiles = [
    'src/*.js',
    'src/{services,components}/**/*-module.js',
    'src/{services,components,mocks}/**/*.js',
    'src/components/**/*.html'
];

var pathSrcHtml = ['src/**/*.html'];

var filesToExclude = {
    services: [
        'src/components/**/*.spec.js'
    ],
    components: [
        'src/services/**/*.spec.js',
        'src/components/widgets/**/*.spec.js'
    ],
    widgets: [
        'src/services/**/*.spec.js',
        'src/components/!(widgets)/**/*.spec.js'
    ]
};


var filesToCover = {
    services: 'src/services/**/!(*spec|*mock).js',
    components: 'src/components/!(widgets)/**/!(*spec|*mock).js',
    widgets: 'src/components/widgets/**/!(*spec|*mock).js'
};

function runTests(singleRun, done, karmaConfPath, type) {
    var bowerDeps = wiredep({
        directory: 'bower_components',
        dependencies: true,
        devDependencies: true
    });

    var reporters = ['progress'];
    var preprocessors = {};

    pathSrcHtml.forEach(function (path) {
        preprocessors[path] = ['ng-html2js'];
    });

    if (singleRun) {
        var srcJs = type ? filesToCover [type] : 'src/**/!(*spec|*mock).js';
        preprocessors[srcJs] = ['coverage'];
        reporters.push('coverage')
    }

    var localConfig = {
        configFile: path.join(__dirname, '/../', karmaConfPath),
        singleRun: singleRun,
        autoWatch: !singleRun,
        reporters: reporters,
        preprocessors: preprocessors,
        files: bowerDeps.js.concat(fullTestsFiles),
        exclude: type ? filesToExclude[type] : []
    };

    var server = new karma.Server(localConfig, function (failCount) {
        done(failCount ? new Error("Failed " + failCount + " tests.") : null);
    });
    server.start();
}

gulp.task('test:components', function (done) {
    return runTests(true /* singleRun */, done, 'karma.conf.js', 'components')
});

gulp.task('test:services', function (done) {
    return runTests(true /* singleRun */, done, 'karma.conf.js', 'services')
});

gulp.task('test:widgets', function (done) {
    return runTests(true /* singleRun */, done, 'karma.conf.js', 'widgets')
});

gulp.task('test:parts', function(done) {
    return runSequence('test:services', 'test:components', 'test:widgets', done);
});

gulp.task('test', function (done) {
    runTests(true /* singleRun */, done, 'karma.conf.js')
});

gulp.task('test:auto', function (done) {
    runTests(false /* singleRun */, done, 'karma.conf.js')
});

gulp.task('test:ci', function (done) {
    runTests(true /* singleRun */, done, 'karma.conf.ci.js')
});
