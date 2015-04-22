(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:DatasetService
     * @description Dataset service. This service provide the entry point to the backend dataset REST api.
     */
    function DatasetService($rootScope, $upload, $http, RestURLs) {
        var self = this;

        /**
         * @ngdoc method
         * @name getDatasets
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @description Get the dataset list
         * @returns {Promise} - the GET call promise
         */
        self.getDatasets = function() {
            return $http.get(RestURLs.datasetUrl);
        };

        /**
         * @ngdoc method
         * @name fileToDataset
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @description Convert file to dataset object for upload
         * @param {file} file - the file tu upload
         * @param {string} name - the dataset name
         * @param {string} id - the dataset id (used to update existing dataset)
         * @returns {Object} - the adapted dataset infos {name: string, progress: number, file: *, error: boolean}
         */
        self.fileToDataset = function(file, name, id) {
            return {name: name, progress: 0, file: file, error: false, id: id};
        };

        /**
         * @ngdoc method
         * @name createDataset
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @description Create the dataset
         * @param {dataset} dataset - the dataset infos to create
         * @returns {Promise} - the $upload promise
         */
        self.createDataset = function(dataset) {
            return $upload.http({
                url: RestURLs.datasetUrl + '?name=' + encodeURIComponent(dataset.name),
                headers: {'Content-Type': 'text/plain'},
                data: dataset.file
            });
        };

        /**
         * @ngdoc method
         * @name updateDataset
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @description Update the dataset
         * @param {dataset} dataset - the dataset infos to update
         * @returns {Promise} - the $upload promise
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
         * @ngdoc method
         * @name deleteDataset
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @description Delete the dataset
         * @param {dataset} dataset - the dataset infos to update
         * @returns {Promise} - the DELETE promise
         */
        self.deleteDataset = function(dataset) {
            return $http.delete(RestURLs.datasetUrl + '/' + dataset.id);
        };

        /**
         * Ask certification for a dataset
         * @param dataset
         * @returns promise
         */
        self.processCertification = function(dataset) {
            return $http.put(RestURLs.datasetUrl + '/' + dataset.id + '/processcertification');
        };

        /**
         * Get the dataset content
         * @param datasetId - dataset id
         * @param metadata - if false, the metadata will not be returned
         */
        /**
         * @ngdoc method
         * @name getDataFromId
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @description Get the dataset content
         * @param {string} datasetId - the dataset id
         * @param {boolean} metadata - if false, the metadata will not be returned
         * @returns {Promise} - the GET promise
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

    angular.module('data-prep.services.dataset')
        .service('DatasetService', DatasetService);
})();