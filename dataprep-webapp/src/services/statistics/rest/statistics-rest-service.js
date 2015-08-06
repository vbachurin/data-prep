(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.statistics.service:StatisticsAggregationRestService
     * @description Aggregation REST service. This service gets the aggregation
     */
    function StatisticsAggregationRestService($http, RestURLs) {

        /**
         * @ngdoc method
         * @name getAggregations
         * @methodOf data-prep.services.transformation.service:TransformationRestService
         * @param {string} stringifiedColumn The column metadata
         * @description Fetch the aggregation on a column of the dataset
         * @returns {HttpPromise} The POST promise
         */
        this.getAggregations = function(stringifiedColumn) {
            return $http.post(RestURLs.datasetUrl + '/aggregation/column', stringifiedColumn);
        };

    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsAggregationRestService', StatisticsAggregationRestService);
})();