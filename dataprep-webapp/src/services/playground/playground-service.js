(function() {
    'use strict';

    function PlaygroundService(DatasetService, DatasetGridService, FilterService, RecipeService, PreparationService) {
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
        //---------------------------------------------------INIT-----------------------------------------------
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

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------PREPARATION-------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Create a new preparation or change its name if it already exists
         * @param name - the preparation name
         */
        self.createOrUpdatePreparation = function(name) {
            if(PreparationService.currentPreparation && self.originalPreparationName !== name) {
                //TODO change name if different from current name
            }
            else {
                PreparationService.create(self.currentMetadata.id, name);
            }
        };
    }

    angular.module('data-prep.services.playground')
        .service('PlaygroundService', PlaygroundService);
})();