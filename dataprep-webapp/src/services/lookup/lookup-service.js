(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.lookup.service:LookupService
     * @description Lookup service. This service provide the entry point to load lookup content
     * @requires data-prep.services.transformations.service:TransformationRestService
     * @requires data-prep.services.datasets.service:DatasetRestService
     * @requires data-prep.services.state.service:StateService
     * @requires data-prep.services.recipe.service:RecipeService
     */
    function LookupService($q, state, TransformationRestService, DatasetRestService, StateService, RecipeService) {
        return {
            loadContent: loadContent,
            setUpdateMode: setUpdateMode,
            setAddMode: setAddMode,
            loadLookupPanel: loadLookupPanel
        };

        /**
         * @ngdoc method
         * @name loadContent
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookup The lookup action
         * @description Loads the lookup dataset content
         */
        function loadContent(lookup) {
            DatasetRestService.getContentFromUrl(getDsUrl(lookup))
                .then(function (lookupDsContent) {
                    loadLookupParameters(lookup, lookupDsContent, state.playground.lookup.step);
                });
        }

        /**
         * @ngdoc method
         * @name getActions
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {string} datasetId The dataset id
         * @description Loads the possible lookup datasets
         */
        function getActions(datasetId) {
            if(state.playground.lookup.actions.length) {
                return $q.when(state.playground.lookup.actions);
            } else {
                return TransformationRestService.getDatasetTransformations(datasetId)
                    .then(function (lookup) {
                        StateService.setLookupActions(lookup.data);
                        return lookup.data;
                    });
            }
        }

        /**
         * @ngdoc method
         * @name getDsUrl
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookup dataset lookup action
         * @returns {String} The url of the lookup dataset
         * @description Extract the dataset url from lookup
         */
        function getDsUrl(lookup) {
            return _.find(lookup.parameters, {'name': 'lookup_ds_url'}).default;
        }


        /**
         * @ngdoc method
         * @name getDataset
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookup dataset lookup action
         * @param {object} step the lookup step
         * @returns {object} The dataset used in lookup step
         * @description Check if the dataset is used in lookup step
         */
        function getDataset(lookup, step) {
            /*jshint camelcase: false */
            return _.find(lookup.parameters, {
                name: 'lookup_ds_id',
                default: step.actionParameters.parameters.lookup_ds_id
            });
        }


        /**
         * @ngdoc method
         * @name getSelectedColumnLookupStep
         * @methodOf data-prep.services.lookup.service:LookupService
         * @returns {Object} The last lookup step relative to the selected column
         * @description Get the last lookup step relative to the selected column
         */
        function getSelectedColumnLookupStep() {
            var recipeList = RecipeService.getRecipe();
            return _.findLast(recipeList, function (recipe) {
                return recipe.column.id === state.playground.grid.selectedColumn.id && recipe.transformation.name === 'lookup';
            });
        }

        /**
         * @ngdoc method
         * @name loadLookupPanel
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {boolean} tooglePanel toogle pannel display if true
         * @description Toogle pannel display if true
         */
        function loadLookupPanel(tooglePanel) {
            var lookupStep = getSelectedColumnLookupStep();
            if (lookupStep) {
                setUpdateMode(lookupStep, tooglePanel);
            } else {
                setAddMode(tooglePanel);
            }
        }

        /**
         * @ngdoc method
         * @name loadLookupPanelContent
         * @methodOf data-prep.services.lookup.service:LookupService
         * @description Load lookup panel content
         */
        function loadLookupPanelContent() {
            getActions(state.playground.dataset.id)
                .then(function (lookupActions) {
                    if (lookupActions.length) {
                        if(state.playground.lookup.step){
                            var lookupDataset = _.find(lookupActions, function (action) {
                                return getDataset(action, state.playground.lookup.step);
                            });
                            if(lookupDataset){
                                loadContent(lookupDataset);
                            } else {
                                loadContent(lookupActions[0]);
                            }
                        } else {
                            loadContent(lookupActions[0]);
                        }
                    }
                });
        }

        /**
         * @ngdoc method
         * @name setAddMode
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {boolean} tooglePanel toogle pannel display if true
         * @description initialize mode to update a lookup step
         */
        function setAddMode(tooglePanel) {
            if(tooglePanel){
                StateService.setLookupVisibility(!state.playground.lookup.visibility);
            }
            StateService.setUpdatingLookupStep(false);
            StateService.setLookupStep(null);

            if (state.playground.lookup.visibility) {
                loadLookupPanelContent();
            }
        }

        /**
         * @ngdoc method
         * @name setUpdateMode
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} step a lookup step
         * @param {boolean} tooglePanel toogle pannel display if true
         * @description initialize mode to update a lookup step
         */
        function setUpdateMode(step, tooglePanel) {
            if(tooglePanel){
                if (!state.playground.lookup.step) {
                    if (!state.playground.lookup.visibility) {
                        StateService.setLookupVisibility(!state.playground.lookup.visibility);
                    }
                } else {
                    if (state.playground.lookup.step.transformation.stepId === step.transformation.stepId) {
                        StateService.setLookupVisibility(!state.playground.lookup.visibility);
                    } else {
                        StateService.setLookupVisibility(true);
                    }
                }
            }

            StateService.setLookupUpdateMode();
            StateService.setLookupStep(step);

            if (state.playground.lookup.visibility) {
                loadLookupPanelContent();
            }
        }


        /**
         * @ngdoc method
         * @name loadLookupParameters
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookupDsContent a lookup data
         * @param {object} step a lookup step
         * @description initialize upload mode with existing step parameters
         */

        function loadLookupParameters(lookup, lookupDsContent, step) {
            /*jshint camelcase: false */
            StateService.setLookupDataset(lookup);
            StateService.setCurrentLookupData(lookupDsContent);

            //Load old lookup parameters when changing lookup dataset
            if (state.playground.lookup.isUpdatingLookupStep && getDataset(lookup, state.playground.lookup.step)) { /*jshint camelcase: false */
                StateService.setLookupSelectedColumn(_.find(lookupDsContent.metadata.columns, {id: step.actionParameters.parameters.lookup_join_on}));

                _.forEach(state.playground.lookup.columnCheckboxes, function (columnCheckbox) {
                    if (_.find(step.actionParameters.parameters.lookup_selected_cols, {id: columnCheckbox.id})) {
                        columnCheckbox.isAdded = true;
                    }
                });
                StateService.updateLookupColumnsToAdd();

                StateService.setGridSelection(_.find(state.playground.data.metadata.columns, {id: step.actionParameters.parameters.column_id}));
            }
        }

    }

    angular.module('data-prep.services.lookup')
        .service('LookupService', LookupService);
})();