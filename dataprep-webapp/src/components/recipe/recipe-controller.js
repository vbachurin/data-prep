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
         * Load selected step and disable next steps
         */
        vm.loadStep = PlaygroundService.loadStep;
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