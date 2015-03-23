(function() {
    'use strict';

    function FilterListCtrl(FilterService) {
        var vm = this;
        vm.filterService = FilterService;

        /**
         * Delete a filter
         * @param filter
         */
        vm.delete = FilterService.removeFilter;

        /**
         * Update a filter
         * @param filter
         * @param newValue
         */
        vm.update = FilterService.updateFilter;
    }

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