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
         * @param {string} column - the column metadata
         * @description Fetch the transformations suggestions on a column of the dataset
         * @returns {HttpPromise} - the GET promise
         */
        this.getTransformations = function(column) {
            var columnDescription = JSON.stringify(column);
            return $http.post(RestURLs.transformUrl + '/suggest/column', columnDescription);
        };
    }

    angular.module('data-prep.services.transformation')
        .service('TransformationRestService', TransformationRestService);
})();