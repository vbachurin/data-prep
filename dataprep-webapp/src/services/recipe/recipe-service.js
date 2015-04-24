(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.recipe.service:RecipeService
     * @description Recipe service. This service provide the entry point to manipulate properly the recipe
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function RecipeService(PreparationService, ConverterService) {
        var listType = 'LIST';

        /**
         * @ngdoc property
         * @name recipe
         * @propertyOf data-prep.services.recipe.service:RecipeService
         * @description [PRIVATE] the recipe step list
         * @type {object[]}
         */
        var recipe = [];

        /**
         * @ngdoc property
         * @name activeThresholdStep
         * @propertyOf data-prep.services.recipe.service:RecipeService
         * @description [PRIVATE] the last recipe step that is active
         * @type {object}
         */
        var activeThresholdStep = null;

        /**
         * @ngdoc property
         * @name initialState
         * @propertyOf data-prep.services.recipe.service:RecipeService
         * @description [PRIVATE] the recipe initial step (without transformation)
         * @type {object}
         */
        var initialState;

        /**
         * @ngdoc method
         * @name getRecipe
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Return recipe step list
         * @returns {object[]} - the GET call promise
         */
        this.getRecipe = function() {
            return recipe;
        };

        /**
         * @ngdoc method
         * @name reset
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Reset the current recipe
         */
        this.reset = function() {
            initialState = null;
            recipe = [];
            activeThresholdStep = null;
        };

        /**
         * @ngdoc method
         * @name getActiveThresholdStep
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Get the last active step
         * @returns {object} - the last active step
         */
        this.getActiveThresholdStep = function() {
            return activeThresholdStep;
        };

        //--------------------------------------------------------------------------------------------------------------
        //----------------------------------------------------STEP PARAMS-----------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name resetParamValue
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} params - the params to reset
         * @description [PRIVATE] Reset params values with saved initial values
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
         * @ngdoc method
         * @name executeFnOnParams
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} transformation - the transformation with parameters
         * @param {function} fn - the function to execute
         * @description [PRIVATE] Execute function to all params and choices params
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
         * @ngdoc method
         * @name resetParams
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} recipeItem - the item to reset
         * @description Reset all params of the recipe item, with saved values (param.initialValue)
         */
        this.resetParams = function(recipeItem) {
            executeFnOnParams(recipeItem.transformation, resetParamValue);
        };

        /**
         * @ngdoc method
         * @name initParameters
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} parameters - the parameters
         * @param {object} paramValues - the parameters initial values
         * @description [PRIVATE] Init parameters initial value and type
         * @returns {object[]} - the parameters with initialized values
         */
        var initParameters = function(parameters, paramValues) {
            return _.chain(parameters)
                .filter(function(param) {
                    return param.name !== 'column_name';
                })
                .forEach(function(param) {
                    param.initialValue = param.value = paramValues[param.name];
                    param.inputType = ConverterService.toInputType(param.type);
                })
                .value();
        };

        /**
         * @ngdoc method
         * @name initChoices
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} choices - the choices
         * @param {object} paramValues - the parameters and choice initial values
         * @description [PRIVATE] Init choice initial value, including each choice params initial value and type
         * @returns {object[]} - the choices with initialized values
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

        //--------------------------------------------------------------------------------------------------------------
        //------------------------------------------------------STEPS--------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name createItem
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object[]} actionStep - the action step array containing [id, actions, metadata]
         * @description [PRIVATE] Create a recipe item from Preparation step
         * @returns {object} - the adapted recipe item
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
                    label: actionStep[2].label,
                    parameters: parameters,
                    items: items
                },
                actionParameters: actionStep[1]
            };
        };

        /**
         * @ngdoc method
         * @name refresh
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Refresh recipe items with current preparation steps
         */
        this.refresh = function() {
            PreparationService.getDetails()
                .then(function(resp) {
                    //steps ids are in reverse order and the last is the 'no-transformation' id
                    var steps = resp.data.steps.slice(0);
                    var initialStepId = steps.pop();
                    initialState = {transformation: {stepId: initialStepId}};

                    activeThresholdStep = null;
                    recipe = _.chain(steps)
                        .reverse()
                        .zip(resp.data.actions, resp.data.metadata)
                        .map(createItem)
                        .value();
                });
        };

        /**
         * @ngdoc method
         * @name disableStepsAfter
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} step - the limit between active and inactive
         * @description Disable all steps after the given one
         */
        this.disableStepsAfter = function(step) {
            var stepFound = step === initialState;
            _.forEach(recipe, function(nextStep) {
                if(stepFound) {
                    nextStep.inactive = true;
                }
                else {
                    nextStep.inactive = false;
                    if(nextStep === step) {
                        stepFound = true;
                    }
                }
            });
            activeThresholdStep = step;
        };

        /**
         * @ngdoc method
         * @name getPreviousStep
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} step - the given step
         * @description Get the step before the given one
         */
        this.getPreviousStep = function(step) {
            var previousStep = initialState;
            for(var i in recipe) {
                var currentStep = recipe[i];
                if(currentStep === step) {
                    return previousStep;
                }
                else {
                    previousStep = currentStep;
                }
            }
        };
    }

    angular.module('data-prep.services.recipe')
        .service('RecipeService', RecipeService);
})();