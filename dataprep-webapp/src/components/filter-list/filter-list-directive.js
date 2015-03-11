(function() {
    'use strict';

    function FilterList() {
        return {
            restrict: 'E',
            templateUrl: 'components/filter-list/filter-list.html',
            scope: {
                metadata: '='
            },
            bindToController: true,
            controllerAs: 'filterCtrl',
            controller: 'FilterCtrl'
        };
    }

    angular.module('data-prep.filter-list')
        .directive('filterList', FilterList);
})();