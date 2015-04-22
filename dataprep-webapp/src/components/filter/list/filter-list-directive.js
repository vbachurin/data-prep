(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.filter-list.directive:FilterList
     * @description This directive display the filter list as badges. It consumes the filter list from {@link data-prep.services.filter.service:FilterService FilterService}
     * @restrict E
     */
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