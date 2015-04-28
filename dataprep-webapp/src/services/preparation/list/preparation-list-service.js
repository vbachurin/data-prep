(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.preparation.service:PreparationListService
     * @description Preparation list service. This service holds the preparations list and adapt them for the application.
      It uses PreparationService to get the preparations, and DatasetListService to get the datasets
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.dataset.service:DatasetListService
     */
    function PreparationListService($q, PreparationService, DatasetListService) {
        var self = this;

        /**
         * @ngdoc property
         * @name preparations
         * @propertyOf data-prep.services.preparation.service:PreparationListService
         * @description the preparations list
         */
        self.preparations = [];

        /**
         * @ngdoc method
         * @name adaptMetadataInfos
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {object[]} preparations - the preparations to adapt
         * @param {object[]} datasets - the datasets to inject
         * @description [PRIVATE] Inject the corresponding dataset to every preparation
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
         * @ngdoc method
         * @name refreshPreparations
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @description Refresh the preparations list
         * @returns {Promise} - the process promise
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
         * @ngdoc method
         * @name getPreparationsPromise
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @description Return preparation promise that resolve current preparation list if not empty, or call GET service
         * @returns {Promise} - the process promise
         */
        self.getPreparationsPromise = function() {
            if(self.preparations.length) {
                return $q.when(self.preparations);
            }
            else {
                return self.refreshPreparations();
            }
        };

        /**
         * @ngdoc method
         * @name getDatasetPreparations
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @description Return all the preparation(s) for the given dataset
         * @returns {Object[]} - the preparation list for the given dataset
         */
        self.getDatasetPreparations = function(wanted) {
            return _.filter(self.preparations, function(preparation) {
                return preparation.dataset.id === wanted.id;
            });
        };
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationListService', PreparationListService);
})();