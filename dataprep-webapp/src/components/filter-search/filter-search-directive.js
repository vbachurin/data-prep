(function() {
    'use strict';

    function FilterSearch() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/filter-search/filter-search.html',
            bindToController: true,
            controllerAs: 'filterCtrl',
            controller: 'FilterSearchCtrl'
        };
    }

    angular.module('data-prep.filter-search')
        .directive('filterSearch', FilterSearch);
})();