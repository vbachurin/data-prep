(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:DatasetService
     * @description Dataset general service. This service manage the operations that touches the datasets
     * @requires data-prep.services.dataset.service:DatasetListService
     */
    function DatasetService(DatasetListService) {
        var self = this;

        /**
         * @ngdoc method
         * @name datasetsList
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @description Return the datasets list. See {@link data-prep.services.dataset.service:DatasetListService DatasetListService}.datasets
         * @returns {object[]} The datasets list
         */
        self.datasetsList = function() {
            return DatasetListService.datasets;
        };

        /**
         * @ngdoc method
         * @name getDatasets
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @description Return a promise that resolves the datasets list. See {@link data-prep.services.dataset.service:DatasetListService DatasetListService}.getDatasetsPromise
         * @returns {promise} - the pending GET or resolved promise
         */
        self.getDatasets = DatasetListService.getDatasetsPromise;

        /**
         * @ngdoc method
         * @name delete
         * @name data-prep.services.dataset.service:DatasetService
         * @param {object} dataset The dataset to delete
         * @description Delete a dataset. It just call {@link data-prep.services.dataset.service:DatasetListService DatasetListService} delete function
         * @returns {promise} The pending DELETE promise
         */
        self.delete = DatasetListService.delete;

        /**
         * @ngdoc method
         * @name create
         * @name data-prep.services.dataset.service:DatasetService
         * @param {object} dataset The dataset to create
         * @description Create a dataset. It just call {@link data-prep.services.dataset.service:DatasetListService DatasetListService} create function
         * @returns {promise} The pending DELETE promise
         */
        self.create = DatasetListService.create;

        /**
         * @ngdoc method
         * @name update
         * @name data-prep.services.dataset.service:DatasetService
         * @param {object} dataset The dataset to update
         * @description Update a dataset. It just call {@link data-prep.services.dataset.service:DatasetListService DatasetListService} update function
         * @returns {promise} The pending PUT promise
         */
        self.update = DatasetListService.update;

        /**
         * @ngdoc method
         * @name getContent
         * @name data-prep.services.dataset.service:DatasetService
         * @param {string} datasetId The dataset id
         * @param {boolean} metadata If false, the metadata will not be returned
         * @description Get a dataset content. It just call {@link data-prep.services.dataset.service:DatasetListService DatasetListService} getContent function
         * @returns {promise} The pending GET promise
         */
        self.getContent = DatasetListService.getContent;

        /**
         * @ngdoc method
         * @name getDatasetByName
         * @methodOf data-prep.services.dataset.service:DatasetService
         * @param {string} name The dataset name
         * @description Get the dataset that has the wanted name
         * @returns {object} The dataset
         */
        self.getDatasetByName = function(name) {
            return _.find(self.datasetsList(), function(dataset) {
                return dataset.name === name;
            });
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
         * @name getUniqueName
         * @methodOf data-prep.services.dataset.service:DatasetService
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
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetService', DatasetService);
})();