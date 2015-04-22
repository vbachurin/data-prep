(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.datagrid
     * @description This module contains the controller and directives for the datagrid
     * @requires data-prep.datagrid-tooltip
     * @requires data-prep.datagrid-header
     * @requires data-prep.services.dataset
     * @requires data-prep.services.filter
     */
    angular.module('data-prep.datagrid', [
        'data-prep.datagrid-tooltip',
        'data-prep.datagrid-header',
        'data-prep.services.dataset',
        'data-prep.services.filter'
    ]);
})();