(function() {
    'use strict';

    function PlaygroundService(DatasetService, DatasetGridService, FilterService, RecipeService) {
        var self = this;
        self.visible = false;

        self.currentData = null;
        self.currentMetadata = null;
        self.currentPreparation = null;

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
            if(!self.currentMetadata || self.currentPreparation || dataset.id !== self.currentMetadata.id) {
                DatasetService.getDataFromId(dataset.id, false)
                    .then(function(data) {
                        self.currentMetadata = dataset;
                        self.currentData = data;

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
    }

    angular.module('data-prep.services.playground')
        .service('PlaygroundService', PlaygroundService);
})();