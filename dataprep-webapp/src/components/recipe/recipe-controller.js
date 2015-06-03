(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.recipe.controller:RecipeCtrl
     * @description Recipe controller.
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.playground.service:PreviewService
     * @requires data-prep.services.preparation.service:PreparationService
     */
    function RecipeCtrl($rootScope, $timeout, RecipeService, PlaygroundService, PreparationService, PreviewService) {
        var vm = this;
        vm.recipeService = RecipeService;

        var previewTimeout;

        /**
         * @ngdoc method
         * @name resetParams
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {object} recipeItem - the item to reset
         * @description Reset the params of the recipe item by calling {@link data-prep.services.recipe.service:RecipeService RecipeService}
         * Called on param accordion open.
         */
        vm.resetParams = RecipeService.resetParams;

        //---------------------------------------------------------------------------------------------
        //------------------------------------------Params update--------------------------------------
        //---------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name stepUpdateClosure
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {object} step The step to bind the closure
         * @description Create a closure function that call the step update with the provided step id
         * @returns {Function} The function closure binded with the provided step id
         */
        vm.stepUpdateClosure = function(step) {
            return function(newParams) {
                vm.updateStep(step, newParams);
            };
        };

        /**
         * @ngdoc method
         * @name updateStep
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {string} step The step id to update
         * @param {object} newParams the new step parameters
         * @description Update a step parameters in the loaded preparation
         */
        vm.updateStep = function(step, newParams) {
            PreviewService.cancelPreview();

            if(! PreparationService.paramsHasChanged(step, newParams)) {
                return;
            }

            $rootScope.$emit('talend.loading.start');
            var lastActiveStepIndex = RecipeService.getActiveThresholdStepIndex();
            PreparationService.updateStep(step, newParams)
                .then(RecipeService.refresh)
                .then(function() {
                    var activeStep = RecipeService.getStep(lastActiveStepIndex, true);
                    return PlaygroundService.loadStep(activeStep);
                })
                .then(function() {
                    vm.showModal = {};
                })
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        };

        //---------------------------------------------------------------------------------------------
        //---------------------------------------------Preview-----------------------------------------
        //---------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name updatePreview
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {string} updateStep The step position index to update for the preview
         * @param {object} params The new step params
         * @description [PRIVATE] Call the preview service to display the diff between the original steps and the updated steps
         */
        var updatePreview = function(updateStep, params) {
            PreparationService.insertColumnInfo(params, updateStep.column);

            //Parameters has not changed
            if(updateStep.inactive || JSON.stringify(params) === JSON.stringify(updateStep.actionParameters.parameters)) {
                return;
            }

            var currentStep = RecipeService.getLastActiveStep();
            PreviewService.getPreviewUpdateRecords(currentStep, updateStep, params);
        };

        /**
         * @ngdoc method
         * @name previewUpdateClosure
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {object} step The step to update
         * @description [PRIVATE] Create a closure with a target step that call the update preview on execution
         */
        vm.previewUpdateClosure = function(step) {
            return function(params) {
                updatePreview(step, params);
            };
        };
    }

    /**
     * @ngdoc property
     * @name recipe
     * @propertyOf data-prep.recipe.controller:RecipeCtrl
     * @description The recipe.
     * It is bound to {@link data-prep.services.recipe.service:RecipeService RecipeService} property
     * @type {object[]}
     */
    Object.defineProperty(RecipeCtrl.prototype,
        'recipe', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.recipeService.getRecipe();
            }
        });

    angular.module('data-prep.recipe')
        .controller('RecipeCtrl', RecipeCtrl);
})();