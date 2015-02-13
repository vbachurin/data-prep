'use strict';

module.exports = function(config) {

    config.set({
        autoWatch : false,
        frameworks: ['jasmine'],
        browsers : ['PhantomJS'],

        plugins : [
            'karma-phantomjs-launcher',
            'karma-jasmine',
            'karma-coverage',
            'karma-ng-html2js-preprocessor',
            'karma-junit-reporter'//junit is used in jenkins tests but not launch is dev tests
        ],
        reporters: ['progress', 'coverage'],
        preprocessors: {
            // source files, that you wanna generate coverage for
            // do not include tests or libraries
            // (these files will be instrumented by Istanbul)
            'src/**/!(*spec|*mock).js': ['coverage'],
            'src/**/*.html':['ng-html2js']
        },
        ngHtml2JsPreprocessor: {
            stripPrefix: 'src/',
            moduleName: 'htmlTemplates'
        },
        junitReporter    : {
            outputFile : 'target/surefire-reports/TEST-karma-results.xml'
        }
    });
};