import FilterListCtrl from './filter-list-controller';
import FilterList from './filter-list-directive';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.filter-list
     * @description This module contains the controller and directives to manage the filter list
     * @requires talend.widget
     */
    angular.module('data-prep.filter-list', ['talend.widget'])
        .controller('FilterListCtrl', FilterListCtrl)
        .directive('filterList', FilterList);
})();