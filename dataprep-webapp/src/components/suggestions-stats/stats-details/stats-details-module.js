(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.stats-details
     * @description This module contains the controller and directives for the statistics tabs
     * @requires talend.widget
     * @requires data-prep.services.statistics
     * @requires data-prep.services.utils
     * @requires data-prep.services.state
     */
    angular.module('data-prep.stats-details', [
        'talend.widget',
        'data-prep.services.statistics',
        'data-prep.services.utils',
        'data-prep.services.state'
    ]);
})();