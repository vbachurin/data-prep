(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-menu.controller:TransformMenuCtrl
     * @description Transformation menu item controller.
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.transformation.service:TransformationService
     * @requires data-prep.services.utils.service:ConverterService
     * @requires data-prep.services.dataset.service:DatasetService
     */
    function TransformMenuCtrl(PlaygroundService, PreparationService, TransformationService, ConverterService, DatasetService,TypesService) {
        var vm = this;
        vm.converterService = ConverterService;
        vm.types = TypesService.types.then(function ( response ) {
                                                vm.types = response.data;
                                            });

        /**
         * @ngdoc method
         * @name initDynamicParams
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description [PRIVATE] Fetch the transformation dynamic parameters and inject them into transformation menu params
         * @returns {promise} The GET request promise
         */
        var initDynamicParams = function(menu) {
            var infos = {
                columnId: vm.column.id,
                datasetId:  PlaygroundService.currentMetadata.id,
                preparationId:  PreparationService.currentPreparationId
            };
            return TransformationService.initDynamicParameters(menu, infos);
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
        vm.select = function (menu, scope) {
            if(menu.dynamic) {
                vm.dynamicFetchInProgress = true;
                vm.showModal = true;
                vm.selectedMenu = menu;
                vm.selectedScope = scope;

                //get new parameters
                initDynamicParams(menu).finally(function() {
                    vm.dynamicFetchInProgress = false;
                });
            }
            else if (menu.parameters || menu.items) {
                vm.showModal = true;
                vm.selectedMenu = menu;
                vm.selectedScope = scope;
            }
            else {
                vm.transformClosure(menu, scope)();
            }
        };

        vm.transformClosure = function(menu, scope) {
            /*jshint camelcase: false */
            return function(params) {
                params = params || {};
                params.scope = scope;
                params.column_id = vm.column.id;
                params.column_name = vm.column.name;

                transform(menu, params);
            };
        };

        /**
         * @ngdoc method
         * @name changeType
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @param type the new domain/type of the column
         * @param typeLabel the new domain/type label of the column
         * @description change the domain/type for the column
         */
        vm.changeType = function(typeId,typeLabel,count){
          vm.column.domain = typeId;
          vm.column.domainLabel = typeLabel;
          vm.column.domainCount = count;
          DatasetService.updateColumn(PlaygroundService.currentMetadata.id, vm.column);
        };

        vm.displayPercentage = function(semanticDomain){
            return semanticDomain.frequency >= 100 ? semanticDomain.frequency.toPrecision(3) : semanticDomain.frequency.toPrecision(2);
        };

        /**
         * @ngdoc method
         * @name transform
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @param {object} params The transformation params
         * @description Perform a transformation on the column
         */
        function transform(menu, params) {
            PlaygroundService.appendStep(menu.name, params)
                .then(function() {
                    vm.showModal = false;
                });
        }
    }

    angular.module('data-prep.transformation-menu')
        .controller('TransformMenuCtrl', TransformMenuCtrl);
})();