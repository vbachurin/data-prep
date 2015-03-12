(function() {
    'use strict';

    function RecipeService() {
        var recipe = [];

        this.getRecipe = function() {
            return recipe;
        };

        var createRecipeItem = function(column, menu) {
            return {
                transformation: angular.copy(menu),
                column: column.id
            };
        };

        this.add = function(column, menu) {
            recipe.push(createRecipeItem(column, menu));
        };
    }

    angular.module('data-prep.services.recipe')
        .service('RecipeService', RecipeService);
})();