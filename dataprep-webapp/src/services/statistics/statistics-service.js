(function() {
    'use strict';

    function StatisticsService(DatasetGridService) {
        /**
         * Calculate column value distribution
         * @param columnId
         * @returns [{colValue: string, nb: integer}} - colValue : the grouped value, frequency : the nb of time the value appears
         */
        this.getDistribution = function(columnId) {
            var records = DatasetGridService.data.records;

            var result = _.chain(records)
                .groupBy(function(item) {
                    return item[columnId];
                })
                .map(function(val, index) {
                    return {colValue: index, frequency: val.length};
                })
                .sortBy('frequency')
                .reverse()
                .value();

            return result;
        };
    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsService', StatisticsService);
})();