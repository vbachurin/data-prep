(function() {
    'use strict';

    function StatisticsService(DatasetGridService) {
        this.getDistribution = function(columnId) {
            var records = DatasetGridService.data.records;

            var result = _.chain(records)
                .groupBy(function(item) {
                    return item[columnId];
                })
                .map(function(val, index) {
                    return {colVal: index, nb: val.length};
                })
                .sortBy('nb')
                .reverse()
                .value();

            return result;
        }
    }

    angular.module('data-prep.services.statistics')
        .service('StatisticsService', StatisticsService);
})();