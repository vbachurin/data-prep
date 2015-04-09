(function() {
    'use strict';

    function DatasetService($rootScope, $upload, $http, RestURLs) {
        var self = this;

        /**
         * Get the dataset list
         * @returns Promise
         */
        self.getDatasets = function() {
            return $http.get(RestURLs.datasetUrl);
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
         * @param metadata - if false, the metadata will not be returned
         */
        self.getDataFromId = function(datasetId, metadata) {
            $rootScope.$emit('talend.loading.start');
            return $http.get(RestURLs.datasetUrl + '/' + datasetId + '/content?metadata=' + metadata)
                .then(function(res) {
                    return res.data;
                })
                .finally(function() {
                    $rootScope.$emit('talend.loading.stop');
                });
        };
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetService', DatasetService);
})();