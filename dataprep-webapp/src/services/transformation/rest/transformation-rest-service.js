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
         * @param {string} column The column metadata
         * @description Fetch the transformations suggestions on a column of the dataset
         * @returns {HttpPromise} The POST promise
         */
        this.getTransformations = function(column) {
            var columnDescription = JSON.stringify(column);
            return $http.post(RestURLs.transformUrl + '/suggest/column', columnDescription);
        };

        /**
         * @ngdoc method
         * @name getDynamicParameters
         * @methodOf data-prep.services.transformation.service:TransformationRestService
         * @param {string} action The action name
         * @param {string} columnId The column Id
         * @param {string} datasetId The datasetId
         * @param {string} preparationId The preparation Id
         * @description Fetch the transformations dynamic params
         * @returns {HttpPromise} The GET promise
         */
        this.getDynamicParameters = function(action, columnId, datasetId, preparationId) {
            var queryParams = preparationId ? '?preparationId=' + encodeURIComponent(preparationId) : '?datasetId=' + encodeURIComponent(datasetId);
            queryParams+= '&columnId=' + encodeURIComponent(columnId);

            return $http.get(RestURLs.transformUrl + '/suggest/' + action + '/params' + queryParams);
        };
    }

    angular.module('data-prep.services.transformation')
        .service('TransformationRestService', TransformationRestService);
})();