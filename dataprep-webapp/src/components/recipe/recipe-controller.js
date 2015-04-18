(function() {
    'use strict';

    function RecipeCtrl(RecipeService, PlaygroundService) {
        var vm = this;
        vm.recipeService = RecipeService;

        /**
         * Reset the params of the recipe item. Called on param accordion open
         */
        vm.resetParams = RecipeService.resetParams;

        /**
         * Toggle selected step
         * - step is inactive : activate it with all the previous steps
         * - step is active : deactivate it with all the following steps
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
         * On step button hover in order to inform actions on steps :
         * - highlight inactive buttons above the one (including the one)
         * - highlight active buttons under the one (including the one)
         * @param index - the position of the hovered button
         */
        vm.stepHoverStart = function(index) {
            _.forEach(vm.recipe, function(element, elementIndex) {
                element.highlight = (element.inactive && index >= elementIndex) || (!element.inactive && index <= elementIndex);
            });
        };

        /**
         * On step button leave : reset steps button highlight
         */
        vm.stepHoverEnd = function() {
            _.forEach(vm.recipe, function(element) {
                element.highlight = false;
            });
        };
    }

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