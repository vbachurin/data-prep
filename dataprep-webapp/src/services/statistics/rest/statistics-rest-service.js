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
         * @param {string} stringifiedColumn The column metadata
         * @description Fetch the aggregation on a column of the dataset
         * @returns {HttpPromise} The POST promise
         */
        this.getAggregations = function(stringifiedColumn) {

            var responsePromise =  $http.post(RestURLs.datasetUrl + '/aggregation/column', stringifiedColumn)
                .then(function(response) { //Success
                    return response.data;
            }, function() { // Failure
                    return [];
                    /*
                    return [
                        { 'data': 'Lansing', 'occurrences': 15 },
                        { 'data': 'Helena', 'occurrences': 5 },
                        { 'data': 'Baton Rouge', 'occurrences': 64 },
                        { 'data': 'Annapolis', 'occurrences': 4 },
                        { 'data': 'Pierre', 'occurrences': 104 }
                    ];
                    */
            });

            return responsePromise;
        };

    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsRestService', StatisticsRestService);
})();