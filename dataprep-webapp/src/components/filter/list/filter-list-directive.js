/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.filter-list.directive:FilterList
     * @description This directive display the filter list as badges. It consumes the filter list from {@link data-prep.services.filter.service:FilterService FilterService}
     * @restrict E
     * @requires data-prep.services.filter.service:FilterService
     */
    function FilterList() {
        return {
            restrict: 'E',
            templateUrl: 'components/filter/list/filter-list.html',
            scope: {
                filters: '=',
                onFilterChange: '&',
                onFilterRemove: '&'
            },
            bindToController: true,
            controllerAs: 'filterCtrl',
            controller: 'FilterListCtrl'
        };
    }

    angular.module('data-prep.filter-list')
        .directive('filterList', FilterList);
})();