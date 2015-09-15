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
            controller: 'RecipeCtrl',
            link: function(scope, iElement, iAttrs, ctrl) {
                function attachDeleteMouseOver(recipe) {
                    _.forEach(recipe, function(step) {
                        var stepId = step.transformation.stepId;
                        var hasDiff = step.diff && step.diff.createdColumns && step.diff.createdColumns.length;
                        var createdColumns = step.diff.createdColumns;

                        function shouldBeRemoved(stepToTest) {
                            /*jshint camelcase: false */
                            // current step
                            return stepToTest.transformation.stepId === stepId ||
                                    //step on a column that will be removed
                                    (hasDiff && stepToTest.actionParameters &&
                                        createdColumns.indexOf(stepToTest.actionParameters.parameters.column_id) > -1);
                        }

                        var stepsToRemove = _.chain(recipe)
                            .filter(shouldBeRemoved)
                            .map(function(step) {
                                return iElement.find('#step-' + step.transformation.stepId);
                            })
                            .value();

                        var removeElement = iElement.find('#step-' + stepId).find('#step-remove-' + stepId);
                        removeElement.on('mouseover', function() {
                            _.forEach(stepsToRemove, function(stepElement) {
                                stepElement.addClass('remove');
                            });
                        });
                        removeElement.on('mouseout', function() {
                            _.forEach(stepsToRemove, function(stepElement) {
                                stepElement.removeClass('remove');
                            });
                        });
                    });
                }

                scope.$watch(function() {
                    return ctrl.recipe;
                }, function(recipe) {
                    setTimeout(attachDeleteMouseOver.bind(null, recipe), 0);
                });
            }
        };
    }

    angular.module('data-prep.recipe')
        .directive('recipe', Recipe);
})();