(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.statistics.service:StatisticsRestService
     * @description Statistics REST service.
     */
    function StatisticsRestService($http, RestURLs) {
        /**
         * @ngdoc method
         * @name getAggregations
         * @methodOf data-prep.services.statistics.service:StatisticsRestService
         * @param {object} parameters The aggregation parameters
         * @description Fetch the aggregation on a column of the dataset
         * @returns {HttpPromise} The POST promise
         */
        this.getAggregations = function (parameters) {
            return $http.post(RestURLs.aggregationUrl, parameters)
                .then(function (response) {
                    return response.data;
                });
        };
    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsRestService', StatisticsRestService);
})();