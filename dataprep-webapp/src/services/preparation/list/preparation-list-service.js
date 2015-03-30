(function() {
    'use strict';

    function PreparationListService(PreparationService, DatasetListService) {
        var self = this;
        self.preparations = [];

        var adaptMetadataInfos = function(preparations) {
            _.forEach(preparations, function(prep) {
                var correspondingDataset = _.find(DatasetListService.datasets, function(dataset) {
                    return dataset.id === prep.dataSetId;
                });
                prep.dataset = correspondingDataset;
            });
        };

        /**
         * Refresh preparation list
         */
        self.refreshPreparations = function() {
            PreparationService.getPreparations()
                .then(function(result) {
                    adaptMetadataInfos(result.data);
                    self.preparations = result.data;
                });
        };
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationListService', PreparationListService);
})();