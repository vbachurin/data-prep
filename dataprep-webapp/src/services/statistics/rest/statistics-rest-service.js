/**
 * @ngdoc service
 * @name data-prep.services.statistics.service:StatisticsRestService
 * @description Statistics REST service.
 */
export default function StatisticsRestService($q, $http, $cacheFactory, RestURLs) {
    'ngInject';

    var aggregationCache = $cacheFactory('aggregationStatistics', {capacity: 5});

    /**
     * @ngdoc method
     * @name resetCache
     * @methodOf data-prep.services.statistics.service:StatisticsRestService
     * @description Reset the cache
     */
    this.resetCache = function resetCache() {
        aggregationCache.removeAll();
    };

    /**
     * @ngdoc method
     * @name getAggregations
     * @methodOf data-prep.services.statistics.service:StatisticsRestService
     * @param {object} parameters The aggregation parameters
     * @description Fetch the aggregation on a column of the dataset
     * @returns {Promise} The POST promise
     */
    this.getAggregations = function (parameters) {
        var cacheKey = JSON.stringify(parameters);
        var resultFromCache = aggregationCache.get(cacheKey);

        return resultFromCache ?
            $q.when(resultFromCache) :
            $http.post(RestURLs.aggregationUrl, parameters)
                .then(function (response) {
                    aggregationCache.put(cacheKey, response.data);
                    return response.data;
                });
    };
}