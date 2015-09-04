(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.recipe.directive:Recipe
     * @description This directive display the recipe with the step params as accordions.
     * @restrict E
     * @usage
     <recipe></recipe>
     */
    function Recipe() {
        return {
            restrict: 'E',
            templateUrl: 'components/recipe/recipe.html',
            controllerAs: 'recipeCtrl',
            controller: 'RecipeCtrl'
        };
    }

    angular.module('data-prep.recipe')
        .directive('recipe', Recipe);
})();