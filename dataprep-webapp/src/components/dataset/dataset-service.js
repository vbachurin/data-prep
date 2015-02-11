(function() {
    'use strict';

    function DatasetService($rootScope, $upload, $http, RestURLs) {
        var self = this;
        
        /**
         * Get the dataset list
         */
        self.getDatasets = function() {
            return $http.get(RestURLs.datasetUrl)
                .then(function(res) {
                    return res.data;
                });
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
         * @returns $upload promise
         */
        self.createDataset = function(dataset) {
            return $upload.http({
                url: RestURLs.datasetUrl + '?' + jQuery.param({name: dataset.name}),
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
                url: RestURLs.datasetUrl + '/' + dataset.id + '?' + jQuery.param({name: dataset.name}),
                method: 'POST',
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
            return $http.get(RestURLs.datasetUrl + '/' + datasetId + '?' + jQuery.param({metadata: metadata}))
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