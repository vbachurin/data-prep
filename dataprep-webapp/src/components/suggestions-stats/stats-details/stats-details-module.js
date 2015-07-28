(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.stats-details
     * @description This module contains the controller and directives for the statistics tabs
     * @requires talend.widget
     * @requires data-prep.services.transformation
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.stats-details', [
        'talend.widget',
        'data-prep.services.transformation',
        'data-prep.services.utils'
    ]);
})();