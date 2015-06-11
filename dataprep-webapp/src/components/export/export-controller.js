(function() {
    'use strict';

    function ExportCtrl($window,PlaygroundService, PreparationService, RecipeService, RestURLs, ExportService) {
        var vm = this;
        vm.exportUrl = RestURLs.exportUrl;
        vm.preparationService = PreparationService;
        vm.recipeService = RecipeService;
        vm.playgroundService = PlaygroundService;
        vm.exportService = ExportService;
        vm.csvSeparator = ';';
        vm.exportTypes = [];


        vm.launchExport = function(usedType){
            console.log('launchExport: \'' + usedType + '\'');
            var exportType;
            if (usedType){
                exportType = usedType;
            } else {
                var lastType = $window.localStorage.getItem("dataprep.export.lastType");
                exportType = lastType;
            }

            if(!exportType){
                // use default one
            }
            $window.localStorage.setItem("dataprep.export.lastType",exportType);
            vm.export(exportType);
        };

        vm.export = function (type) {
            console.log('export: \'' + type + '\'');
            var form = document.getElementById('exportForm');
            form.action = vm.exportUrl;
            form.exportType.value = type;
            form.submit();
        };

        ExportService.exportTypes()
            .then(function(response){
                vm.exportTypes = response.data;
        });

    }

    /**
     * @ngdoc property
     * @name preparationId
     * @propertyOf data-prep.export.controller:ExportCtrl
     * @description The current preparationId
     * It is bound to {@link data-prep.services.preparation.service:PreparationService PreparationService} property
     */
    Object.defineProperty(ExportCtrl.prototype,
        'preparationId', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.preparationService.currentPreparationId;
            }
        });

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
     * It is bound to {@link data-prep.services.playground.service:PlaygroundService PlaygroundService} property
     */
    Object.defineProperty(ExportCtrl.prototype,
        'datasetId', {
            enumerable: true,
            configurable: false,
            get: function () {
                var metadata = this.playgroundService.currentMetadata;
                return metadata ? metadata.id : '';
            }
        });

    angular.module('data-prep.export')
        .controller('ExportCtrl', ExportCtrl);
})();