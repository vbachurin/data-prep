(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.filter-list.directive:FilterList
     * @description This directive display the filter list as badges. It consumes the filter list from {@link data-prep.services.filter.service:FilterService FilterService}
     * @restrict E
     * @requires data-prep.services.filter.service:FilterService
     */
    function FilterList(FilterService, state) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/filter/list/filter-list.html',
            controllerAs: 'filterCtrl',
            controller: function() {
                this.filterService = FilterService;
                this.state = state;
                this.calculateLinesPercentage = function(){
                    return ((state.playground.shownLinesLength / state.playground.allLinesLength)*100).toFixed(0) + '%';
                };
            }
        };
    }

    angular.module('data-prep.filter-list')
        .directive('filterList', FilterList);
})();