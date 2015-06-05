(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.recipe.service:RecipeService
     * @description Recipe service. This service provide the entry point to manipulate properly the recipe
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.transformation.service:TransformationService
     */
    function RecipeService(PreparationService, TransformationService) {
        var self = this;
        var choiceType = 'CHOICE';
        var clusterType = 'CLUSTER';

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

        //--------------------------------------------------------------------------------------------------------------
        //----------------------------------------------------STEP UTILS------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name getRecipe
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Return recipe step list
         * @returns {object[]} - The recipe step list
         */
        this.getRecipe = function() {
            return recipe;
        };

        /**
         * @ngdoc method
         * @name getStep
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {number} index The wanted index
         * @param {boolean} defaultLast Return the last step if no step is identified by the index
         * @description Return a recipe step identified by index
         * @returns {object} The recipe step
         */
        this.getStep = function(index, defaultLast) {
            if(index >= recipe.length || index < 0) {
                return defaultLast ? recipe[recipe.length - 1] : null;
            }
            return recipe[index];
        };

        /**
         * @ngdoc method
         * @name getStepBefore
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {number} index The targeted step index
         * @description Return the step just before the provided index
         * @returns {object} The recipe step
         */
        this.getStepBefore = function(index) {
            if(index <= 0) {
                return initialState;
            }
            else if(index >= recipe.length) {
                return recipe[recipe.length - 1];
            }

            return recipe[index - 1];
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

        /**
         * @ngdoc method
         * @name getActiveThresholdStepIndex
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Get the last active step index
         * @returns {number} The last active step index
         */
        this.getActiveThresholdStepIndex = function() {
            return activeThresholdStep ? recipe.indexOf(activeThresholdStep) : -1;
        };

        /**
         * @ngdoc method
         * @name getActiveThresholdStepIndexOnLaunch
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Get the last active step index, at launch it gets all the recipe Length
         * @returns {number} The last active step index, at launch it gets all the recipe Length
         */
        this.getActiveThresholdStepIndexOnLaunch = function() {
            return activeThresholdStep ? recipe.indexOf(activeThresholdStep) : recipe.length - 1;
        };

        /**
         * @ngdoc method
         * @name getCurrentStepIndex
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Get the current clicked step index
         * @returns {number} The current step index
         */
        this.getCurrentStepIndex = function(step) {
            return step?recipe.indexOf(step):-1;
        };

        /**
         * @ngdoc method
         * @name getLastActiveStep
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Get the last active step (last step if activeThresholdStep var is not set)
         * @returns {object} The last active step
         */
        this.getLastActiveStep = function() {
            return activeThresholdStep ? activeThresholdStep : recipe[recipe.length - 1];
        };

        //--------------------------------------------------------------------------------------------------------------
        //----------------------------------------------------STEP PARAMS-----------------------------------------------
        //--------------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name resetParams
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} recipeItem The item to reset
         * @description Reset all params of the recipe item, with saved values (param.initialValue)
         */
        this.resetParams = function(recipeItem) {
            //simple parameters
            TransformationService.resetParamValue(recipeItem.transformation.parameters, null);

            //choices
            TransformationService.resetParamValue(recipeItem.transformation.items, choiceType);

            //clusters
            TransformationService.resetParamValue(recipeItem.transformation.cluster, clusterType);
        };

        /**
         * @ngdoc method
         * @name initDynamicParams
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @param {object} recipes The old recipe (recipes.old) and the new recipe (recipes.new)
         * @description Initialize the dynamic parameters of the provided dynamic steps.
         * If a step exists in the old recipe (same stepId == same parent + same content), we reuse them
         * Otherwise, we call the backend
         */
        var initDynamicParams = function(recipes) {
            var getOldStepById = function(step) {
                return _.find(recipes.old, function(oldStep) {
                    return oldStep.transformation.stepId === step.transformation.stepId;
                });
            };

            var initOnStep = function(step) {
                var oldStep = getOldStepById(step);
                if(oldStep) {
                    step.transformation.parameters = oldStep.transformation.parameters;
                    step.transformation.items = oldStep.transformation.items;
                    step.transformation.cluster = oldStep.transformation.cluster;
                }
                else {
                    var infos = {
                        columnId: step.column.id,
                        preparationId:  PreparationService.currentPreparationId,
                        stepId: self.getPreviousStep(step).transformation.stepId
                    };
                    return TransformationService.initDynamicParameters(step.transformation, infos)
                        .then(function() {
                            return TransformationService.initParamsValues(step.transformation, step.actionParameters.parameters);
                        });
                }
            };

            return _.chain(recipes.new)
                .filter(function(step) {
                    return step.transformation.dynamic;
                })
                .forEach(initOnStep)
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
            var stepId = actionStep[0];
            var actionValues = actionStep[1];
            var metadata = actionStep[2];

            var item = {
                column: {
                    /*jshint camelcase: false */
                    id: actionValues.parameters.column_id,
                    name: actionValues.parameters.column_name
                },
                transformation: {
                    stepId: stepId,
                    name: actionValues.action,
                    label: metadata.label,
                    description: metadata.description,
                    parameters: metadata.parameters,
                    items: metadata.items,
                    dynamic: metadata.dynamic
                },
                actionParameters: actionValues
            };

            TransformationService.initParamsValues(item.transformation, actionValues.parameters);

            return item;
        };

        /**
         * @ngdoc method
         * @name refresh
         * @methodOf data-prep.services.recipe.service:RecipeService
         * @description Refresh recipe items with current preparation steps
         */
        this.refresh = function() {
            return PreparationService.getDetails()
                .then(function(resp) {
                    //steps ids are in reverse order and the last is the 'no-transformation' id
                    var steps = resp.data.steps.slice(0);
                    var initialStepId = steps.pop();
                    initialState = {transformation: {stepId: initialStepId}};

                    var oldRecipe = recipe;
                    var newRecipe = _.chain(steps)
                        .reverse()
                        .zip(resp.data.actions, resp.data.metadata)
                        .map(createItem)
                        .value();
                    activeThresholdStep = null;
                    recipe = newRecipe;

                    return {
                        'old': oldRecipe,
                        'new': newRecipe
                    };
                })
                .then(function(recipes) {
                    initDynamicParams(recipes);
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
    }

    angular.module('data-prep.services.recipe')
        .service('RecipeService', RecipeService);
})();