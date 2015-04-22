(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.filter-list.controller:FilterListCtrl
     * @description Filter list controller.
     */
    function FilterListCtrl(FilterService) {
        var vm = this;
        vm.filterService = FilterService;

        /**
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.filter-list.controller:FilterListCtrl
         * @param {object} filter - the filter to delete
         * @description Delete a filter by calling {@link data-prep.services.filter.service:FilterService FilterService}
         */
        vm.delete = FilterService.removeFilter;

        /**
         * @ngdoc method
         * @name update
         * @methodOf data-prep.filter-list.controller:FilterListCtrl
         * @param {object} oldFilter - the filter to update
         * @param {object} newValue - the filter update parameters
         * @description Update an existing filter by calling {@link data-prep.services.filter.service:FilterService FilterService}
         */
        vm.update = FilterService.updateFilter;
    }

    /**
     * @ngdoc property
     * @name filters
     * @propertyOf data-prep.filter-list.controller:FilterListCtrl
     * @description The filter list from {@link data-prep.services.filter.service:FilterService FilterService}
     */
    Object.defineProperty(FilterListCtrl.prototype,
        'filters', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.filterService.filters;
            }
        });

    angular.module('data-prep.filter-list')
        .controller('FilterListCtrl', FilterListCtrl);
})();