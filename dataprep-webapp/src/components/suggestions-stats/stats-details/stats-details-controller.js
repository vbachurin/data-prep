(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.stats-details.controller:StatsDetailsCtrl
     * @description Statistics details
     * @requires data-prep.services.transformation.service:ColumnSuggestionService
     * @requires data-prep.services.statisticsService.service:StatisticsService
     */
    function StatsDetailsCtrl($scope, ColumnSuggestionService, StatisticsService) {
        var vm = this;
        vm.columnSuggestionService = ColumnSuggestionService;
        vm.statisticsService = StatisticsService;

        /**
         * @ngdoc method
         * @name addPatternFilter
         * @methodOf data-prep.stats-details.controller:StatsDetailsCtrl
         * @param {object} item Pattern object (ex : {'pattern':'aaa','occurrences':8})
         * @description Add a pattern filter from selected pattern item
         */
        vm.addPatternFilter = function addPatternFilter(item) {
            alert('The selected pattern is: ' + item.pattern + '. Filtering is coming soon.');
            //return StatisticsService.addFilter(item.data);
        };

        /**
         * update the boxplot chart data
         * */
        $scope.$watch(function(){
            return vm.statisticsService.boxplotData;
        }, function(newData){
            vm.boxplotData = newData;
        });
    }

    Object.defineProperty(StatsDetailsCtrl.prototype,
        'statistics', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.statisticsService.statistics;
            }
        });


    Object.defineProperty(StatsDetailsCtrl.prototype,
        'patternFrequencyTable', {
            enumerable: true,
            configurable: false,
            get: function () {
                var column = this.columnSuggestionService.currentColumn;
                return column ? column.statistics.patternFrequencyTable : null;
            }
        });

    angular.module('data-prep.stats-details')
        .controller('StatsDetailsCtrl', StatsDetailsCtrl);
})();