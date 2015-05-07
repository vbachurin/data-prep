(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:DatasetListService
     * @description Dataset grid service. This service holds the dataset list like a cache and consume DatasetRestService to access to the REST api<br/>
     * <b style="color: red;">WARNING : do NOT use this service directly.
     * {@link data-prep.services.dataset.service:DatasetService DatasetService} must be the only entry point for datasets</b>
     * @requires data-prep.services.dataset.service:DatasetRestService
     */
    function DatasetListService($q, DatasetRestService) {
        var self = this;
        var datasetsPromise;

        /**
         * @ngdoc property
         * @name datasets
         * @propertyOf data-prep.services.dataset.service:DatasetListService
         * @description the dataset list
         */
        self.datasets = null;

        /**
         * @ngdoc method
         * @name refreshDatasets
         * @methodOf data-prep.services.dataset.service:DatasetListService
         * @description Refresh datasets if no refresh is pending
         * @returns {promise} - the pending GET promise
         */
        self.refreshDatasets = function() {
            if(! datasetsPromise) {
                datasetsPromise = DatasetRestService.getDatasets()
                    .then(function(res) {
                        self.datasets = res.data;
                        datasetsPromise = null;
                        return self.datasets;
                    });
            }

            return datasetsPromise;
        };

        /**
         * @ngdoc method
         * @name create
         * @methodOf data-prep.services.dataset.service:DatasetListService
         * @param {object} dataset The dataset to delete
         * @description Create a dataset from backend and refresh its internal list
         * @returns {promise} The pending POST promise
         */
        self.create = function(dataset) {
            var promise = DatasetRestService.create(dataset);
            promise.then(self.refreshDatasets);
            return promise;
        };

        /**
         * @ngdoc method
         * @name update
         * @methodOf data-prep.services.dataset.service:DatasetListService
         * @param {object} dataset The dataset to delete
         * @description Update a dataset from backend and refresh its internal list
         * @returns {promise} The pending POST promise
         */
        self.update = function(dataset) {
            var promise = DatasetRestService.update(dataset);
            promise.then(self.refreshDatasets);
            return promise;
        };

        /**
         * @ngdoc method
         * @name processCertification
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @param {object} dataset The target dataset for certification
         * @description Ask certification for a dataset and refresh its internal list
         * @returns {promise} The pending PUT promise
         */
        self.processCertification = function(dataset) {
            return DatasetRestService.processCertification(dataset.id)
                .then(self.refreshDatasets);
        };

        /**
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.services.dataset.service:DatasetListService
         * @param {object} dataset The dataset to delete
         * @description Delete a dataset from backend and from its internal list
         * @returns {promise} The pending DELETE promise
         */
        self.delete = function(dataset) {
            return DatasetRestService.delete(dataset)
                .then(function() {
                    var index = self.datasets.indexOf(dataset);
                    self.datasets.splice(index, 1);
                });
        };

        /**
         * @ngdoc method
         * @name refreshDefaultPreparation
         * @methodOf data-prep.services.dataset.service:DatasetListService
         * @param {object[]} preparations The preparations to use
         * @description [PRIVATE] Set the default preparation to each dataset
         * @returns {promise} The process promise
         */
        self.refreshDefaultPreparation = function(preparations) {
            return getDatasetsPromise()
                .then(function(datasets) {
                    // group preparation per dataset
                    var datasetPreps = _.groupBy(preparations, function(preparation){
                        return preparation.dataSetId;
                    });

                    // reset default preparation for all datasets
                    _.forEach(datasets, function(dataset){
                        var preparations = datasetPreps[dataset.id];
                        dataset.defaultPreparation = preparations && preparations.length === 1 ?  preparations[0] : null;
                    });

                    return datasets;
                });
        };

        /**
         * @ngdoc method
         * @name getDatasetsPromise
         * @methodOf data-prep.services.dataset.service:DatasetListService
         * @description [PRIVATE] Return a promise that resolves the datasets list
         * @returns {promise} The pending GET or resolved promise
         */
        var getDatasetsPromise = function() {
            return self.datasets === null ? self.refreshDatasets() : $q.when(self.datasets);
        };
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetListService', DatasetListService);
})();