(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.lookup.service:LookupService
     * @description Lookup service. This service provide the entry point to load lookup content
     * @requires data-prep.services.state.service:StateService
     * @requires data-prep.services.transformations.service:TransformationRestService
     * @requires data-prep.services.datasets.service:DatasetRestService
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.utils.service:StorageService
     */
    function LookupService($q, state, StateService, TransformationRestService, DatasetRestService, RecipeService, StorageService) {

        return {
            initLookups: initLookups,
            loadFromAction: loadFromAction,
            loadFromStep: loadFromStep,
            updateTargetColumn: updateTargetColumn,
            updateLookupDatasets: updateLookupDatasets,
            disableDatasetsUsedInRecipe: disableDatasetsUsedInRecipe,
            getLookupDatasetsSort: getLookupDatasetsSort,
            getLookupDatasetsOrder: getLookupDatasetsOrder
        };

        /**
         * @ngdoc method
         * @methodOf data-prep.services.lookup.service:LookupService
         * @name getLookupDatasetsSort
         * @description Returns the actual sort parameter
         * */
        function getLookupDatasetsSort() {
            var savedSort = StorageService.getLookupDatasetsSort();
            return savedSort ? savedSort : state.inventory.sortList[1].id;
        }

        /**
         * @ngdoc method
         * @methodOf data-prep.services.lookup.service:LookupService
         * @name getLookupDatasetsOrder
         * @description Returns the actual order parameter
         */
        function getLookupDatasetsOrder() {
            var savedSortOrder = StorageService.getLookupDatasetsOrder();
            return savedSortOrder ? savedSortOrder : state.inventory.orderList[1].id;
        }

        /**
         * @ngdoc method
         * @name getActions
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {string} datasetId The dataset id
         * @description Loads the possible lookup actions (1 action per dataset lookup)
         */
        function getActions(datasetId) {
            if (state.playground.lookup.addedActions.length) {
                return $q.when(state.playground.lookup.addedActions);
            }
            else {
                return TransformationRestService.getDatasetTransformations(datasetId)
                    .then(function (lookup) {
                        var actionsList = lookup.data;
                        var datasetsToAdd = [];
                        _.forEach(actionsList, function(action) {
                            var datasetToAdd = _.find(state.inventory.datasets, {'id': getDsId(action)});
                            datasetToAdd.addedToLookup = false;
                            datasetToAdd.enableToAddToLookup = true;
                            datasetsToAdd.push(datasetToAdd);
                        });
                        StateService.setLookupDatasets(datasetsToAdd);
                        StateService.setLookupActions(actionsList);

                        initLookupDatasets();

                        return state.playground.lookup.addedActions;
                    });
            }
        }

        /**
         * @ngdoc method
         * @name initLookups
         * @methodOf data-prep.services.lookup.service:LookupService
         * @description Load lookup panel content.
         * We completely init the panel, so if the selected column has a lookup in the recipe, we load the last one as update.
         * Otherwise, we init the the first lookup action as new lookup.
         */
        function initLookups() {
            return getActions(state.playground.dataset.id)
                .then(function (lookupActions) {
                    if (!lookupActions.length) {
                        return;
                    }

                    var step = getSelectedColumnLastLookup();
                    if (step) {
                        return loadFromStep(step);
                    }
                    else {
                        return loadFromAction(lookupActions[0]);
                    }
                });
        }

        /**
         * @ngdoc method
         * @name loadFromAction
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookupAction The lookup action
         * @description Loads the lookup dataset content.
         * When the selected column already has a this lookup in recipe, we load it as update.
         * Otherwise, load the lookup action as a new lookup
         */
        function loadFromAction(lookupAction) {
            var step = getSelectedColumnLookup(lookupAction);
            if (step) {
                return loadFromStep(step);
            }

            return getActions(state.playground.dataset.id)
                .then(function () {
                    //lookup already loaded
                    if (state.playground.lookup.dataset === lookupAction && !state.playground.lookup.step) {
                        return;
                    }

                    //load content
                    return DatasetRestService.getContent(getDsId(lookupAction), true)
                        .then(function (lookupDsContent) {
                            initLookupState(lookupAction, lookupDsContent, undefined);
                        });
                });

        }

        /**
         * @ngdoc method
         * @name loadFromStep
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} step The lookup step to load in update mode
         * @description Loads the lookup dataset content from step in update mode
         */
        function loadFromStep(step) {
            return getActions(state.playground.dataset.id)
                .then(function (actions) {
                    /*jshint camelcase: false */
                    var lookupId = step.actionParameters.parameters.lookup_ds_id;
                    var lookupAction = _.find(actions, function (action) {
                        return getDsId(action) === lookupId;
                    });

                    //change column selection to focus on step target
                    /*jshint camelcase: false */
                    var selectedColumn = _.find(state.playground.data.metadata.columns, {id: step.actionParameters.parameters.column_id});
                    StateService.setGridSelection(selectedColumn);

                    //lookup already loaded
                    if (state.playground.lookup.dataset === lookupAction &&
                        state.playground.lookup.step === step) {
                        return;
                    }

                    //load content
                    return getActions(state.playground.dataset.id)
                        .then(function () {
                            return DatasetRestService.getContent(lookupId, true);
                        })
                        .then(function (lookupDsContent) {
                            initLookupState(lookupAction, lookupDsContent, step);
                        });
                });
        }

        /**
         * @ngdoc method
         * @name updateTargetColumn
         * @methodOf data-prep.services.lookup.service:LookupService
         * @description Update the loaded lookup.
         * When the combination [lookup action, selected column] is a step in recipe, we load it as update.
         * Otherwise, we load the lookup action as new lookup
         */
        function updateTargetColumn() {
            if(!state.playground.lookup.visibility) {
                return;
            }

            var lookupAction = state.playground.lookup.dataset;
            return loadFromAction(lookupAction);
        }

        /**
         * @ngdoc method
         * @name getDsId
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookup the lookup action
         * @returns {String} The id of the lookup dataset
         * @description Extract the dataset id from lookup action
         */
        function getDsId(lookup) {
            return _.find(lookup.parameters, {'name': 'lookup_ds_id'}).default;
        }

        /**
         * @ngdoc method
         * @name initLookupState
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookupAction The lookup action
         * @param {object} lookupDsContent The lookup dataset content (columns, records, ...)
         * @param {object} step The step that the lookup is updating (falsy means no update but add mode)
         * @description Set the lookup state
         */
        function initLookupState(lookupAction, lookupDsContent, step) {
            if (step) {
                StateService.setLookupUpdateMode(lookupAction, lookupDsContent, step);
            }
            else {
                StateService.setLookupAddMode(lookupAction, lookupDsContent);
            }
        }

        /**
         * @ngdoc method
         * @name getSelectedColumnLastLookup
         * @methodOf data-prep.services.lookup.service:LookupService
         * @description Fetch the last step in recipe that is a lookup action for the selected column
         */
        function getSelectedColumnLastLookup() {
            var selectedColumn = state.playground.grid.selectedColumn;
            return _.findLast(RecipeService.getRecipe(), function (nextStep) {
                return nextStep.column.id === selectedColumn.id && nextStep.transformation.name === 'lookup';
            });
        }

        /**
         * @ngdoc method
         * @name getSelectedColumnLookup
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookupAction The lookup action to seek
         * @description Fetch the step in recipe that is a lookup action on a specific dataset for the selected column
         */
        function getSelectedColumnLookup(lookupAction) {
            var datasetId = getDsId(lookupAction);
            var selectedColumn = state.playground.grid.selectedColumn;
            return _.findLast(RecipeService.getRecipe(), function (nextStep) {
                /*jshint camelcase: false */
                return nextStep.column.id === selectedColumn.id &&
                    nextStep.transformation.name === 'lookup' &&
                    nextStep.actionParameters.parameters.lookup_ds_id === datasetId;
            });
        }

        /**
         * @ngdoc method
         * @name initLookupDatasets
         * @methodOf data-prep.services.lookup.service:LookupService
         * @description init added datasets list which saved in localStorage
         */
        function initLookupDatasets() {
            var actionsToAdd = [];
            var addedDatasets = StorageService.getLookupDatasets();

            //Consolidate addedDatasets
            _.forEach(RecipeService.getRecipe(), function (nextStep) {
                if(nextStep.actionParameters.action === 'lookup'){ /*jshint camelcase: false */
                    if(_.indexOf(addedDatasets, nextStep.actionParameters.parameters.lookup_ds_id) === -1) { //If the dataset of a lookup step have not been saved
                        addedDatasets.push(nextStep.actionParameters.parameters.lookup_ds_id);
                    }
                }
            });
            StorageService.saveLookupDatasets(addedDatasets);

            _.forEach(addedDatasets, function(datasetId) {
                //init datasets list to add
                _.forEach(state.playground.lookup.datasets, function (datasetToAdd) {
                    if(datasetToAdd.id === datasetId) {
                        datasetToAdd.addedToLookup = true;
                    }
                });

                //init actions list
                var actionToAdd = _.find(state.playground.lookup.actions, function (action) {
                    return _.find(action.parameters, {'name': 'lookup_ds_id'}).default === datasetId;
                });
                if(actionToAdd){
                    actionsToAdd.push(actionToAdd);
                }
            });
            StateService.setLookupAddedActions(actionsToAdd);
        }

        /**
         * @ngdoc method
         * @name updateLookupDatasets
         * @methodOf data-prep.services.lookup.service:LookupService
         * @description Update added datasets list
         */
        function updateLookupDatasets() {

            var datasetsToAdd = _.chain(state.playground.lookup.datasets)
                .filter('addedToLookup')
                .value();

            var actionsToAdd = [];
            _.forEach(datasetsToAdd, function(dataset) {
                var actionToAdd = _.find(state.playground.lookup.actions, function (action) {
                    return _.find(action.parameters, {'name': 'lookup_ds_id'}).default === dataset.id;
                });

                if(actionToAdd){
                    actionsToAdd.push(actionToAdd);
                }
            });
            StateService.setLookupAddedActions(actionsToAdd);

            var datasetsToSave = _.pluck(datasetsToAdd, 'id');
            if(_.indexOf(StorageService.getLookupDatasets(), state.playground.dataset.id) > -1) { //If the playground dataset have been saved in localStorage for the lookup
                datasetsToSave.push(state.playground.dataset.id);
            }
            StorageService.saveLookupDatasets(datasetsToSave);
        }

        /**
         * @ngdoc method
         * @name disableDatasetsUsedInRecipe
         * @methodOf data-prep.services.lookup.service:LookupService
         * @description Disable datasets already used in a lookup step of the recipe to not to be removed
         */
        function disableDatasetsUsedInRecipe() {
            _.forEach(state.playground.lookup.datasets, function (dataset) {
                var lookupStep = _.find(RecipeService.getRecipe(), function (nextStep) { /*jshint camelcase: false */
                    return nextStep.actionParameters.action === 'lookup' && dataset.id === nextStep.actionParameters.parameters.lookup_ds_id;
                });

                dataset.enableToAddToLookup = lookupStep ? false : true;
            });
        }
    }

    angular.module('data-prep.services.lookup')
        .service('LookupService', LookupService);
})();