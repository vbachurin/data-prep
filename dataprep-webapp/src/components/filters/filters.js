(function() {
  'use strict';

  /**
   * @ngdoc object
   * @name data-prep.filters
   * @description This module contains the filters
   * @requires angular-momentjs
   */
  angular.module('data-prep.filters', [
    'angular-momentjs'
  ])
  .filter('moment_from_now', function($moment) {
            return function(dateString,format) {
              return $moment(dateString,format?format:'x').fromNow();
            };
          });

})();