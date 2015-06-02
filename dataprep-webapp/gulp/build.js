'use strict';

var gulp = require('gulp');
var sass = require('gulp-ruby-sass');

var $ = require('gulp-load-plugins')({
  pattern: ['gulp-*', 'main-bower-files', 'uglify-save-license', 'del']
});

gulp.task('styles', ['wiredep'], function () {
  return (sass('src/css/', {style: 'expanded'}))
    .on('error', function handleError(err) {
      console.error(err.toString());
      this.emit('end');
    })
    .pipe($.autoprefixer())
    .pipe(gulp.dest('.tmp/'));
});

gulp.task('injector:css', ['styles'], function () {
  return gulp.src('src/index.html')
    .pipe($.inject(gulp.src([
        '.tmp/**/*.css'
      ], {read: false}), {
      ignorePath: '.tmp',
      addRootSlash: false
    }))
    .pipe(gulp.dest('src/'));
});

gulp.task('scripts', function () {
  return gulp.src(['src/**/*.js', '!src/assets/maps/**'])
    .pipe($.jshint())
    .pipe($.jshint.reporter('jshint-stylish'));
});

gulp.task('injector:js', ['scripts', 'injector:css'], function () {
  return gulp.src(['src/index.html', '.tmp/index.html'])
    .pipe($.inject(gulp.src([
		  'src/**/*.js',
		  '!src/**/*.spec.js',
		  '!src/**/*_test.js',
		  '!src/**/*.mock.js',
		  '!src/lib/**/*.*'
		])
        .pipe($.naturalSort())//This fixes a angularFileSort issue : https://github.com/klei/gulp-angular-filesort/issues/17
		.pipe($.angularFilesort())
          , {
		  ignorePath: 'src',
		  addRootSlash: false
		}))
    .pipe(gulp.dest('src/'));
});

gulp.task('partials', function () {
  return gulp.src(['src/**/*.html', '.tmp/**/*.html'])
    .pipe($.minifyHtml({
      empty: true,
      spare: true,
      quotes: true
    }))
    .pipe($.angularTemplatecache('templateCacheHtml.js', {
      module: 'data-prep'
    }))
    .pipe(gulp.dest('.tmp/inject/'));
});

gulp.task('html', ['wiredep', 'injector:css', 'injector:js', 'partials'], function () {
  var htmlFilter = $.filter('*.html');
  var jsFilter = $.filter('**/*.js');
  var cssFilter = $.filter('**/*.css');
  var assets;

  return gulp.src(['src/*.html', '.tmp/*.html'])
    .pipe($.inject(gulp.src('.tmp/inject/templateCacheHtml.js', {read: false}), {
      starttag: '<!-- inject:partials -->',
      ignorePath: '.tmp',
      addRootSlash: false
    }))
    .pipe(assets = $.useref.assets())
    .pipe($.rev())
    .pipe(jsFilter)
    .pipe($.ngAnnotate())
    .pipe($.uglify({preserveComments: $.uglifySaveLicense}))
    .pipe(jsFilter.restore())
    .pipe(cssFilter)
    .pipe($.csso())
    .pipe(cssFilter.restore())
    .pipe(assets.restore())
    .pipe($.useref())
    .pipe($.revReplace())
    .pipe(htmlFilter)
    .pipe($.minifyHtml({
      empty: true,
      spare: true,
      quotes: true
    }))
    .pipe(htmlFilter.restore())
    .pipe(gulp.dest('dist/'))
    .pipe($.size({ title: 'dist/', showFiles: true }));
});

gulp.task('images', function () {
  return gulp.src('src/**/*.png')
    .pipe($.imagemin({
      optimizationLevel: 3,
      progressive: true,
      interlaced: true
    }))
    .pipe(gulp.dest('dist/'));
});

gulp.task('customFonts', function () {
    return gulp.src('src/**/*.{eot,svg,ttf,woff}')
        .pipe(gulp.dest('dist/'));
});

gulp.task('fonts', function () {
  return gulp.src($.mainBowerFiles())
    .pipe($.filter('**/*.{eot,svg,ttf,woff}'))
    .pipe($.flatten())
    .pipe(gulp.dest('dist/fonts/'));
});

gulp.task('misc', function () {
  return gulp.src(['src/**/*.ico','src/**/*.json'])
    .pipe(gulp.dest('dist/'));
});

gulp.task('clean', function (done) {
  $.del(['dist/', '.tmp/', 'dev/'], done);
});

gulp.task('build', ['clean'], function (){
  gulp.start(['html', 'images', 'fonts', 'customFonts', 'misc']);
});

gulp.task('build:dev', ['clean'], function(){
  gulp.stat(['injector:css', 'injector:js']);
  return gulp.src([
    'src/**/*.*',
    '.tmp/**/*.*',
    '!src/**/*.scss',
    '!src/**/*.jade'
  ]).pipe(gulp.dest('dev/'));
});
