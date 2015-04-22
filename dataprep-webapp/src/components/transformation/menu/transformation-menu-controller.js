(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-menu.controller:TransformMenuCtrl
     * @description Transformation menu item controller.
     * @requires data-prep.services.dataset.service:DatasetGridService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.recipe.service:RecipeService
     */
    function TransformMenuCtrl($rootScope, DatasetGridService, PreparationService, RecipeService) {
        var vm = this;

        /**
         * @ngdoc method
         * @name select
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description Menu selected. 3 choices :
         * <ul>
         *     <li>divider : no action</li>
         *     <li>no parameter and no choice is required : transformation call</li>
         *     <li>parameter or choice required : show modal</li>
         * </ul>
         */
        vm.select = function () {
            if (vm.menu.isDivider) {
                return;
            }

            if (vm.menu.parameters || vm.menu.items) {
                vm.showModal = true;
            }
            else {
                vm.transform();
            }
        };

        /**
         * @ngdoc method
         * @name transform
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @param {object} params - the transformation params
         * @description Perform a transformation on the column and refresh the recipe
         */
        vm.transform = function (params) {
            $rootScope.$emit('talend.loading.start');

            params = params || {};
            /*jshint camelcase: false */
            params.column_name = vm.column.id;

            PreparationService.append(vm.metadata.id, vm.menu.name, params)
                .then(function() {
                    return PreparationService.getContent('head');
                })
                .then(function(response) {
                    DatasetGridService.updateRecords(response.data.records);
                    RecipeService.refresh();
                    vm.showModal = false;
                })
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        };
    }

    angular.module('data-prep.transformation-menu')
        .controller('TransformMenuCtrl', TransformMenuCtrl);
})();