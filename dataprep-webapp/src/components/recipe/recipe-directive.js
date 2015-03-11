(function() {
    'use strict';

    function Recipe() {
        return {
            restrict: 'E',
            templateUrl: 'components/recipe/recipe.html',
            scope: {
                metadata: '='
            },
            bindToController: true,
            controllerAs: 'recipeCtrl',
            controller: 'RecipeCtrl'
        };
    }

    angular.module('data-prep.recipe')
        .directive('recipe', Recipe);
})();