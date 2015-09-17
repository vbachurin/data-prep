(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.playground.service:PlaygroundService
     * @description Playground service. This service provides the entry point to load properly the playground
     * @requires data-prep.services.dataset.service:DatasetService
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.playground.service:PreviewService
     * @requires data-prep.services.filter.service:FilterService
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.transformation.service:TransformationCacheService
     * @requires data-prep.services.transformation.service:SuggestionService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.utils.service:MessageService
     * @requires data-prep.services.statistics.service:StatisticsService
     * @requires data-prep.services.history.service:HistoryService
     * @requires data-prep.services.state.service:StateService
     * @requires data-prep.services.onboarding:OnboardingService
     */
    function PlaygroundService($rootScope, $q, state, DatasetService, DatagridService, PreviewService, FilterService,
                               RecipeService, TransformationCacheService, SuggestionService, PreparationService,
                               MessageService, StatisticsService, HistoryService, StateService, OnboardingService) {
        var DEFAULT_NAME = 'Preparation draft';

        var service = {
            /**
             * @ngdoc property
             * @name originalPreparationName
             * @propertyOf data-prep.services.playground.service:PlaygroundService
             * @description the original preparation name - used to check if the name has changed
             */
            originalPreparationName: '',
            /**
             * @ngdoc property
             * @name preparationName
             * @propertyOf data-prep.services.playground.service:PlaygroundService
             * @description the current preparation
             */
            preparationName: '',
            /**
             * @ngdoc property
             * @name selectedSampleSize
             * @methodOf data-prep.services.playground.service:PlaygroundService
             * @description the selected sample size.
             */
            selectedSampleSize:{},

            //init/load
            initPlayground: initPlayground,     // load dataset
            load: load,                         // load preparation
            loadStep: loadStep,                 // load preparation step
            changeSampleSize: changeSampleSize, // load dataset/preparation with a sample size

            // dataset
            updateColumn: updateColumn,
            addUpdateColumnTypeStep: addUpdateColumnTypeStep,
            addUpdateColumnDomainStep: addUpdateColumnDomainStep,

            //preparation
            createOrUpdatePreparation: createOrUpdatePreparation,
            appendStep: appendStep,
            updateStep: updateStep,
            removeStep: removeStep,
            editCell: editCell
        };
        return service;

        //------------------------------------------------------------------------------------------------------
        //-------------------------------------------------INIT/LOAD--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        function reset(dataset, data, preparation) {
            StateService.resetPlayground();
            StateService.setCurrentDataset(dataset);
            StateService.setCurrentPreparation(preparation);

            FilterService.removeAllFilters();
            RecipeService.refresh();
            StatisticsService.resetCharts();
            DatagridService.setDataset(dataset, data);
            TransformationCacheService.invalidateCache();
            SuggestionService.reset();
            HistoryService.clear();
            PreviewService.reset(false);
        }

        function setName(name) {
            service.preparationName = name;
            service.originalPreparationName = name;
        }

        /**
         * @ngdoc method
         * @name initPlayground
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} dataset The dataset to load
         * @description Initiate a new preparation from dataset.
         - If there is no preparation yet and the dataset to load is still the last loaded, the playground is not changed.
         - Otherwise, the playground is reset with the wanted dataset
         * @returns {Promise} The process promise
         */
        function initPlayground(dataset) {
            if(!state.playground.dataset || state.playground.preparation || dataset.id !== state.playground.dataset.id) {

                return DatasetService.getContent(dataset.id, false, service.selectedSampleSize.value)
                    .then(function(data) {
                        //TODO : temporary fix because asked to.
                        //TODO : when error status during import and get dataset content is managed by backend,
                        //TODO : remove this controle and the 'data-prep.services.utils'/MessageService dependency
                        if(!data || !data.records) {
                            MessageService.error('INVALID_DATASET_TITLE', 'INVALID_DATASET');
                            throw Error('Empty data');
                        }

                        setName('');
                        reset(dataset, data);
                        StateService.hideRecipe();
                        StateService.setNameEditionMode(true);

                        return data;
                    })
                    .then(function(data) {
                        if(OnboardingService.shouldStartTour('playground')) {
                            StateService.setGridSelection(data.columns[0]);
                            setTimeout(OnboardingService.startTour.bind(null, 'playground'), 200);
                        }
                    });
            }
            else {
                return $q.when(true);
            }
        }

        /**
         * @ngdoc method
         * @name changeSampleSize
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @description change the sample size
         * @returns {Promise} The process promise
         */
         function changeSampleSize() {
            // deal with preparation or dataset
            if (state.playground.preparation) {
                return changePreparationSampleSize();
            }
            else {
                return changeDataSetSampleSize();
            }
        }

        /**
         * @ngdoc method
         * @name changeDataSetSampleSize
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {int} the wanted sample size
         * @description change the sample size for the dataset.
         * @returns {Promise} The process promise
         */
        function changePreparationSampleSize() {
            // get the current step
            var index = RecipeService.getActiveThresholdStepIndex();
            var step = RecipeService.getStep(index);

            $rootScope.$emit('talend.loading.start');

            return PreparationService.getContent(state.playground.preparation.id, step.transformation.stepId, service.selectedSampleSize.value)
                .then(function(response) {
                    DatagridService.updateData(response.data);
                })
                .finally(function() {
                    $rootScope.$emit('talend.loading.stop');
                });
        }

        /**
         * @ngdoc method
         * @name changePreparationSampleSize
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {int} the wanted sample size
         * @description change the sample size for the preparation.
         * @returns {Promise} The process promise
         */
        function changeDataSetSampleSize() {
            $rootScope.$emit('talend.loading.start');
            return DatasetService.getContent(state.playground.dataset.id, true, service.selectedSampleSize.value)
                .then(function (data) {
                    //TODO : temporary fix because asked to.
                    //TODO : when error status during import and get dataset content is managed by backend,
                    //TODO : remove this controle and the 'data-prep.services.utils'/MessageService dependency
                    if (!data || !data.records) {
                        MessageService.error('INVALID_DATASET_TITLE', 'INVALID_DATASET');
                        throw Error('Empty data');
                    }
                    DatagridService.updateData(data);
                })
                .finally(function() {
                    $rootScope.$emit('talend.loading.stop');
                });
        }

        /**
         * @ngdoc method
         * @name load
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} preparation - the preparation to load
         * @description Load an existing preparation in the playground :
         <ul>
            <li>set name</li>
            <li>set current preparation before any preparation request</li>
            <li>load grid with 'head' version content</li>
            <li>reinit recipe panel with preparation steps</li>
         </ul>
         * @returns {Promise} - the process promise
         */
        function load(preparation) {
            if(!state.playground.preparation || state.playground.preparation.id !== preparation.id) {

                $rootScope.$emit('talend.loading.start');
                return PreparationService.getContent(preparation.id, 'head', service.selectedSampleSize.value)
                    .then(function(response) {
                        setName(preparation.name);
                        reset(preparation.dataset ? preparation.dataset : {id: preparation.dataSetId}, response.data, preparation);
                        StateService.showRecipe();
                        StateService.setNameEditionMode(false);
                    })
                    .finally(function() {
                        $rootScope.$emit('talend.loading.stop');
                    });
            }
            else {
                return $q.when(true);
            }
        }

        /**
         * @ngdoc method
         * @name loadStep
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} step The preparation step to load
         * @param {string} focusColumnId The column id to focus on
         * @description Load a specific step content in the current preparation, and update the recipe
         * @returns {Promise} The process promise
         */
        function loadStep(step) {
            //step already loaded
            if(RecipeService.getActiveThresholdStep() === step) {
                return;
            }

            $rootScope.$emit('talend.loading.start');
            return PreparationService.getContent(state.playground.preparation.id, step.transformation.stepId, service.selectedSampleSize.value)
                .then(function(response) {
                    DatagridService.updateData(response.data);
                    RecipeService.disableStepsAfter(step);
                    PreviewService.reset(false);
                })
                .finally(function() {
                    $rootScope.$emit('talend.loading.stop');
                });
        }

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------PREPARATION-------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name createOrUpdatePreparation
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} name The preparation name to create or update
         * @description Create a new preparation or change its name if it already exists
         * @returns {Promise} The process promise
         */
        function createOrUpdatePreparation(name) {
            if(service.originalPreparationName !== name) {
                var promise = state.playground.preparation ?
                    PreparationService.setName(state.playground.preparation.id, name) :
                    PreparationService.create(state.playground.dataset.id, name);

                return promise.then(function(preparation) {
                    StateService.setCurrentPreparation(preparation);
                    service.originalPreparationName = name;
                    service.preparationName = name;
                    return preparation;
                });
            }
            else {
                return $q.reject('name unchanged');
            }
        }

        /**
         * @ngdoc method
         * @name setPreparationHead
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} preparationId The preparation id
         * @param {string} headId The head id to set
         * @param {string} columnToFocus The column id to focus
         * @description Move the preparation head to the specified step
         * @returns {promise} The process promise
         */
        function setPreparationHead(preparationId, headId, columnToFocus) {
            $rootScope.$emit('talend.loading.start');

            var promise = PreparationService.setHead(preparationId, headId);

            //load a specific step, we must load recipe first to get the step id to load. Then we load grid at this step.
            if(RecipeService.getLastActiveStep() !== RecipeService.getLastStep()) {
                var lastActiveStepIndex = RecipeService.getActiveThresholdStepIndex();
                promise = promise.then(updateRecipe)
                    // The grid update cannot be done in parallel because the update change the steps ids
                    // We have to wait for the recipe update to complete
                    .then(function() {
                        var activeStep = RecipeService.getStep(lastActiveStepIndex, true);
                        return loadStep(activeStep);
                    });
            }
            //load the recipe and grid head in parallel
            else {
                promise = promise.then(function() {
                    return $q.all([updateRecipe(), updatePreparationDatagrid(columnToFocus)]);
                });
            }

            return promise.finally(function () {
                $rootScope.$emit('talend.loading.stop');
            });
        }

        /**
         * @ngdoc method
         * @name appendStep
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} action The action name
         * @param {object} parameters The transformation parameters
         * @description Call an execution of a transformation on the column in the current preparation and add an entry
         * in actions history. It there is no preparation yet, it is created first and tagged as draft.
         */
        function appendStep(action, parameters) {
            /*jshint camelcase: false */
            $rootScope.$emit('talend.loading.start');

            //create the preparation and taf it draft if it does not exist
            var prepCreation = state.playground.preparation ?
                $q.when(state.playground.preparation) :
                createOrUpdatePreparation(DEFAULT_NAME)
                    .then(function(preparation) {
                        preparation.draft = true;
                        setName('');
                        return preparation;
                    });

            return prepCreation
                //append step
                .then(function(preparation) {
                    return PreparationService.appendStep(preparation.id, {action: action, parameters: parameters});
                })
                //update recipe and datagrid
                .then(function(){
                    var columnToFocus = parameters.column_id;
                    return $q.all([updateRecipe(), updatePreparationDatagrid(columnToFocus)]);
                })
                //add entry in history for undo/redo
                .then(function() {
                    var actualHead = RecipeService.getLastStep();
                    var previousHead = RecipeService.getPreviousStep(actualHead);

                    var undo = setPreparationHead.bind(service, state.playground.preparation.id, previousHead.transformation.stepId);
                    var redo = setPreparationHead.bind(service, state.playground.preparation.id, actualHead.transformation.stepId, parameters.column_id);
                    HistoryService.addAction(undo, redo);
                })
                //hide loading screen
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        }

        /**
         * @ngdoc method
         * @name updateStep
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} step The step to update
         * @param {object} newParams The new parameters
         * @description Call an execution of a transformation update on the provided step and add an entry in the
         * actions history
         */
        function updateStep(step, newParams) {
            /*jshint camelcase: false */
            $rootScope.$emit('talend.loading.start');

            //save the head before transformation for undo
            var previousHead = RecipeService.getLastStep().transformation.stepId;
            //save the last active step index to load this step after update
            var lastActiveStepIndex = RecipeService.getActiveThresholdStepIndex();

            return PreparationService.updateStep(state.playground.preparation.id, step, newParams)
                .then(updateRecipe)
                //get step id to load and update datagrid with it
                .then(function() {
                    var activeStep = RecipeService.getStep(lastActiveStepIndex, true);
                    return loadStep(activeStep);
                })
                //add entry in history for undo/redo
                .then(function() {
                    var actualHead = RecipeService.getLastStep().transformation.stepId;
                    var undo = setPreparationHead.bind(service, state.playground.preparation.id, previousHead);
                    var redo = setPreparationHead.bind(service, state.playground.preparation.id, actualHead, newParams.column_id);
                    HistoryService.addAction(undo, redo);
                })
                //hide loading screen
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        }

        /**
         * @ngdoc method
         * @name removeStep
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} step The step to delete
         * @description Call an execution of a transformation on the column in the current preparation and add an entry
         * in actions history
         */
        function removeStep(step) {
            /*jshint camelcase: false */
            $rootScope.$emit('talend.loading.start');

            //save the head before transformation for undo
            var previousHead = RecipeService.getLastStep().transformation.stepId;

            return PreparationService.removeStep(state.playground.preparation.id, step.transformation.stepId)
                //update recipe and datagrid
                .then(function() {
                    return $q.all([updateRecipe(), updatePreparationDatagrid()]);
                })
                //add entry in history for undo/redo
                .then(function() {
                    var actualHead = RecipeService.getLastStep().transformation.stepId;
                    var undo = setPreparationHead.bind(service, state.playground.preparation.id, previousHead, step.actionParameters.parameters.column_id);
                    var redo = setPreparationHead.bind(service, state.playground.preparation.id, actualHead);
                    HistoryService.addAction(undo, redo);
                })
                //hide loading screen
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        }

        /**
         * @ngdoc method
         * @name editCell
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {Object} rowItem The row
         * @param {object} column The column where to execute the transformation
         * @param {string} newValue The new value to put on th target
         * @param {boolean} updateAllCellWithValue Indicates the scope (cell or column) of the transformaton
         * @description Perform a cell or a column edition
         */
        function editCell(rowItem, column, newValue, updateAllCellWithValue) {
            /*jshint camelcase: false */
            var params = {
                scope : updateAllCellWithValue ? 'column' : 'cell',
                column_id: column.id,
                column_name: column.name,
                row_id: rowItem.tdpId,
                cell_value: rowItem[column.id],
                replace_value: newValue
            };
            var action = 'replace_on_value';

            return appendStep(action, params);
        }

        //------------------------------------------------------------------------------------------------------
        //--------------------------------------------------DATASET---------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name updateColumn
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} columnId The column id to focus update
         * @param {string} type the new type of the column
         * @param {string} domain the new domain of the column
         * @description Perform an datagrid refresh with the preparation head
         */
        function updateColumn(columnId, type, domain) {
            return DatasetService.updateColumn(state.playground.dataset.id, columnId, {type: type, domain: domain})
                .then(function() {
                          if (state.playground.preparation) {
                              return updatePreparationDatagrid();
                          }
                          else {
                              return updateDatasetDatagrid();
                          }
                      });

        }

        /**
         * @ngdoc method
         * @name addUpdateColumnTypeStep
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} columnId The column id to focus update
         * @param {string} type the new type of the column
         * @description Add step which change column type
         */
        function addUpdateColumnTypeStep(columnId, type) {
            return appendStep('type_change',
                {
                    'scope':'column',
                    'column_id': columnId,
                    'NEW_TYPE':type.id
                })
                .then(function() {
                    // if preparation
                    if (state.playground.preparation) {
                        return updatePreparationDatagrid();
                    }
                    // dataset
                    else {
                        return updateDatasetDatagrid();
                    }
                });
        }

        /**
         * @ngdoc method
         * @name addUpdateColumnDomainStep
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} columnId The column id to focus update
         * @param {string} domain the new domain of the column
         * @description Add step which change column domain
         */
        function addUpdateColumnDomainStep(columnId, domain) {
            return appendStep('domain_change',
                {
                    'scope':'column',
                    'column_id': columnId,
                    'NEW_DOMAIN_ID':domain.id,
                    'NEW_DOMAIN_LABEL': domain.label,
                    'NEW_DOMAIN_FREQUENCY': domain.frequency
                })
                .then(function() {
                  // if preparation
                  if (state.playground.preparation) {
                      return updatePreparationDatagrid();
                  }
                  // dataset
                  else {
                      return updateDatasetDatagrid();
                  }
                });
        }

        //------------------------------------------------------------------------------------------------------
        //---------------------------------------------------UTILS----------------------------------------------
        //------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name updatePreparationDatagrid
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} focusColumnId The column id to focus on
         * @description Perform an datagrid refresh with the preparation head
         */
        function updatePreparationDatagrid() {
            return PreparationService.getContent(state.playground.preparation.id, 'head', service.selectedSampleSize.value)
                .then(function(response) {
                    DatagridService.updateData(response.data);
                    PreviewService.reset(false);
                });
        }

        /**
         * @ngdoc method
         * @name updateDatasetDatagrid
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @description Perform an datagrid refresh on the current dataset
         */
        function updateDatasetDatagrid() {
            return DatasetService.getContent(state.playground.dataset.id, false, service.selectedSampleSize.value)
                .then(function(response) {
                    DatagridService.updateData(response);
                });
        }

        /**
         * @ngdoc method
         * @name updateRecipe
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @description Update the recipe
         */
        function updateRecipe() {
            return RecipeService.refresh()
                .then(function() {
                    if(RecipeService.getRecipe().length === 1) { //first step append
                        StateService.showRecipe();
                    }
                });
        }
    }

    angular.module('data-prep.services.playground')
        .service('PlaygroundService', PlaygroundService);
})();