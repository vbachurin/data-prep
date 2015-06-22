(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.recipe.directive:Recipe
     * @description This directive display the recipe with the step params as accordions.
     * @restrict E
     * @usage
     <recipe
            metadata="metadata">
     </recipe>
     * @param {object} metadata The loaded metadata
     */
    function Recipe($timeout, RecipeService) {
        return {
            restrict: 'E',
            templateUrl: 'components/recipe/recipe.html',
            scope: {
                metadata: '='
            },
            bindToController: true,
            controllerAs: 'recipeCtrl',
            controller: 'RecipeCtrl',
            link: function(scope, iElement) {
                scope.$watch(
                    function() {
                        return RecipeService.getRecipe();
                    },
                    function(recipe) {
                        if(recipe.length) {
                            $timeout(function() {
                                iElement.find('.talend-accordion-trigger:last').click();
                            });
                        }
                    }
                );
            }
        };
    }

    angular.module('data-prep.recipe')
        .directive('recipe', Recipe);
})();