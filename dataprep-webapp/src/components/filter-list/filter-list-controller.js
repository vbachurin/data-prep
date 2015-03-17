(function() {
    'use strict';

    function FilterListCtrl(FilterService) {
        var vm = this;
        vm.filterService = FilterService;

        vm.delete = function(filter) {
            FilterService.removeFilter(filter);
        };
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