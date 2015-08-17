(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.filter-list
     * @description This module contains the controller and directives to manage the filter list
     * @requires talend.widget
     * @requires data-prep.filter-search
     * @requires data-prep.services.filter
     * @requires data-prep.services.statistics
     */
    angular.module('data-prep.filter-list', [
        'talend.widget',
        'data-prep.filter-search',
        'data-prep.services.filter',
        'data-prep.services.statistics'
    ]);
})();