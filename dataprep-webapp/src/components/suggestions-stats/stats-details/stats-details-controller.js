(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.stats-details.controller:StatsDetailsCtrl
     * @description Statistics details
     * @requires data-prep.services.transformation.service:ColumnSuggestionService
     * @requires data-prep.services.statisticsService.service:StatisticsService
     */
    function StatsDetailsCtrl(ColumnSuggestionService, StatisticsService) {
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
         * @ngdoc method
         * @name onBrushEndFn
         * @methodOf data-prep.stats-details.controller:StatsDetailsCtrl
         * @param {Array} interval of the range (ex : [1, 15])
         * @description adds a range filter to the selected numeric column
         */
        vm.onBrushEndFn = function onBrushEndFn (interval){
            return StatisticsService.addRangeFilter(interval);
        };
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
        'boxplotData', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.statisticsService.boxplotData;
            }
        });

    Object.defineProperty(StatsDetailsCtrl.prototype,
        'rangeLimits', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.statisticsService.rangeLimits;
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