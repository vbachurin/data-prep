'use strict';

var gulp = require('gulp');

var util = require('util');

var browserSync = require('browser-sync');

var middleware = require('./proxy');

function browserSyncInit(baseDir, files, browser) {
  browser = browser === undefined ? 'default' : browser;

  var routes = null;
  if(baseDir === 'src' || (util.isArray(baseDir) && baseDir.indexOf('src') !== -1)) {
    routes = {
      '/bower_components': 'bower_components'
    };
  }

  browserSync.instance = browserSync.init(files, {
    startPath: '/',
    server: {
      baseDir: baseDir,
      middleware: middleware,
      routes: routes
    },
    browser: browser
  });

}

//serve the app for dev, it uses the assets/config/config.mine.json prioritarily.
gulp.task('serve', ['clean'], function () {
  gulp.start('copy-personnal-files');
  gulp.start('watch');
  browserSyncInit([
    '.tmp',
    'src'
  ], [
    '.tmp/**/*.css',
    'src/**/*.js',
    'src/assets/config/**/*',
    'src/assets/images/**/*',
    '.tmp/*.html',
    '.tmp/**/*.html',
    'src/**/*.html'
  ]);
});

//serve the app for dev, ignoring the file assets/config/config.mine.json
gulp.task('serve:default', ['clean'], function () {
  gulp.start('watch');
  browserSyncInit([
    '.tmp',
    'src'
  ], [
    '.tmp/**/*.css',
    'src/**/*.js',
    'src/assets/config/**/*',
    'src/assets/images/**/*',
    '.tmp/*.html',
    '.tmp/**/*.html',
    'src/**/*.html'
  ]);
});


gulp.task('serve:dist', ['build'], function () {
  browserSyncInit('dist');
});

gulp.task('serve:e2e', ['wiredep', 'injector:js', 'injector:css'], function () {
  browserSyncInit(['.tmp', 'src'], null, []);
});

gulp.task('serve:e2e-dist', ['build'], function () {
  browserSyncInit('dist', null, []);
});
