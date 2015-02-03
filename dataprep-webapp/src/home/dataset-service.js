/*global jQuery:false */

(function() {
    'use strict';

    function DatasetService($upload, $http) {
        /**
         * Get the dataset list
         */
        this.getDatasets = function() {
            return $http.get('http://10.42.10.99:8081/datasets')
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
                url: 'http://10.42.10.99:8081/datasets?' + jQuery.param({name: dataset.name}),
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
                url: 'http://10.42.10.99:8081/datasets/' + dataset.id + '?' + jQuery.param({name: dataset.name}),
                method: 'PUT',
                headers: {'Content-Type': 'text/plain'},
                data: dataset.file
            });
        };
    }

    angular.module('data-prep')
        .service('DatasetService', DatasetService);
})();