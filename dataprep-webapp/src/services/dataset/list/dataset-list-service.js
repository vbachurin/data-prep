(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:DatasetListService
     * @description Dataset grid service. This service holds the dataset list like a cache and consume DatasetRestService to access to the REST api
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
         * @name getUniqueName
         * @methodOf data-prep.services.dataset.service:DatasetListService
         * @param {string} name - the base name
         * @description Get a unique name from a base name. The existence check is done on the local dataset list. It transform the base name, adding "(number)"
         * @returns {string} - the unique name
         */
        self.getUniqueName = function(name) {
            var cleanedName = name.replace(/\([0-9]+\)$/, '').trim();
            var result = cleanedName;

            var index = 1;
            while(self.getDatasetByName(result)) {
                result = cleanedName + ' (' + index + ')';
                index ++;
            }

            return result;
        };

        /**
         * @ngdoc method
         * @name getDatasetByName
         * @methodOf data-prep.services.dataset.service:DatasetListService
         * @param {string} name - the dataset name
         * @description Get the dataset that has the wanted name
         * @returns {object} - the dataset
         */
        self.getDatasetByName = function(name) {
            return _.find(self.datasets, function(dataset) {
                return dataset.name === name;
            });
        };

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
         * @name getDatasetsPromise
         * @methodOf data-prep.services.dataset.service:DatasetListService
         * @description Return a promise that resolves the datasets list
         * @returns {promise} - the pending GET or resolved promise
         */
        self.getDatasetsPromise = function() {
            if(self.datasets === null) {
                return self.refreshDatasets();
            }
            else {
                return $q.when(self.datasets);
            }
        };
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetListService', DatasetListService);
})();