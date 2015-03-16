(function() {
    'use strict';

    function RecipeCtrl(RecipeService) {
        var vm = this;
        vm.recipeService = RecipeService;

        /**
         * Reset the params of the recipe item. Called on param accordion open
         * @type {RecipeService.resetParams}
         */
        vm.resetParams = RecipeService.resetParams;
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