(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.recipe.controller:RecipeCtrl
     * @description Recipe controller.
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.playground.service:PlaygroundService
     */
    function RecipeCtrl(RecipeService, PlaygroundService, DatasetPreviewService) {
        var vm = this;
        vm.recipeService = RecipeService;

        /**
         * @ngdoc method
         * @name resetParams
         * @methodOf data-prep.recipe.controller:RecipeCtrl
         * @param {object} recipeItem - the item to reset
         * @description Reset the params of the recipe item by calling {@link data-prep.services.recipe.service:RecipeService RecipeService}
         * Called on param accordion open.
         */
        vm.resetParams = RecipeService.resetParams;

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

            if(vm.recipe[index].inactive) {
                var actions = [];
                _.chain(vm.recipe)
                    .filter(function(element, elementIndex) {
                        return elementIndex <= index && element.inactive;
                    })
                    .forEach(function(element) {
                        actions.push(element.actionParameters);
                    })
                    .value();
                DatasetPreviewService.getPreviewAppendRecords(actions);
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
            DatasetPreviewService.cancelPreview();
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