(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.recipe.service:BulletService
     * @description Recipe service. This service provide the entry point to manipulate properly the recipeBullet
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.transformation.service:TransformationService
     */
    function BulletService(RecipeService, $timeout, PreviewService, PlaygroundService) {
        var self = this;
        var previewTimeout;

        //---------------------------------------------------------------------------------------------
        //------------------------------------------Mouse handlers-------------------------------------
        //---------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name toggleStep
         * @methodOf data-prep.services.recipe.service:BulletService
         * @param {object} step - the step to toggle
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
        };

        /**
         * @ngdoc method
         * @name stepHoverStart
         * @methodOf data-prep.services.recipe.service:BulletService
         * @param {number} index - the position of the hovered button
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
         * @methodOf data-prep.services.recipe.service:BulletService
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
         * @methodOf data-prep.services.recipe.service:BulletService
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
         * @methodOf data-prep.services.recipe.service:BulletService
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
        .service('BulletService', BulletService);
})();