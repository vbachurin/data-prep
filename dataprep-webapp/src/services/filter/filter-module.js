(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.module:filter
     * @description This module contains the services to manage filters in the datagrid. It is responsible for the filter update within the SlickGrid grid
     * @requires data-prep.services.module:dataset
     */
    angular.module('data-prep.services.filter', [
        'data-prep.services.dataset'
    ]);
})();