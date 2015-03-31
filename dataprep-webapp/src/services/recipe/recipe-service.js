(function() {
    'use strict';

    function RecipeService(PreparationService) {
        var recipe = [];

        /**
         * Return recipe item list
         * @returns {Array}
         */
        this.getRecipe = function() {
            return recipe;
        };

        /**
         * Reset the current recipe item list
         */
        this.reset = function() {
            recipe = [];
        };

        /**
         * Save current value ine param.initialValue
         * @param params
         */
        var saveParamCurrentValue = function(params) {
            //choice
            if(params && params.type === 'LIST') {
                params.initialValue = params.selectedValue;
            }
            //parameters
            else {
                _.forEach(params, function (param) {
                    param.initialValue = angular.isDefined(param.value) ? param.value : param.default;
                });
            }
        };

        /**
         * Replace params values with saved initial values
         * @param params
         */
        var resetParamValue = function(params) {
            //choice
            if(params && params.type === 'LIST') {
                params.selectedValue = params.initialValue;
            }
            //parameters
            else {
                _.forEach(params, function (param) {
                    param.value = param.initialValue;
                });
            }
        };

        /**
         * Execute function to all params and choices params
         * @param transformation - the transformation with parameters
         * @param fn - the function to execute
         */
        var executeFnOnParams = function(transformation, fn) {
            fn(transformation.parameters);

            _.forEach(transformation.items, function (choice) {
                fn(choice);
                _.forEach(choice.values, function (choiceItem) {
                    fn(choiceItem.parameters);
                });
            });
        };

        /**
         * Clone the transformation and create a recipe item
         * @param column - the targeted column
         * @param transformation - the applied transformation
         */
        var createRecipeItem = function(column, transformation) {
            return {
                transformation: angular.copy(transformation),
                column: column
            };
        };

        /**
         * Add a new item in recipe list
         * @param column - the targeted column
         * @param transformation - the applied transformation
         */
        this.add = function(column, transformation) {
            var recipeItem = createRecipeItem(column, transformation);
            recipe.push(recipeItem);
            executeFnOnParams(recipeItem.transformation, saveParamCurrentValue);
        };

        /**
         * Reset all params of the recipe item, with saved values (param.initialValue)
         * @param recipeItem - the item to reset
         */
        this.resetParams = function(recipeItem) {
            executeFnOnParams(recipeItem.transformation, resetParamValue);
        };

        //--------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------PREPARATION---------------------------------------------
        //--------------------------------------------------------------------------------------------------------------

        /**
         * Create a recipe item from Preparation step
         * @param actionStep
         * @returns {Object}
         */
        var createItem = function(actionStep) {
            var parameters = _.chain(Object.keys(actionStep[1].parameters))
                .filter(function(paramKey) {
                    return paramKey !== 'column_name';
                })
                .map(function(paramKey) {
                    var paramName = paramKey;
                    var paramValue = actionStep[1].parameters[paramKey];
                    return {
                        label: paramName,
                        name: paramName,
                        type: isNaN(paramValue) ? 'string' : 'numeric',
                        inputType: isNaN(paramValue) ? 'text' : 'number',
                        initialValue: paramValue,
                        default: paramValue
                    };
                })
                .value();
            return {
                column: {
                    /*jshint camelcase: false */
                    id: actionStep[1].parameters.column_name
                },
                transformation: {
                    stepId: actionStep[0],
                    name: actionStep[1].action,
                    parameters: parameters.length ? parameters : null
                }
            };
        };

        /**
         * Refresh recipe items with current preparation steps
         */
        this.refresh = function() {
            PreparationService.getDetails()
                .then(function(resp) {
                    var steps = resp.data.steps.slice(1);
                    recipe = _.chain(steps)
                        .zip(resp.data.actions)
                        .map(createItem)
                        .value();
                });
        };
    }

    angular.module('data-prep.services.recipe')
        .service('RecipeService', RecipeService);
})();