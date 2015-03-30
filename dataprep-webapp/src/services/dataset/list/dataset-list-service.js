(function() {
    'use strict';

    function DatasetListService($q, DatasetService) {
        var self = this;
        var datasetsPromise;

        self.datasets = [];

        /**
         * Get unique name by adding '(num)' at the end
         * @param name - requested name
         * @returns string - the resulting name
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
         * Check if an existing dataset already has the provided name
         */
        self.getDatasetByName = function(name) {
            return _.find(self.datasets, function(dataset) {
                return dataset.name === name;
            });
        };

        /**
         * Refresh datasets if no refresh is pending
         */
        self.refreshDatasets = function() {
            if(! datasetsPromise) {
                datasetsPromise = DatasetService.getDatasets()
                    .then(function(res) {
                        self.datasets = res.data;
                        datasetsPromise = null;
                    });
            }

            return datasetsPromise;
        };

        /**
         * Return a promise that resolve the datasets list
         */
        self.getDatasetsPromise = function() {
            if(self.datasets.length) {
                return $q.when(self.datasets);
            }
            else {
                return self.refreshDatasets();
            }
        };
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetListService', DatasetListService);
})();