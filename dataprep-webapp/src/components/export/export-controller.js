(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.export.controller:ExportCtrl
     * @description Export controller.
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.utils.service:RestURLs
     * @requires data-prep.services.export.service:ExportService
     */
    function ExportCtrl($timeout, state, PreparationService, RecipeService, RestURLs, ExportService) {
        var vm = this;
        vm.state = state;
        vm.preparationService = PreparationService;
        vm.recipeService = RecipeService;
        vm.exportService = ExportService;

        /**
         * @ngdoc property
         * @name currentExportType
         * @propertyOf data-prep.export.controller:ExportCtrl
         * @description Current export type
         * @type {Object}
         */
        vm.currentExportType = null;

        /**
         * @ngdoc property
         * @name currentExportParameters
         * @propertyOf data-prep.export.controller:ExportCtrl
         * @description Current export parameters model, bound to form inputs
         * @type {Object}
         */
        vm.currentExportParameters = null;

        /**
         * @ngdoc method
         * @name resetCurrentParameters
         * @methodOf data-prep.export.controller:ExportCtrl
         * @description Reset current export parameters with the saved one from localStorage
         */
        vm.resetCurrentParameters = function resetCurrentParameters() {
            vm.currentExportParameters = ExportService.getParameters();
            vm.currentExportType = ExportService.getType(vm.currentExportParameters.exportType);
        };

        /**
         * @ngdoc method
         * @name changeTypeAndExport
         * @methodOf data-prep.export.controller:ExportCtrl
         * @param {object} exportType The export type
         * @description Change the export type and launch the export.
         * If the export type has parameters, we init the form and display a modal
         */
        vm.changeTypeAndExport = function (exportType) {
            var parameters = {exportType: exportType.id};
            vm.currentExportType = exportType;
            vm.currentExportParameters = parameters;
            
            if (exportType.parameters) {
                _.forEach(exportType.parameters, function(param) {
                    parameters['exportParameters.' + param.name] = param.defaultValue.value;
                });
                vm.showModal = true;
            }
            else {
                ExportService.setParameters(parameters);
                $timeout(vm.export);
            }
        };

        /**
         * @ngdoc method
         * @name saveEditionAndExport
         * @methodOf data-prep.export.controller:ExportCtrl
         * @description Save the currently edited export parameters and launch the export
         */
        vm.saveEditionAndExport = function saveEditionAndExport() {
            ExportService.setParameters(vm.currentExportParameters);
            $timeout(vm.export);
        };

        /**
         * @ngdoc method
         * @name export
         * @methodOf data-prep.export.controller:ExportCtrl
         * @description Launch the export
         */
        vm.export = function () {
            vm.form.action = RestURLs.exportUrl;
            vm.form.submit();
        };

        // get types list
        ExportService.refreshTypes()
            .then(vm.resetCurrentParameters);
    }

    /**
     * @ngdoc property
     * @name stepId
     * @propertyOf data-prep.export.controller:ExportCtrl
     * @description The current stepId
     * It is bound to {@link data-prep.services.recipe.service:RecipeService RecipeService}.getLastActiveStep()
     */
    Object.defineProperty(ExportCtrl.prototype,
        'stepId', {
            enumerable: true,
            configurable: false,
            get: function () {
                var step = this.recipeService.getLastActiveStep();
                return step ? step.transformation.stepId : '';
            }
        });

    /**
     * @ngdoc property
     * @name datasetId
     * @propertyOf data-prep.export.controller:ExportCtrl
     * @description The current dataset id
     * It is bound to {@link data-prep.services.state.constant:state state} property
     */
    Object.defineProperty(ExportCtrl.prototype,
        'datasetId', {
            enumerable: true,
            configurable: false,
            get: function () {
                var dataset = this.state.playground.dataset;
                return dataset ? dataset.id : '';
            }
        });

    /**
     * @ngdoc property
     * @name exportTypes
     * @propertyOf data-prep.export.controller:ExportCtrl
     * @description The export types list
     * It is bound to {@link data-prep.services.export.service:ExportService ExportService} property
     */
    Object.defineProperty(ExportCtrl.prototype,
        'exportTypes', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.exportService.exportTypes;
            }
        });

    angular.module('data-prep.export')
        .controller('ExportCtrl', ExportCtrl);
})();