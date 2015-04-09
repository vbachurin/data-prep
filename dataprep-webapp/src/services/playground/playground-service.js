(function() {
    'use strict';

    function PlaygroundService($rootScope, $q, DatasetService, DatasetGridService, FilterService, RecipeService, PreparationService, MessageService) {
        var self = this;
        self.visible = false;

        self.currentData = null;
        self.currentMetadata = null;
        self.originalPreparationName = '';
        self.preparationName = '';

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------VISIBILITY--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Set visibility flag to true
         */
        self.show = function () {
            self.visible = true;
        };

        /**
         * Set visibility flag to false
         */
        self.hide = function () {
            self.visible = false;
        };

        //------------------------------------------------------------------------------------------------------
        //-------------------------------------------------INIT/LOAD--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Initiate a preparation.
         * If there is no preparation yet and the dataset to load is still the last loaded, the playground is not changed.
         * Otherwise, the playground is reset with the wanted dataset
         * @param dataset - the dataset to load
         * @return Promise
         */
        self.initPlayground = function(dataset) {
            if(!self.currentMetadata || PreparationService.currentPreparation || dataset.id !== self.currentMetadata.id) {
                return DatasetService.getDataFromId(dataset.id, false)
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
                        PreparationService.currentPreparation = null;

                        FilterService.removeAllFilters();
                        RecipeService.reset();
                        DatasetGridService.setDataset(dataset, data);
                    });
            }
            else {
                return $q.when(true);
            }
        };

        /**
         * Load an existing preparation in the playground :
         * - set name,
         * - set current preparation before any preparation request
         * - load grid with 'head' version content,
         * - reinit recipe panel with preparation steps
         * @param preparation - the preparation to load
         * @returns {*}
         */
        self.load = function(preparation) {
            self.preparationName = preparation.name;
            self.originalPreparationName = preparation.name;

            // Update current preparation id before preparation operations
            PreparationService.currentPreparation = preparation.id;

            $rootScope.$emit('talend.loading.start');
            return PreparationService.getContent('head')
                .then(function(response) {
                    self.currentMetadata = preparation.dataset;
                    self.currentData = response.data;

                    FilterService.removeAllFilters();
                    RecipeService.refresh();
                    DatasetGridService.setDataset(preparation.dataset, response.data);
                })
                .finally(function() {
                    $rootScope.$emit('talend.loading.stop');
                });
        };

        /**
         * Load an existing preparation in the playground, at a specific step.
         * WARNING : we consider that the preparation is already loaded, only an update is the grid is done
         * - set current preparation before any preparation request
         * - load grid with 'stepId' version content,
         * @param step - the step to load
         * @returns {*}
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
                    RecipeService.disableStepsAfter(step);
                    DatasetGridService.setDataset(self.currentMetadata, response.data);
                })
                .finally(function() {
                    $rootScope.$emit('talend.loading.stop');
                });
        };

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------PREPARATION-------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Create a new preparation or change its name if it already exists
         * @param name - the preparation name
         */
        self.createOrUpdatePreparation = function(name) {
            if(self.originalPreparationName !== name) {
                if(PreparationService.currentPreparation) {
                    PreparationService.update(name)
                        .then(function() {
                            self.originalPreparationName = name;
                            self.preparationName = name;
                        });
                }
                else {
                    PreparationService.create(self.currentMetadata.id, name)
                        .then(function() {
                            self.originalPreparationName = name;
                            self.preparationName = name;
                        });
                }
            }
        };
    }

    angular.module('data-prep.services.playground')
        .service('PlaygroundService', PlaygroundService);
})();