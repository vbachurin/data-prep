(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid-header.controller:DatagridHeaderCtrl
     * @description Dataset Column Header controller.
     * @requires data-prep.services.transformation.service:TransformationCacheService
     * @requires data-prep.services.utils.service:ConverterService
     * @requires data-prep.services.playground.service:PlaygroundService
     */
    function DatagridHeaderCtrl(TransformationCacheService, ConverterService, PlaygroundService) {
        var COLUMN_CATEGORY = 'columns';

        var vm = this;
        vm.converterService = ConverterService;

        vm.newName = vm.column.name;
        vm.oldName = vm.newName;
        vm.isEditMode = false;
        vm.updateEnabled = false;
        vm.RENAME_ACTION = 'rename_column';

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
         * @name updateColumnName
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description update the new column name
         */
        vm.updateColumnName = function (col) {

            var params = {};
            var paramName = 'new_column_name';
            params[paramName] = vm.newName;

            PlaygroundService.appendStep(vm.RENAME_ACTION, col, params)
                .then(function() {
                    vm.setEditMode(false);
                    vm.updateEnabled = false;
                    vm.oldName = vm.newName;
                });
        };


        /**
         * @ngdoc method
         * @name enableUpdate
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description Called when the column name is changed
         */
        vm.canUpdate = function () {
            if(vm.oldName !== vm.newName && vm.newName !== '') {
                return true;
            } else {
                vm.resetColumnName();
                return false;
            }

        };


        /**
         * @ngdoc method
         * @name setEditMode
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description set isEditMode value
         */
        vm.setEditMode = function (bool) {

            vm.isEditMode = bool;

            //reinitialization when no save preparation
            if (bool) {
                vm.newName = vm.column.name;
                vm.oldName = vm.newName;
            }

        };


        /**
         * @ngdoc method
         * @name resetColumnName
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description reset newName
         */
        vm.resetColumnName = function () {
            vm.newName = vm.oldName;
        };


    }

    angular.module('data-prep.datagrid-header')
        .controller('DatagridHeaderCtrl', DatagridHeaderCtrl);
})();
