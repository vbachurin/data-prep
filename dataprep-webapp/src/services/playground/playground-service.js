(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.playground.service:PlaygroundService
     * @description Playground service. This service provides the entry point to load properly the playground
     * @requires data-prep.services.dataset.service:DatasetRestService
     * @requires data-prep.services.dataset.service:DatasetGridService
     * @requires data-prep.services.filter.service:FilterService
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.preparation.service:PreparationRestService
     * @requires data-prep.services.utils.service:MessageService
     */
    function PlaygroundService($rootScope, $q, DatasetRestService, DatasetGridService, FilterService, RecipeService, PreparationRestService, MessageService) {
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
         * @returns {promise} - the process promise
         */
        self.initPlayground = function(dataset) {
            if(!self.currentMetadata || PreparationRestService.currentPreparation || dataset.id !== self.currentMetadata.id) {
                return DatasetRestService.getDataFromId(dataset.id, false)
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
                        PreparationRestService.currentPreparation = null;

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
         * @ngdoc method
         * @name load
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} preparation - the preparation to load
         * @description Load an existing preparation in the playground :
          - set name,
          - set current preparation before any preparation request
          - load grid with 'head' version content,
          - reinit recipe panel with preparation steps
         * @returns {promise} - the process promise
         */
        self.load = function(preparation) {
            self.preparationName = preparation.name;
            self.originalPreparationName = preparation.name;

            // Update current preparation id before preparation operations
            PreparationRestService.currentPreparation = preparation.id;

            $rootScope.$emit('talend.loading.start');
            return PreparationRestService.getContent('head')
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
            return PreparationRestService.getContent(step.transformation.stepId)
                .then(function(response) {
                    self.currentData = response.data;
                    DatasetGridService.setDataset(self.currentMetadata, response.data);
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
         * @param {string} name - the preparation name to create or update
         * @description Create a new preparation or change its name if it already exists
         * @returns {promise} - the process promise
         */
        self.createOrUpdatePreparation = function(name) {
            if(self.originalPreparationName !== name) {
                if(PreparationRestService.currentPreparation) {
                    PreparationRestService.update(name)
                        .then(function() {
                            self.originalPreparationName = name;
                            self.preparationName = name;
                        });
                }
                else {
                    PreparationRestService.create(self.currentMetadata.id, name)
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