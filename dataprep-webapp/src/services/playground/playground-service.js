(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.playground.service:PlaygroundService
     * @description Playground service. This service provides the entry point to load properly the playground
     * @requires data-prep.services.dataset.service:DatasetService
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.filter.service:FilterService
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.utils.service:MessageService
     * @requires data-prep.services.statistics:StatisticsService
     */
    function PlaygroundService($rootScope, $q, DatasetService, DatagridService, FilterService, RecipeService, PreparationService, MessageService, StatisticsService) {
        var self = this;

        /**
         * @ngdoc property
         * @name visible
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description the visibility control
         */
        self.visible = false;

        /**
         * @ngdoc property
         * @name currentData
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description the loaded data
         */
        self.currentData = null;

        /**
         * @ngdoc property
         * @name currentMetadata
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description the loaded metadata
         */
        self.currentMetadata = null;

        /**
         * @ngdoc property
         * @name originalPreparationName
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description the original preparation name - used to check if the name has changed
         */
        self.originalPreparationName = '';

        /**
         * @ngdoc property
         * @name preparationName
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description the current preparation
         */
        self.preparationName = '';

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------VISIBILITY--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name show
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @description Display the playground
         */
        self.show = function () {
            self.visible = true;
        };

        /**
         * @ngdoc method
         * @name hide
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @description Hide the playground
         */
        self.hide = function () {
            self.visible = false;
        };

        //------------------------------------------------------------------------------------------------------
        //-------------------------------------------------INIT/LOAD--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name initPlayground
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} dataset - the dataset to load
         * @description Initiate a new preparation from dataset.
         - If there is no preparation yet and the dataset to load is still the last loaded, the playground is not changed.
         - Otherwise, the playground is reset with the wanted dataset
         * @returns {Promise} - the process promise
         */
        self.initPlayground = function(dataset) {
            if(!self.currentMetadata || PreparationService.currentPreparationId || dataset.id !== self.currentMetadata.id) {
                return DatasetService.getContent(dataset.id, false)
                    .then(function(data) {
                        //TODO : temporary fix because asked to.
                        //TODO : when error status during import and get dataset content is managed by backend,
                        //TODO : remove this controle and the 'data-prep.services.utils'/MessageService dependency
                        if(!data || !data.records) {
                            MessageService.error('INVALID_DATASET_TITLE', 'INVALID_DATASET');
                            throw Error('Empty data');
                        }

                        self.currentMetadata = dataset;
                        self.currentData = data;
                        self.preparationName = '';
                        self.originalPreparationName = '';
                        PreparationService.currentPreparationId = null;

                        FilterService.removeAllFilters();
                        RecipeService.reset();
                        StatisticsService.resetCharts();
                        DatagridService.setDataset(dataset, data);
                    });
            }
            else {
                return $q.when(true);
            }
        };

        /**
         * @ngdoc method
         * @name load
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} preparation - the preparation to load
         * @description Load an existing preparation in the playground :
          - set name,
          - set current preparation before any preparation request
          - load grid with 'head' version content,
          - reinit recipe panel with preparation steps
         * @returns {Promise} - the process promise
         */
        self.load = function(preparation) {
            if(PreparationService.currentPreparationId !== preparation.id) {
                self.preparationName = preparation.name;
                self.originalPreparationName = preparation.name;

                // Update current preparation id before preparation operations
                PreparationService.currentPreparationId = preparation.id;

                $rootScope.$emit('talend.loading.start');
                return PreparationService.getContent('head')
                    .then(function(response) {
                        self.currentMetadata = {id: preparation.datasetId};
                        self.currentData = response.data;

                        FilterService.removeAllFilters();
                        RecipeService.refresh();
                        StatisticsService.resetCharts();
                        DatagridService.setDataset(preparation.dataset, response.data);
                    })
                    .finally(function() {
                        $rootScope.$emit('talend.loading.stop');
                    });
            }
            else {
                return $q.when(true);
            }
        };

        /**
         * @ngdoc method
         * @name loadStep
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} step - the preparation step to load
         * @description Load a specific step content in the current preparation, and update the recipe
         * @returns {promise} - the process promise
         */
        self.loadStep = function(step) {
            //step already loaded
            if(RecipeService.getActiveThresholdStep() === step) {
                return;
            }

            $rootScope.$emit('talend.loading.start');
            return PreparationService.getContent(step.transformation.stepId)
                .then(function(response) {
                    self.currentData = response.data;
                    DatagridService.setDataset(self.currentMetadata, response.data);
                    RecipeService.disableStepsAfter(step);
                })
                .finally(function() {
                    $rootScope.$emit('talend.loading.stop');
                });
        };

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
        self.createOrUpdatePreparation = function(name) {
            if(self.originalPreparationName !== name) {
                return PreparationService.setName(self.currentMetadata, name)
                    .then(function() {
                        self.originalPreparationName = name;
                        self.preparationName = name;
                    });
            }
            else {
                return $q.reject();
            }
        };

        /**
         * @ngdoc method
         * @name transform
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} action The action name
         * @param {object} column The columns metadata
         * @param {object} params The transformation params
         * @description Perform a transformation on the column in the current preparation, refresh the recipe and the
         * data. If there is no preparation yet, PreparationService create it.
         */
        self.appendStep = function(action, column, params) {
            $rootScope.$emit('talend.loading.start');
            return PreparationService.appendStep(self.currentMetadata, action, column, params)
                .then(function() {
                    return PreparationService.getContent('head');
                })
                .then(function(response) {
                    DatagridService.updateData(response.data);
                    RecipeService.refresh();
                })
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        };
    }

    angular.module('data-prep.services.playground')
        .service('PlaygroundService', PlaygroundService);
})();