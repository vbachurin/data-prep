(function() {
    'use strict';

    function PreparationListService($q, PreparationService, DatasetListService) {
        var self = this;
        self.preparations = [];

        /**
         * Bind the corresponding dataset to every preparation
         * @param preparations
         */
        var adaptMetadataInfos = function(preparations, datasets) {
            _.forEach(preparations, function(prep) {
                var correspondingDataset = _.find(datasets, function(dataset) {
                    return dataset.id === prep.dataSetId;
                });
                prep.dataset = correspondingDataset;
            });
        };

        /**
         * Refresh preparation list
         */
        self.refreshPreparations = function() {
            return $q.all([PreparationService.getPreparations(), DatasetListService.getDatasetsPromise()])
                .then(function(results) {
                    var preparationResult = results[0];
                    var datasets = results[1];
                    adaptMetadataInfos(preparationResult.data, datasets);
                    self.preparations = preparationResult.data;

                    return self.preparations;
                });
        };

        /**
         * Return preparation promise that resolve current preparation list if not empty, or call GET service
         * @returns Promise
         */
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