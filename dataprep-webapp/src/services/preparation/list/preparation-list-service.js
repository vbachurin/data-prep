(function() {
    'use strict';

    function PreparationListService($q, PreparationService, DatasetListService) {
        var self = this;
        self.preparations = [];

        /**
         * Wait for datasets, and bind the corresponding dataset to every preparation
         * @param preparations
         */
        var adaptMetadataInfos = function(preparations) {
            DatasetListService.getDatasetsPromise()
                .then(function(datasets) {
                    _.forEach(preparations, function(prep) {
                        var correspondingDataset = _.find(datasets, function(dataset) {
                            return dataset.id === prep.dataSetId;
                        });
                        prep.dataset = correspondingDataset;
                    });
                });
        };

        /**
         * Refresh preparation list
         */
        self.refreshPreparations = function() {
            return PreparationService.getPreparations()
                .then(function(result) {
                    adaptMetadataInfos(result.data);
                    self.preparations = result.data;

                    return self.preparations;
                });
        };

        self.getPreparationsPromise = function() {
            if(self.preparations.length) {
                return $q.when(self.preparations);
            }
            else {
                return self.refreshPreparations();
            }
        };
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationListService', PreparationListService);
})();