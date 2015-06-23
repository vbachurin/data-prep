(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.export.controller:ExportCtrl
     * @description Export controller.
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.utils.service:RestURLs
     * @requires data-prep.services.export.service:ExportService
     */
    function ExportCtrl($window,PlaygroundService, PreparationService, RecipeService, RestURLs, ExportService) {
        var vm = this;
        vm.exportUrl = RestURLs.exportUrl;
        vm.preparationService = PreparationService;
        vm.recipeService = RecipeService;
        vm.playgroundService = PlaygroundService;
        vm.exportService = ExportService;

        // all export types
        vm.exportTypes = [];
        // dynamic object populated by reflection
        vm.exportParameters = {};

        // constants for localStorage keys
        vm.exportParamKey = 'datarep.export.param';
        vm.exportIdKey = 'dataprep.export.id';

        vm.currentExportType = {};


        /**
         * @ngdoc method
         * @name launchExport
         * @methodOf data-prep.export.controller:ExportCtrl
         * @description use by dropdown button list to change default export and define parameters
         */
        vm.launchExport = function(exportType){

            vm.currentExportType=exportType;
            var exportId;
            if (exportType){
                exportId = exportType.id;
            } else {
                var lastExportId = $window.localStorage.getItem(vm.exportIdKey);
                exportId = lastExportId;
            }

            if(!exportId){
                // use default one from rest api!!
                exportId = _.result(_.find(vm.exportTypes, function(exportType){
                    return exportType.defaultExport === 'true';
                }), 'id');
            }

            var needParameters = _.result(_.find(vm.exportTypes, function(exportType){
                return exportType.id === exportId;
            }), 'needParameters');


            $window.localStorage.setItem(vm.exportIdKey,exportId);

            if (needParameters==='true' && exportType){
                vm.setupParametersModal(exportType);
                return;
            }

            vm.export();
        };

        /**
         * @ngdoc method
         * @name export
         * @methodOf data-prep.export.controller:ExportCtrl
         * @description start the export
         */
        vm.export = function() {

            // search in local storage
            var type = $window.localStorage.getItem(vm.exportIdKey);

            // if not use the default one
            if(!type){
                var needParameters = false;
                _.each(vm.exportType,function(val){
                   if (val.defaultExport==='true'){
                       type=val.id;
                       needParameters=val.needParameters;
                       vm.currentExportType=val;
                   }
                });
                // if this default need parameters open modal to ask
                if(needParameters==='true'){
                    vm.setupParametersModal(vm.currentExportType);
                    return;
                }
            }

            var form = document.getElementById('exportForm');
            form.action = vm.exportUrl;
            form.exportType.value = type;

            _.each(Object.keys(vm.exportParameters),function(val){
                form.elements['exportParameters.' + val].value = vm.exportParameters[val];
                $window.localStorage.setItem(vm.exportParamKey+'.'+val,vm.exportParameters[val]);
            });

            form.submit();

        };

        /**
         * @ngdoc method
         * @name setupParametersModal
         * @methodOf data-prep.export.controller:ExportCtrl
         * @description prepare dynamic objects with new fields corresponding to export parameters
         */
        vm.setupParametersModal = function(exportType) {
            _.each( exportType.parameters, function ( val ){
                if ( val.type === 'radio' ){
                    vm.exportParameters[val.name] = val.defaultValue.value;
                }
                var paramValue = $window.localStorage.getItem( vm.exportParamKey + '.' + val.name );

            });
            vm.showExport = true;
        };


        // setup export types on start
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