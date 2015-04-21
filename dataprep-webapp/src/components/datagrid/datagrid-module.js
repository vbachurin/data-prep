(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.module:datagrid
     * @description This module contains the controller and directives the datagrid
     * @requires data-prep.module:datagrid-tooltip
     * @requires data-prep.module:datagrid-header
     * @requires data-prep.services.module:dataset
     * @requires data-prep.services.module:filter
     */
    angular.module('data-prep.datagrid', [
        'data-prep.datagrid-tooltip',
        'data-prep.datagrid-header',
        'data-prep.services.dataset',
        'data-prep.services.filter'
    ]);
})();