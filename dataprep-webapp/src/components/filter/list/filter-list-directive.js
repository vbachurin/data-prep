/**
 * @ngdoc directive
 * @name data-prep.filter-list.directive:FilterList
 * @description This directive display the filter list as badges. It consumes the filter list from {@link data-prep.services.filter.service:FilterService FilterService}
 * @restrict E
 * @usage
 * <filter-list filters="filters"
 *              on-filter-change="change(filter, value)"
 *              on-filter-remove="remove(filter)"></filter-list>
 * @param {array} filters The filters to display
 * @param {function} onFilterChange The change filter callback
 * @param {function} onFilterRemove The remove filter callback
 */
export default function FilterList() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/filter/list/filter-list.html',
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