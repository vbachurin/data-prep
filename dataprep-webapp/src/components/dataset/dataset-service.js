(function() {
    'use strict';

    function DatasetService($rootScope, $upload, $http, $q, RestURLs) {
        var self = this;
        var datasets;

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
            return _.find(datasets, function(dataset) {
                return dataset.name === name;
            });
        };

        /**
         * Refresh datasets
         */
        self.refreshDatasets = function() {
            return $http.get(RestURLs.datasetUrl)
                .then(function(res) {
                    datasets = res.data;
                    return datasets;
                });
        };

        /**
         * Get the dataset list
         */
        self.getDatasets = function() {
            if(datasets) {
                return $q.when(datasets);
            }
            else {
                return self.refreshDatasets();
            }
        };

        /**
         * Convert file to dataset object for upload
         * @param file - the file tu upload
         * @param name - the dataset name
         * @param id - the dataset id (used to update existing dataset)
         * @returns {{name: string, progress: number, file: *, error: boolean}}
         */
        self.fileToDataset = function(file, name, id) {
            return {name: name, progress: 0, file: file, error: false, id: id};
        };

        /**
         * Create the dataset
         * @param dataset
         * @returns $upload promiseCUSTOMERS_JSO_LIGHT (1)
         */
        self.createDataset = function(dataset) {
            return $upload.http({
                url: RestURLs.datasetUrl + '?name=' + encodeURIComponent(dataset.name),
                headers: {'Content-Type': 'text/plain'},
                data: dataset.file
            });
        };

        /**
         * Update the dataset
         * @param dataset
         * @returns $upload promise
         */
        self.updateDataset = function(dataset) {
            return $upload.http({
                url: RestURLs.datasetUrl + '/' + dataset.id + '?name=' + encodeURIComponent(dataset.name),
                method: 'PUT',
                headers: {'Content-Type': 'text/plain'},
                data: dataset.file
            });
        };

        /**
         * Delete the dataset
         * @param dataset
         * @returns promise
         */
        self.deleteDataset = function(dataset) {
            return $http.delete(RestURLs.datasetUrl + '/' + dataset.id);
        };

        /**
         * Get the dataset content
         * @param datasetId - dataset id
         */
        self.getDataFromId = function(datasetId, metadata) {
            $rootScope.$emit('talend.loading.start');
            return $http.get(RestURLs.datasetUrl + '/' + datasetId + '?metadata=' + metadata)
                .then(function(res) {
                    return res.data;
                })
                .finally(function() {
                    $rootScope.$emit('talend.loading.stop');
                });
        };
    }

    angular.module('data-prep-dataset')
        .service('DatasetService', DatasetService);
})();