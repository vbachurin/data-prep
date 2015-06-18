(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid-header.controller:DatagridHeaderCtrl
     * @description Dataset Column Header controller.
     * @requires data-prep.services.transformation.service:TransformationCacheService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function DatagridHeaderCtrl(TransformationCacheService, ConverterService) {
        var COLUMN_CATEGORY = 'columns';

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

    }

    angular.module('data-prep.datagrid-header')
        .controller('DatagridHeaderCtrl', DatagridHeaderCtrl);
})();
