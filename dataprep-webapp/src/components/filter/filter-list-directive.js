(function() {
    'use strict';

    function FilterList() {
        return {
            restrict: 'E',
            templateUrl: 'components/filter/filter-list.html',
            scope: {
                metadata: '='
            },
            bindToController: true,
            controllerAs: 'filterCtrl',
            controller: 'FilterCtrl'
        };
    }

    angular.module('data-prep-filter')
        .directive('filterList', FilterList);
})();