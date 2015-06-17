(function() {
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
        this.allToggledSteps = [];

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
        this.toggleStep = function(step) {
            PreviewService.cancelPreview();

            if(step.inactive) {
                PlaygroundService.loadStep(step);
            }
            else {
                var previousStep = RecipeService.getPreviousStep(step);
                PlaygroundService.loadStep(previousStep);
            }
                self.allToggledSteps.push(RecipeService.getLastActiveStep());
        };

        /**
         * @ngdoc method
         * @name toggleAllSteps
         * @methodOf data-prep.recipe.service:RecipeBulletService
         * @description Enable/disable step All steps
         */
        this.toggleAllSteps = function() {
            var firstStep = RecipeService.getRecipe()[0];
            if(!firstStep.inactive){
                self.toggleStep(firstStep);
            }else{
                var lastToggledStep = self.allToggledSteps[self.allToggledSteps.length-1];
                var stepToToggle = RecipeService.isFirstStep(lastToggledStep)?self.allToggledSteps[self.allToggledSteps.length - 2]:lastToggledStep;
                self.toggleStep(stepToToggle);
                //var lastStepIndex = RecipeService.getRecipe().length - 1;
                //self.toggleStep(RecipeService.getRecipe()[lastStepIndex]);
            }

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
        this.stepHoverStart = function(index) {
            _.forEach(RecipeService.getRecipe(), function(element, elementIndex) {
                element.highlight = (element.inactive && index >= elementIndex) || (!element.inactive && index <= elementIndex);
            });

            $timeout.cancel(previewTimeout);
            if(RecipeService.getRecipe()[index].inactive) {
                previewTimeout = $timeout(previewAppend.bind(self, index), 100);
            }
            else {
                previewTimeout = $timeout(previewDisable.bind(self, index), 100);
            }
        };

        /**
         * @ngdoc method
         * @name stepHoverEnd
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @description On step button leave : reset steps button highlight
         */
        this.stepHoverEnd = function() {
            _.forEach(RecipeService.getRecipe(), function(element) {
                element.highlight = false;
            });

            $timeout.cancel(previewTimeout);
            previewTimeout = $timeout(PreviewService.cancelPreview, 100);
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
        var previewAppend = function(stepPosition) {
            var previewStep = RecipeService.getStep(stepPosition);
            var currentStep = RecipeService.getLastActiveStep();

            PreviewService.getPreviewDiffRecords(currentStep, previewStep);
        };

        /**
         * @ngdoc method
         * @name previewDisable
         * @methodOf data-prep.services.recipe.service:RecipeBulletService
         * @param {string} stepPosition The step position index to disable for the preview
         * @description [PRIVATE] Call the preview service to display the diff between the current step and the step before the active targeted step
         */
        var previewDisable = function(stepPosition) {
            var previewStep = RecipeService.getStepBefore(stepPosition);
            var currentStep = RecipeService.getLastActiveStep();

            PreviewService.getPreviewDiffRecords(currentStep, previewStep);
        };
    }

    angular.module('data-prep.services.recipe')
        .service('RecipeBulletService', RecipeBulletService);
})();