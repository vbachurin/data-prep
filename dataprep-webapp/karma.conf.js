'use strict';

var path = require('path');
var conf = require('./gulp/conf');
var argv = require('yargs').argv;

var _ = require('lodash');
var wiredep = require('wiredep');
var babelMoreOptions = {presets: 'es2015'};

var pathSrcHtml = [
    path.join(conf.paths.src, '/**/*.html')
];

var pathSrcMock = [
    path.join(conf.paths.src, '/mocks/**/*.js')
];

var pathSrcSpec = [
    path.join(
        conf.paths.src,
        '/app/**/' + (argv.folder ? argv.folder + '/**/' : '') + '*.spec.js'
    )
];

function listFiles() {
    var wiredepOptions = _.extend({}, conf.wiredep, {
        dependencies: true,
        devDependencies: true
    });

    var patterns = wiredep(wiredepOptions).js
        .concat([
            './node_modules/phantomjs-polyfill/bind-polyfill.js',
            path.join(conf.paths.src, '/app/**/*-module.js'),
        ])
        .concat(pathSrcSpec)
        .concat(pathSrcHtml)
        .concat(pathSrcMock);

    var files = patterns.map(function (pattern) {
        return {
            pattern: pattern
        };
    });
    files.push({
        pattern: path.join(conf.paths.src, '/assets/**/*'),
        included: false,
        served: true,
        watched: false
    });
    return files;
}

module.exports = function (config) {

    var configuration = {
        files: listFiles(),

        singleRun: !argv.auto,

        autoWatch: !!argv.auto,

        ngHtml2JsPreprocessor: {
            stripPrefix: 'src/',
            moduleName: 'htmlTemplates'
        },

        logLevel: config.LOG_WARN,

        babelPreprocessor: {
            options: {
                sourceMap: 'inline'
            },
            filename: function (file) {
                return file.originalPath.replace(/\.js$/, '.es5.js');
            },
            sourceFileName: function (file) {
                return file.originalPath;
            }
        },

        frameworks: ['jasmine'],

        browsers: ['PhantomJS'],

        plugins: [
            'karma-babel-preprocessor',
            'karma-chrome-launcher',
            'karma-phantomjs-launcher',
            'karma-coverage',
            'karma-jasmine',
            'karma-ng-html2js-preprocessor',
            'karma-webpack',
            'karma-spec-reporter'
        ],

        //coverageReporter: {type: 'html', dir: 'coverage/'},
        coverageReporter: {
            type: 'html',
            dir: 'coverage/',

            // configure the reporter to use isparta for JavaScript coverage
            // Only on { "karma-coverage": "douglasduteil/karma-coverage#next" }
            instrumenters: {isparta: require('isparta')},
            instrumenter: {
                '**/*.js': 'isparta'
            },
            instrumenterOptions: {
                isparta: {babel: babelMoreOptions}
            }
        },

        reporters: ['progress', 'coverage'],

        preprocessors: {
            'src/**/*.html': ['ng-html2js'],
            'src/app/**/*-module.js': ['webpack'],
            'src/**/*.spec.js': ['webpack']
        },

        webpack: {
            // *optional* babel options: isparta will use it as well as babel-loader
            babel: {
                presets: ['es2015']
            },
            // *optional* isparta options: istanbul behind isparta will use it
            isparta: {
                embedSource: true,
                noAutoWrap: true,
                // these babel options will be passed only to isparta and not to babel-loader
                babel: {
                    presets: ['es2015']
                }
            },
            module: {
                preLoaders: [
                    // transpile all files except testing sources with babel as usual
                    {
                        test: /\.js$/,
                        exclude: [
                            'src/app/**/*.spec.js',
                            path.resolve('node_modules/')
                        ],
                        loader: 'isparta'
                    }
                ]
            },
            webpackMiddleware: {
                stats: {
                    chunks: false
                }
            }
        },

        proxies: {
            '/assets/': path.join('/base/', conf.paths.src, '/assets/')
        }
    };

    // This block is needed to execute Chrome on Travis
    // If you ever plan to use Chrome and Travis, you can keep it
    // If not, you can safely remove it
    // https://github.com/karma-runner/karma/issues/1144#issuecomment-53633076
    if (configuration.browsers[0] === 'Chrome' && process.env.TRAVIS) {
        configuration.customLaunchers = {
            'chrome-travis-ci': {
                base: 'Chrome',
                flags: ['--no-sandbox']
            }
        };
        configuration.browsers = ['chrome-travis-ci'];
    }

    config.set(configuration);
};