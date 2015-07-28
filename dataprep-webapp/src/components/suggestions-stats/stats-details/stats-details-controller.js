(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.stats-details.controller:StatsDetailsCtrl
     * @description statistics details
     * @requires data-prep.services.transformation.service:ColumnSuggestionService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function StatsDetailsCtrl($scope, ColumnSuggestionService, ConverterService) {
        var vm = this;
        vm.columnSuggestionService = ColumnSuggestionService;

        /**
         * @ngdoc method
         * @name isInt
         * @methodOf data-prep.stats-details.controller:StatsDetailsCtrl
         * @param {number} stat value to clean the float
         * @description cleans 5.2568845842587425588 into 5.25
         * @returns {number} - in the clean format
         */
        vm.isInt = function isInt (nbr){
            return nbr % 1 === 0 ? nbr : nbr.toFixed(2);
        };

        /**
         * @ngdoc method
         * @name patternBarchartClickFn
         * @methodOf data-prep.stats-details.controller:StatsDetailsCtrl
         * @param {object} ex {'pattern':'aaa','occurrences':8}
         * @description coming soon, filter the selected pattern
         */
        vm.patternBarchartClickFn = function patternBarchartClickFn (item){
            alert('The selected pattern is: ' + item.pattern + '. Filtering is coming soon.');
            //return StatisticsService.addFilter(item.data);
        };

        /**
         * Updates the stats values tab
         **/
        $scope.$watch(function(){
            return vm.columnSuggestionService.currentColumn;
        }, function(newSelectedCol){
            if(newSelectedCol){
                vm.updatedColumn = newSelectedCol;
                var colType = ConverterService.simplifyType(vm.updatedColumn.type);
                vm.statsByColType = [];
                var stats = vm.updatedColumn.statistics;
                switch(colType){
                    case 'number':
                        //As it is correct only for positive values
                        if(stats.quantiles.lowerQuantile === 'NaN'){
                            vm.statsByColType = [
                                {'Count':stats.count},
                                {'Distinct Count':stats.distinctCount},
                                {'Duplicate Count':stats.duplicateCount},
                                {'Empty':stats.empty},
                                {'Invalid':stats.invalid},
                                {'Max':stats.max},
                                {'Mean':stats.mean},
                                {'Min':stats.min},
                                {'Valid':stats.valid},
                                {'Variance':stats.variance}
                            ];
                        } else {
                            vm.statsByColType = [
                                {'Count':stats.count},
                                {'Distinct Count':stats.distinctCount},
                                {'Duplicate Count':stats.duplicateCount},
                                {'Empty':stats.empty},
                                {'Invalid':stats.invalid},
                                {'Max':stats.max},
                                {'Mean':stats.mean},
                                {'Min':stats.min},
                                {'Lower Quantile':stats.quantiles.lowerQuantile},
                                {'Median':stats.quantiles.median},
                                {'Upper Quantile':stats.quantiles.upperQuantile},
                                {'Valid':stats.valid},
                                {'Variance':stats.variance}
                            ];
                        }

                        break;
                    case 'boolean':
                        vm.statsByColType = [
                            {'Count':stats.count},
                            {'Distinct Count':stats.distinctCount},
                            {'Duplicate Count':stats.duplicateCount},
                            {'Empty':stats.empty},
                            {'Invalid':stats.invalid},
                            {'Valid':stats.valid}
                        ];
                        break;
                    case 'text':
                        vm.statsByColType = [
                            {'Count':stats.count},
                            {'Distinct Count':stats.distinctCount},
                            {'Duplicate Count':stats.duplicateCount},
                            {'Empty':stats.empty},
                            {'Invalid':stats.invalid},
                            {'Average Length':stats.textLengthSummary.averageLength},
                            {'Average Length With Blank':stats.textLengthSummary.averageLengthWithBlank},
                            {'Maximal Length':stats.textLengthSummary.maximalLength},
                            {'Minimal Length':stats.textLengthSummary.minimalLength},
                            {'Minimal Length With Blank':stats.textLengthSummary.minimalLengthWithBlank},
                            {'Valid':stats.valid}
                        ];
                        break;
                    case 'date':
                        vm.statsByColType = [
                            {'Count':stats.count},
                            {'Distinct Count':stats.distinctCount},
                            {'Duplicate Count':stats.duplicateCount},
                            {'Empty':stats.empty},
                            {'Invalid':stats.invalid},
                            {'Valid':stats.valid}
                        ];
                        break;
                }
            }
        });
    }

    angular.module('data-prep.stats-details')
        .controller('StatsDetailsCtrl', StatsDetailsCtrl);
})();