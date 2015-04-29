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
        self.preparations = null;

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
         * @name setDefaultPreparation
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {object[]} AllPreparations - the preparations to use
         * @param {object[]} datasets - the datasets to update
         * @description [PRIVATE] Set the default preparation for the given dataset if any
         */
        var setDefaultPreparation = function(AllPreparations, datasets) {

            // group preparation per dataset
            var datasetPreps = _.groupBy(AllPreparations, function(preparation){
                return preparation.dataSetId;
            });

            // reset default preparation for all datasets
            _.forEach(datasets, function(dataset){
                var preparations = datasetPreps[dataset.id];
                dataset.defaultPreparation = preparations && preparations.length === 1 ?  preparations[0] : null;
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
                    setDefaultPreparation(preparationResult.data, datasets);
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
            if(self.preparations === null) {
                return self.refreshPreparations();
            }
            else {
                return $q.when(self.preparations);
            }
        };

    }

    angular.module('data-prep.services.preparation')
        .service('PreparationListService', PreparationListService);
})();