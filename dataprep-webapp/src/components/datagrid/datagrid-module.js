(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.datagrid
     * @description This module contains the controller and directives for the datagrid
     * @requires data-prep.datagrid-tooltip
     * @requires data-prep.datagrid-header
     * @requires data-prep.services.dataset
     * @requires data-prep.services.preparation
     * @requires data-prep.services.filter
     * @requires data-prep.services.playground
     * @requires 'data-prep.services.statistics'
     */
    angular.module('data-prep.datagrid', [
        'data-prep.datagrid-tooltip',
        'data-prep.datagrid-header',
        'data-prep.services.dataset',
        'data-prep.services.preparation',
        'data-prep.services.filter',
        'data-prep.services.playground',
        'data-prep.services.statistics',
        'data-prep.services.utils'
    ]);
})();