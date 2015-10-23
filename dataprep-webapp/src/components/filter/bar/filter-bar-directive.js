(function() {
    'use strict';

    function FilterBar() {
        return {
            restrict: 'E',
            templateUrl: 'components/filter/bar/filter-bar.html',
            scope: {},
            bindToController: true,
            controller: 'FilterBarCtrl',
            controllerAs: 'filterBarCtrl'
        };
    }

    angular.module('data-prep.filter-bar')
        .directive('filterBar', FilterBar);
})();