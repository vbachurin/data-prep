(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.recipe.service:RecipeBulletService
     * @description Recipe Bullet service. This service provides the action services triggered by bullets
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.playground.service:PreviewService
     * @requires data-prep.services.playground.service:PlaygroundService
     */
    function RecipeBulletService($timeout, RecipeService, PreviewService, PlaygroundService) {
        var self = this;
        var previewTimeout;

        //---------------------------------------------------------------------------------------------
        //------------------------------------------Mouse Actions--------------------------------------
        //---------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name toggleStep
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @param {object} step The step to toggle
         * @description Toggle selected step and load the last active step content
         * <ul>
         *     <li>step is inactive : activate it with all the previous steps</li>
         *     <li>step is active : deactivate it with all the following steps</li>
         * </ul>
         */
        this.toggleStep = function (step) {
            PreviewService.cancelPreview();

            if (step.inactive) {
                PlaygroundService.loadStep(step);
            }
            else {
                var previousStep = RecipeService.getPreviousStep(step);
                PlaygroundService.loadStep(previousStep, step);
            }
        };

        /**
         * @ngdoc method
         * @name toggleRecipe
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @description Enable/disable the recipe.
         * When it is enabled, the last active step before disabling action is loaded
         */
        this.toggleRecipe = function toggleRecipe() {
            var recipe = RecipeService.getRecipe();
            var firstStep = recipe[0];
            var stepToLoad;

            if (!firstStep.inactive) {
                self.lastToggled = RecipeService.getLastActiveStep();
                stepToLoad = firstStep;
            }
            else {
                stepToLoad = self.lastToggled || recipe[recipe.length - 1];
            }
            self.toggleStep(stepToLoad);
        };

        /**
         * @ngdoc method
         * @name cancelPendingPreview
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @description Cancel the pending preview. If the REST call is pending, this call is canceled too.
         */
        function cancelPendingPreview() {
            clearTimeout(previewTimeout);
            PreviewService.stopPendingPreview();
        }

        /**
         * @ngdoc method
         * @name stepHoverStart
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @param {number} step The hovered step
         * @description On step button hover in order to inform actions on steps :
         * <ul>
         *     <li>highlight inactive buttons above the one (including the one)</li>
         *     <li>highlight active buttons under the one (including the one)</li>
         * </ul>
         */
        this.stepHoverStart = function (step) {
            cancelPendingPreview();
            previewTimeout = setTimeout(function() {
                var index = RecipeService.getStepIndex(step);
                var stepColumnId = step.column.id;

                if (RecipeService.getRecipe()[index].inactive) {
                    previewAppend(index, stepColumnId);
                }
                else {
                    previewDisable(index, stepColumnId);
                }
            }, 200);
        };

        /**
         * @ngdoc method
         * @name stepHoverEnd
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * * @param {number} step The hovered end step
         * @description On step button leave : reset steps button highlight
         */
        this.stepHoverEnd = function (step) {
            cancelPendingPreview();
            previewTimeout = setTimeout(function() {
                $timeout(PreviewService.cancelPreview.bind(null, false, step.column.id));
            }, 100);
        };

        //---------------------------------------------------------------------------------------------
        //---------------------------------------------Preview-----------------------------------------
        //---------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name previewAppend
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @param {number} stepPosition The step position index to preview
         * @param {string} stepColumnId The step target column id
         * @description [PRIVATE] Call the preview service to display the diff between the current step and the disabled targeted step
         */
        var previewAppend = function (stepPosition, stepColumnId) {
            var previewStep = RecipeService.getStep(stepPosition);
            var currentStep = RecipeService.getLastActiveStep();

            PreviewService.getPreviewDiffRecords(currentStep, previewStep, stepColumnId);
        };

        /**
         * @ngdoc method
         * @name previewDisable
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @param {number} stepPosition The step position index to disable for the preview
         * @param {string} stepColumnId The step target column id
         * @description [PRIVATE] Call the preview service to display the diff between the current step and the step before the active targeted step
         */
        var previewDisable = function (stepPosition, stepColumnId) {
            var previewStep = RecipeService.getStepBefore(stepPosition);
            var currentStep = RecipeService.getLastActiveStep();

            PreviewService.getPreviewDiffRecords(currentStep, previewStep, stepColumnId);
        };
    }

    angular.module('data-prep.services.recipe')
        .service('RecipeBulletService', RecipeBulletService);
})();