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
                PlaygroundService.loadStep(previousStep);
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
         * @name stepHoverStart
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @param {number} index The position of the hovered button
         * @description On step button hover in order to inform actions on steps :
         * <ul>
         *     <li>highlight inactive buttons above the one (including the one)</li>
         *     <li>highlight active buttons under the one (including the one)</li>
         * </ul>
         */
        this.stepHoverStart = function (step) {
            var index = RecipeService.getStepIndex(step);
            var stepColumnId = +step.column.id;
            _.forEach(RecipeService.getRecipe(), function (element, elementIndex) {
                element.highlight = (element.inactive && index >= elementIndex) || (!element.inactive && index <= elementIndex);
            });

            $timeout.cancel(previewTimeout);
            if (RecipeService.getRecipe()[index].inactive) {
                previewTimeout = $timeout(previewAppend.bind(self, index, stepColumnId), 100);
            }
            else {
                previewTimeout = $timeout(previewDisable.bind(self, index, stepColumnId), 100);
            }
        };

        /**
         * @ngdoc method
         * @name stepHoverEnd
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @description On step button leave : reset steps button highlight
         */
        this.stepHoverEnd = function (step) {
            var stepColumnId = +step.column.id;
            _.forEach(RecipeService.getRecipe(), function (element) {
                element.highlight = false;
            });

            $timeout.cancel(previewTimeout);
            previewTimeout = $timeout(PreviewService.cancelPreview.bind(null, false, stepColumnId), 100);
        };

        //---------------------------------------------------------------------------------------------
        //---------------------------------------------Preview-----------------------------------------
        //---------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name previewAppend
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @param {string} stepPosition The step position index to preview
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
         * @param {string} stepPosition The step position index to disable for the preview
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