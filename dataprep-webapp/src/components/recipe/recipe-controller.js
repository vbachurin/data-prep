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
        //------------------------------------------Mouse handlers-------------------------------------
        //---------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name toggleStep
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {object} step - the step to toggle
         * @description Toggle selected step and load the last active step content
         * <ul>
         *     <li>step is inactive : activate it with all the previous steps</li>
         *     <li>step is active : deactivate it with all the following steps</li>
         * </ul>
         */
        vm.toggleStep = function(step) {
            PreviewService.cancelPreview();

            if(step.inactive) {
                PlaygroundService.loadStep(step);
            }
            else {
                var previousStep = RecipeService.getPreviousStep(step);
                PlaygroundService.loadStep(previousStep);
            }
        };

        /**
         * @ngdoc method
         * @name stepHoverStart
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {number} index - the position of the hovered button
         * @description On step button hover in order to inform actions on steps :
         * <ul>
         *     <li>highlight inactive buttons above the one (including the one)</li>
         *     <li>highlight active buttons under the one (including the one)</li>
         * </ul>
         */
        vm.stepHoverStart = function(index) {
            _.forEach(vm.recipe, function(element, elementIndex) {
                element.highlight = (element.inactive && index >= elementIndex) || (!element.inactive && index <= elementIndex);
            });

            $timeout.cancel(previewTimeout);
            if(vm.recipe[index].inactive) {
                previewTimeout = $timeout(previewAppend.bind(vm, index), 100);
            }
            else {
                previewTimeout = $timeout(previewDisable.bind(vm, index), 100);
            }
        };

        /**
         * @ngdoc method
         * @name stepHoverEnd
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @description On step button leave : reset steps button highlight
         */
        vm.stepHoverEnd = function() {
            _.forEach(vm.recipe, function(element) {
                element.highlight = false;
            });

            $timeout.cancel(previewTimeout);
            previewTimeout = $timeout(PreviewService.cancelPreview, 100);
        };

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
                    vm.showModal = [];
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
         * @name previewAppend
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {string} stepPosition The step position index to preview
         * @description [PRIVATE] Call the preview service to display the diff between the current step and the disabled targeted step
         */
        var previewAppend = function(stepPosition) {
            var previewStep = RecipeService.getStep(stepPosition);
            var currentStep = RecipeService.getLastActiveStep();

            PreviewService.getPreviewDiffRecords(currentStep, previewStep);
        };

        /**
         * @ngdoc method
         * @name previewDisable
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {string} stepPosition The step position index to disable for the preview
         * @description [PRIVATE] Call the preview service to display the diff between the current step and the step before the active targeted step
         */
        var previewDisable = function(stepPosition) {
            var previewStep = RecipeService.getStepBefore(stepPosition);
            var currentStep = RecipeService.getLastActiveStep();

            PreviewService.getPreviewDiffRecords(currentStep, previewStep);
        };

        /**
         * @ngdoc method
         * @name updatePreview
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {string} updateStep The step position index to update for the preview
         * @param {object} params The new step params
         * @description [PRIVATE] Call the preview service to display the diff between the original steps and the updated steps
         */
        var updatePreview = function(updateStep, params) {
            /*jshint camelcase: false */
            params.column_name = updateStep.column.id;

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