(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:TransformationService
     * @description Transformation service. This service provide the entry point to get transformation menu from REST api
     */
    function TransformationService($http, RestURLs) {

        /**
         * @ngdoc method
         * @name getTransformations
         * @methodOf data-prep.services.transformation.service:TransformationService
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

        this.getPreviewAppend = function(records, actions, canceler) {
            var actionParam = {records: records, actions: actions};
            var request = {
                method: 'POST',
                url: RestURLs.previewUrl + '/append',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: actionParam,
                timeout: canceler.promise
            };

            return $http(request);
        };
    }

    angular.module('data-prep.services.transformation')
        .service('TransformationService', TransformationService);
})();