(function() {
    'use strict';

    function DatasetListService(DatasetService) {
        var self = this;

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
         * Refresh datasets
         */
        self.refreshDatasets = function() {
            return DatasetService.getDatasets()
                .then(function(res) {
                    self.datasets = res.data;
                });
        };
    }

    angular.module('data-prep-dataset')
        .service('DatasetListService', DatasetListService);
})();