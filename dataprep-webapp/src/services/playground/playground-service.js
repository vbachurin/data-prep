(function() {
    'use strict';

    function PlaygroundService($rootScope, DatasetService, DatasetGridService, FilterService, RecipeService, PreparationService) {
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
         */
        self.initPlayground = function(dataset) {
            if(!self.currentMetadata || PreparationService.currentPreparation || dataset.id !== self.currentMetadata.id) {
                DatasetService.getDataFromId(dataset.id, false)
                    .then(function(data) {
                        self.currentMetadata = dataset;
                        self.currentData = data;
                        self.preparationName = '';
                        self.originalPreparationName = '';
                        PreparationService.currentPreparation = null;

                        FilterService.removeAllFilters();
                        RecipeService.reset();
                        DatasetGridService.setDataset(dataset, data);
                        self.show();
                    });
            }
            else {
                self.show();
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
            self.preparationName = preparation.name || '';
            self.originalPreparationName = preparation.name || '';

            // Update current preparation id before preparation operations
            PreparationService.currentPreparation = preparation.id;
            //TODO get all content (with columns)
            var loadPreparation = function(columns) {
                $rootScope.$emit('talend.loading.start');
                return PreparationService.getContent('head')
                    .then(function(response) {
                        self.currentMetadata = preparation.dataset;
                        self.currentData = response.data;

                        //TODO : don't need that when response.data will contain columns
                        var data = response.data;
                        data.columns = columns;

                        FilterService.removeAllFilters();
                        RecipeService.refresh();
                        DatasetGridService.setDataset(preparation.dataset, data);

                        self.show();
                    })
                    .finally(function() {
                        $rootScope.$emit('talend.loading.stop');
                    });
            };

            //TODO : remove that and return directly loadPreparation when backend service can return metadata and columns
            //Temporary fix : Get columns from dataset id.
            return DatasetService.getDataFromId(preparation.dataSetId, true)
                .then(function(response) {
                    var columns = response.columns;
                    return loadPreparation(columns);
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