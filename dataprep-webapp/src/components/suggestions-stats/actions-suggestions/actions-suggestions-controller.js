(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
     * @description Actions suggestion controller
     * @requires data-prep.services.transformation.service:ColumnSuggestionService
     * @requires data-prep.services.transformation.service:TransformationService
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.playground.service:PreviewService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.recipe.service:RecipeService
     */
    function ActionsSuggestionsCtrl($timeout, ColumnSuggestionService, TransformationService, PlaygroundService, PreviewService, PreparationService, RecipeService) {
        var previewTimeout;
        var vm = this;
        vm.columnSuggestionService = ColumnSuggestionService;

        /**
         * @ngdoc property
         * @name dynamicTransformation
         * @propertyOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description The dynamic param transformation to display
         */
        vm.dynamicTransformation = null;

        /**
         * @ngdoc property
         * @name dynamicFetchInProgress
         * @propertyOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description Flag that indicates if a fetch of dynamic parameters is in progress
         */
        vm.dynamicFetchInProgress = false;

        /**
         * @ngdoc property
         * @name showDynamicModal
         * @propertyOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description Flag that change the dynamic parameters modal display
         */
        vm.showDynamicModal = false;

        /**
         * @ngdoc method
         * @name initDynamicParams
         * @methodOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description [PRIVATE] Fetch the transformation dynamic parameters and inject them into transformation menu params
         * @returns {promise} The GET request promise
         */
        var initDynamicParams = function(transfo) {
            var infos = {
                columnId: vm.column.id,
                datasetId:  PlaygroundService.currentMetadata.id,
                preparationId:  PreparationService.currentPreparationId
            };
            return TransformationService.initDynamicParameters(transfo, infos);
        };


        /**
         * @ngdoc property
         * @name showModalContent
         * @propertyOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description show/hides the dynamic transformation or the alert message
         */
        vm.showModalContent = null;

        /**
         * @ngdoc method
         * @name checkDynamicResponse
         * @methodOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description [PRIVATE] sets the showHideModalContent and the emptyParamsMsg properties
         */
        vm.checkDynamicResponse = function showModalContent (){
            //transformation type :cluster
            if(vm.dynamicTransformation.cluster){
                if(vm.dynamicTransformation.cluster.clusters.length){
                    vm.showModalContent = true;
                }
                else{
                    vm.showModalContent = false;
                    vm.emptyParamsMsg = 'NO_CLUSTERS_ACTION_MSG';
                }
            }

            //transformation type :simpleParams
            else if(vm.dynamicTransformation.parameters){
                if(vm.dynamicTransformation.parameters.length){
                    vm.showModalContent = true;
                }
                else{
                    vm.showModalContent = false;
                    vm.emptyParamsMsg = 'NO_CHOICES_ACTION_MSG';
                }
            }

            //transformation type :choice
            else if(vm.dynamicTransformation.items){
                if(vm.dynamicTransformation.items.length){
                    vm.showModalContent = true;
                }
                else{
                    vm.showModalContent = false;
                    vm.emptyParamsMsg = 'NO_PARAMS_ACTION_MSG';
                }
            }
        };

        /**
         * @ngdoc method
         * @name select
         * @methodOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description Transformation selection.
         <ul>
            <li>Dynamic transformation : fetch the dynamic parameters and show the modal</li>
            <li>Static transformation : append the new step in the current preparation</li>
         </ul>
         */
        vm.select = function select(transfo, transfoScope) {
            if(transfo.dynamic) {
                vm.dynamicTransformation = transfo;
                vm.dynamicScope = transfoScope;
                vm.dynamicFetchInProgress = true;
                vm.showDynamicModal = true;

                //get new parameters
                initDynamicParams(transfo).finally(function() {
                    vm.checkDynamicResponse();
                    vm.dynamicFetchInProgress = false;
                });
            }
            else {
                vm.transformClosure(transfo, transfoScope)();
            }
        };

        /**
         * @ngdoc method
         * @name transformClosure
         * @methodOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description Transformation application closure. It take the transformation to build the closure.
         * The closure then take the parameters and append the new step in the current preparation
         */
        vm.transformClosure = function transform(transfo, transfoScope) {
            /*jshint camelcase: false */
            return function(params) {
                PreviewService.cancelPreview();
                RecipeService.cancelEarlyPreview();

                params = params || {};
                params.scope = transfoScope;
                params.column_id = vm.column.id;
                params.column_name = vm.column.name;

                PlaygroundService.appendStep(transfo.name, params)
                    .then(function() {
                        vm.showDynamicModal = false;
                    });
            };
        };

        /**
         * @ngdoc method
         * @name earlyPreview
         * @methodOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @param {object} transformation The transformation
         * @param {string} transfoScope The transformation scope
         * @description Perform an early preview (preview before transformation application) after a 200ms delay
         */
        vm.earlyPreview = function earlyPreview(transformation, transfoScope) {
            /*jshint camelcase: false */
            return function(params) {
                $timeout.cancel(previewTimeout);
                previewTimeout = $timeout(function() {
                    params.scope = transfoScope;
                    params.column_id = vm.column.id;
                    params.column_name = vm.column.name;

                    var datasetId = PlaygroundService.currentMetadata.id;

                    RecipeService.earlyPreview(vm.column, transformation, params);
                    PreviewService.getPreviewAddRecords(datasetId, transformation.name, params);
                }, 200);
            };
        };

        /**
         * @ngdoc method
         * @name cancelEarlyPreview
         * @methodOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description Cancel any current or pending early preview
         */
        vm.cancelEarlyPreview = function cancelEarlyPreview() {
            $timeout.cancel(previewTimeout);
            RecipeService.cancelEarlyPreview();
            PreviewService.cancelPreview();
        };
    }

    /**
     * @ngdoc property
     * @name column
     * @propertyOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
     * @description The transformations column.
     * This is bound to {@link data-prep.services.transformation:ColumnSuggestionService ColumnSuggestionService}.currentColumn
     */
    Object.defineProperty(ActionsSuggestionsCtrl.prototype,
        'column', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.columnSuggestionService.currentColumn;
            }
        });

    /**
     * @ngdoc property
     * @name suggestions
     * @propertyOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
     * @description The suggested transformations list.
     * This is bound to {@link data-prep.services.transformation:ColumnSuggestionService ColumnSuggestionService}.transformations
     */
    Object.defineProperty(ActionsSuggestionsCtrl.prototype,
        'columnSuggestions', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.columnSuggestionService.transformations;
            }
        });

    angular.module('data-prep.actions-suggestions')
        .controller('ActionsSuggestionsCtrl', ActionsSuggestionsCtrl);
})();