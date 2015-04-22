(function() {
    'use strict';

    function FilterList() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/filter/list/filter-list.html',
            bindToController: true,
            controllerAs: 'filterCtrl',
            controller: 'FilterListCtrl'
        };
    }

    angular.module('data-prep.filter-list')
        .directive('filterList', FilterList);
})();