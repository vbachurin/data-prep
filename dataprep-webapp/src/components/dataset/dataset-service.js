(function() {
    'use strict';

    function DatasetService($upload, $http, RestURLs) {
        /**
         * Get the dataset list
         */
        this.getDatasets = function() {
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
        this.fileToDataset = function(file, name, id) {
            return {name: name, progress: 0, file: file, error: false, id: id};
        };

        /**
         * Create the dataset
         * @param dataset
         * @returns $upload promise
         */
        this.createDataset = function(dataset) {
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
        this.updateDataset = function(dataset) {
            return $upload.http({
                url: RestURLs.datasetUrl + '/' + dataset.id + '?' + jQuery.param({name: dataset.name}),
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
        this.deleteDataset = function(dataset) {
            return $http.delete(RestURLs.datasetUrl + '/' + dataset.id);
        };

        /**
         * Get the dataset content
         * @param dataset
         */
        this.getData = function(dataset) {
            return $http.get(RestURLs.datasetUrl + '/' + dataset.id + '?' + jQuery.param({metadata: false}))
                .then(function(res) {
                    return res.data;
                });
        };
    }

    angular.module('data-prep-dataset')
        .service('DatasetService', DatasetService);
})();