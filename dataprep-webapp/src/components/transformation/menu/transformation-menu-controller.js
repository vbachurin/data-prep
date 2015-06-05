(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-menu.controller:TransformMenuCtrl
     * @description Transformation menu item controller.
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.transformation.service:TransformationService
     */
    function TransformMenuCtrl(PlaygroundService, PreparationService, TransformationService) {
        var vm = this;

        /**
         * @ngdoc method
         * @name initDynamicParams
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description [PRIVATE] Fetch the transformation dynamic parameters and inject them into transformation menu params
         * @returns {promise} The GET request promise
         */
        var initDynamicParams = function() {
            var infos = {
                columnId: vm.column.id,
                datasetId:  PlaygroundService.currentMetadata.id,
                preparationId:  PreparationService.currentPreparationId
            };
            return TransformationService.initDynamicParameters(vm.menu, infos);
        };

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

            if(vm.menu.dynamic) {
                vm.dynamicFetchInProgress = true;
                vm.showModal = true;

                //get new parameters
                initDynamicParams().finally(function() {
                    vm.dynamicFetchInProgress = false;
                });
            }
            else if (vm.menu.parameters || vm.menu.items) {
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
         * @param {object} params The transformation params
         * @description Perform a transformation on the column
         */
        vm.transform = function (params) {
            PlaygroundService.appendStep(vm.menu.name, vm.column, params)
                .then(function() {
                    vm.showModal = false;
                });
        };
    }

    angular.module('data-prep.transformation-menu')
        .controller('TransformMenuCtrl', TransformMenuCtrl);
})();