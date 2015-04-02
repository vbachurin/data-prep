(function() {
    'use strict';

    function RecipeService(PreparationService, ConverterService) {
        var recipe = [];
        var listType = 'LIST';

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
         * Replace params values with saved initial values
         * @param params
         */
        var resetParamValue = function(params) {
            //choice
            if(params && params.type === listType) {
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
         * Init parameters initial value and type
         * @param parameters - the parameters
         * @param paramValues - the parameters initial values
         * @returns Array of parameters
         */
        var initParameters = function(parameters, paramValues) {
            return _.chain(parameters)
                .filter(function(param) {
                    return param.name !== 'column_name';
                })
                .forEach(function(param) {
                    param.initialValue = paramValues[param.name];
                    param.inputType = ConverterService.toInputType(param.type);
                })
                .value();
        };

        /**
         * Init choice initial value, including each choice params initial value and type
         * @param choices - the choices
         * @param paramValues - the parameters and choice initial values
         * @returns Array of choices
         */
        var initChoices = function(choices, paramValues) {
            return _.chain(choices)
                .forEach(function(choice) {
                    choice.type = listType;
                    choice.selectedValue = choice.initialValue = _.find(choice.values, function(choiceItem) {
                        return choiceItem.name === paramValues[choice.name];
                    });

                    _.forEach(choice.values, function(choiceItem) {
                        initParameters(choiceItem.parameters, paramValues);
                    });
                })
                .value();
        };

        /**
         * Create a recipe item from Preparation step
         * @param actionStep
         * @returns {Object}
         */
        var createItem = function(actionStep) {
            var parameters = initParameters(actionStep[2].parameters, actionStep[1].parameters);
            var items = initChoices(actionStep[2].items, actionStep[1].parameters);

            return {
                column: {
                    /*jshint camelcase: false */
                    id: actionStep[1].parameters.column_name
                },
                transformation: {
                    stepId: actionStep[0],
                    name: actionStep[1].action,
                    parameters: parameters,//parameters.length ? parameters : null,
                    items: items//items.length ? items : null
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
                        .zip(resp.data.actions, resp.data.metadata)
                        .map(createItem)
                        .value();
                });
        };
    }

    angular.module('data-prep.services.recipe')
        .service('RecipeService', RecipeService);
})();