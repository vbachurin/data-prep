'use strict';

module.exports = function(config) {

    config.set({
        autoWatch : false,

        frameworks: ['jasmine'],

        browsers : ['PhantomJS'],

        plugins : [
            'karma-phantomjs-launcher',
            'karma-jasmine',
            'karma-coverage'
        ],

        reporters: ['progress', 'coverage'],

        preprocessors: {
            // source files, that you wanna generate coverage for
            // do not include tests or libraries
            // (these files will be instrumented by Istanbul)
            'src/**/!(*spec|*mock).js': ['coverage']
            //'www/templates/**/*.html':['ng-html2js']
        }
    });
};