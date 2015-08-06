(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.statistics.service:StatisticsCacheService
     * @description StatisticsCacheService cache service. This service provide the entry point to get aggregation datas.
     * It holds a cache that should be invalidated at each new playground load
     * @requires data-prep.services.statistics.service:StatisticsCacheService
     */
    function StatisticsCacheService($q, StatisticsService) {
        var suggestionsCache = [];

        /**
         * @ngdoc method
         * @name getKey
         * @methodOf data-prep.services.statistics.service:StatisticsCacheService
         * @param {object} column The column to set as key
         * @description [PRIVATE] Generate a unique key for the column.
         */
        var getKey = function getKey(currentColumn, targetColumn, calculation) {
            var keyObj = {
                currentColumn: currentColumn.id,
                targetColumn: targetColumn.id,
                calculation: calculation.id
            };
            return JSON.stringify(keyObj);
        };

        /**
         * @ngdoc method
         * @name getAggregations
         * @methodOf data-prep.services.statistics.service:StatisticsCacheService
         * @param {object} currentColumn The selected column
         * @param {object} targetColumn The aggregation target column
         * @param {object} calculation The selected calculation
         * @description Get aggregations from cache if present, from REST call otherwise.
         * It cleans and adapts them.
         */
        this.getAggregations = function getAggregations(currentColumn, targetColumn, calculation) {
            var key = getKey(currentColumn, targetColumn, calculation);

            //if cache contains the key, the value is either the values or the fetch promise
            var aggregation = suggestionsCache[key];
            if(aggregation) {
                return $q.when(aggregation);
            }

            //fetch menus from REST and adapt them. The Promise is put in cache, it is then replaced by the value.
            var fetchPromise = StatisticsService.getAggregations(key)
                .then(function(aggregation) {
                    suggestionsCache[key] = aggregation;
                    return aggregation;
                });

            suggestionsCache[key] = fetchPromise;
            return fetchPromise;
        };

        /**
         * @ngdoc method
         * @name invalidateCache
         * @methodOf data-prep.services.transformation.service:StatisticsCacheService
         * @description Invalidate all cache entries
         */
        this.invalidateCache = function invalidateCache() {
            suggestionsCache = [];
        };
    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsCacheService', StatisticsCacheService);
})();