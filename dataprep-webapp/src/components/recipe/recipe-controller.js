(function() {
    'use strict';

    function RecipeCtrl(RecipeService) {
        var vm = this;
        vm.recipeService = RecipeService;
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