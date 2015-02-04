'use strict';

var gulp = require('gulp');

gulp.task('watch', ['consolidate', 'wiredep', 'injector:css', 'injector:js'] ,function () {
  gulp.watch('src/**/*.scss', ['injector:css']);
  gulp.watch('src/**/*.js', ['injector:js']);
  gulp.watch('src/assets/images/**/*', ['images']);
  gulp.watch('bower.json', ['wiredep']);
  gulp.watch('src/**/*.jade', ['consolidate:jade']);
});
