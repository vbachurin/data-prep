(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid-header.controller:DatagridHeaderCtrl
     * @description Dataset Column Header controller.
     * @requires data-prep.services.transformation.service:TransformationCacheService
     * @requires data-prep.services.utils.service:ConverterService
     * @requires data-prep.services.filter.service:FilterService
     */
    function DatagridHeaderCtrl(TransformationCacheService, ConverterService, FilterService) {
        var vm = this;
        vm.converterService = ConverterService;

        /**
         * @ngdoc method
         * @name initTransformations
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description Get transformations from REST call
         */
        vm.initTransformations = function () {
            if (!vm.transformations && !vm.initTransformationsInProgress) {
                vm.transformationsRetrieveError = false;
                vm.initTransformationsInProgress = true;

                TransformationCacheService.getTransformations(vm.column)
                    .then(function(menus) {
                        vm.transformations = _.filter(menus, function(menu) {
                            return menu.category === COLUMN_CATEGORY;
                        });
                    })
                    .catch(function() {
                        vm.transformationsRetrieveError = true;
                    })
                    .finally(function() {
                        vm.initTransformationsInProgress = false;
                    });
            }
        };

        /**
         * @ngdoc method
         * @name filterInvalidRecords
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description Create a filter for invalid records on the given column.
         * @param {object} column - the column to filter
         */
        vm.filterInvalidRecords = function(column) {
            FilterService.addFilter('invalid_records', column.id, column.name, {values: column.quality.invalidValues});
        };

        /**
         * @ngdoc method
         * @name filterEmptyRecords
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description Create a filter for empty records on the given column.
         * @param {object} column - the column to filter
         */
        vm.filterEmptyRecords = function(column) {
            FilterService.addFilter('empty_records', column.id, column.name, {});
        };

    }

    angular.module('data-prep.datagrid-header')
        .controller('DatagridHeaderCtrl', DatagridHeaderCtrl);
})();
