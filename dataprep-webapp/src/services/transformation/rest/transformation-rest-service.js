(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:TransformationRestService
     * @description Transformation service. This service provide the entry point to get transformation menu from REST api
     */
    function TransformationRestService($http, RestURLs) {

        /**
         * @ngdoc method
         * @name getTransformations
         * @methodOf data-prep.services.transformation.service:TransformationRestService
         * @param {string} datasetId - the dataset id
         * @param {string} column - the column id
         * @description Fetch the transformations suggestions on a column of the dataset
         * @returns {HttpPromise} - the GET promise
         */
        this.getTransformations = function(datasetId, column) {
            var cleanDatasetId = encodeURIComponent(datasetId);
            var cleanColumn = encodeURIComponent(column);
            return $http.get(RestURLs.datasetUrl + '/' + cleanDatasetId + '/' + cleanColumn + '/actions');
        };
    }

    angular.module('data-prep.services.transformation')
        .service('TransformationRestService', TransformationRestService);
})();