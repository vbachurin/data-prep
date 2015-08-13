(function() {
    'use strict';

    function ColumnProfileCtrl($scope, DatagridService, StatisticsService, SuggestionsStatsAggregationsService, PlaygroundService) {
        var vm = this;
        vm.datasetGridService = DatagridService;
        vm.statisticsService = StatisticsService;

        vm.datasetAggregationsService = SuggestionsStatsAggregationsService;
        vm.chartConfig = {};

        vm.barchartClickFn = function barchartClickFn (item){
            return StatisticsService.addFilter(item.data);
        };
        
        //------------------------------------------------------------------------------------------------------
        //----------------------------------------------AGGREGATION---------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * List of all possible calculations
         */
        vm.calculationsList =  [
            {id: 'sum', name: 'SUM'},
            {id: 'max', name: 'MAX'},
            {id: 'min', name: 'MIN'},
            {id: 'count', name: 'COUNT'},
            {id: 'average', name: 'AVERAGE'},
            {id: 'median', name: 'MEDIAN'}
        ];


        /**
         * Update Chart for aggregation
         * @param column - the aggregation target column selected
         * @param calculation - the aggregation operation selected
         */
        vm.updateCharts = function (column, calculation) {
            vm.datasetAggregationsService.updateAggregationsChanges(column, calculation);

            var aggregationCalculation = calculation;

            if (!aggregationCalculation) {
                //Calculation by default
                aggregationCalculation = {id: 'count', name: 'COUNT'};
            }

            if(column){ // Aggregation

                StatisticsService.processVisuDataAggregation(
                    PlaygroundService.currentMetadata.id,
                    vm.datasetAggregationsService.columnSelected,
                    column,
                    aggregationCalculation
                );

            } else { //Count Line

                StatisticsService.processNonMapData(vm.datasetAggregationsService.columnSelected);
            }
        };


        //------------------------------------------------------------------------------------------------------
        //----------------------------------------------CHARTS OPTIONS------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Common highcharts options
         * @param clickFn - the click callback
         * @returns {{exporting: {enabled: boolean}, legend: {enabled: boolean}}}
         */
        var initCommonChartOptions = function(clickFn) {
            return {
                credits: {
                    enabled: false
                },
                exporting: {
                    enabled: false
                },
                legend: {
                    enabled: false
                },
                plotOptions: {
                    series: {
                        cursor: 'pointer',
                        point: {
                            events: {
                                click: clickFn
                            }
                        }
                    }
                }
            };
        };

        /**
         * Geo specific highcharts options
         * @param clickFn - the click callback
         * @param min - min value (defined for color)
         * @param max - max value (defined for color)
         * @returns {{exporting, legend}|{exporting: {enabled: boolean}, legend: {enabled: boolean}}}
         */
        var initGeoChartOptions = function(clickFn, min, max) {
            var options = initCommonChartOptions(clickFn);

            options.tooltip = {
                enabled: true,
                headerFormat: '',
                pointFormat: '{point.name}: <b>{point.value}</b>'
            };
            options.colorAxis = {
                min: min,
                max: max
            };
            options.mapNavigation = {
                enabled: true,
                buttonOptions: {
                    verticalAlign: 'bottom'
                }
            };

            return options;
        };

        //------------------------------------------------------------------------------------------------------
        //-----------------------------------------------CHARTS BUILD-------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Init a geo distribution chart
         * @param column
         */
        var buildGeoDistribution = function(column) {
            var geoChartAction = function() {
                StatisticsService.addFilter(this['hc-key'].substring(3));
                console.log('State: '  + this['hc-key'] + ', value: ' + this.value);
            };

            vm.stateDistribution = StatisticsService.getGeoDistribution(column);

            var data = vm.stateDistribution.data;
            var min = data[data.length - 1].value;
            var max = data[0].value;

            vm.chartConfig = {
                options: initGeoChartOptions(geoChartAction, min, max),
                chartType: 'map',
                title: {text: column.name + ' distribution'},
                series: [
                    {
                        id: column.id,
                        data: data,
                        mapData: Highcharts.maps[vm.stateDistribution.map],
                        joinBy: 'hc-key',
                        states: {
                            hover: {
                                color: '#BADA55'
                            }
                        }
                    }
                ]
            };
        };

        //------------------------------------------------------------------------------------------------------
        //-------------------------------------------------WATCHERS---------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Init chart on column selection change
         */
        $scope.$watch(
            function() {
                return StatisticsService.stateDistribution;
            },
            function(column) {
                vm.stateDistribution = null;
                if(column) {
                    buildGeoDistribution(column);
                }
            }
        );


        /**
         * @ngdoc property
         * @name selectedColumn
         * @propertyOf data-prep.actions-suggestions-stats.controller:ColumnProfileCtrl
         * @description The selected aggregation.
         * This is bound to {@link data-prep.statistics:StatisticsService StatisticsService}.selectedColumn
         */
        Object.defineProperty(ColumnProfileCtrl.prototype,
            'selectedColumn', {
                enumerable: true,
                configurable: true,
                get: function () {
                    return this.statisticsService.selectedColumn;
                }
            });

        /**
         * @ngdoc property
         * @name aggregationSelected
         * @propertyOf data-prep.actions-suggestions-stats.controller:ColumnProfileCtrl
         * @description The selected aggregation.
         * This is bound to {@link data-prep.suggestions-stats:SuggestionsStatsAggregationsService SuggestionsStatsAggregationsService}.aggregationSelected
         */
        Object.defineProperty(ColumnProfileCtrl.prototype,
            'aggregationSelected', {
                enumerable: true,
                configurable: true,
                get: function () {
                    return this.datasetAggregationsService.aggregationSelected;
                }
            });

        /**
         * @ngdoc property
         * @name columnAggregationSelected
         * @propertyOf data-prep.actions-suggestions-stats.controller:ColumnProfileCtrl
         * @description The selected aggregation target column.
         * This is bound to {@link data-prep.suggestions-stats:SuggestionsStatsAggregationsService SuggestionsStatsAggregationsService}.columnAggregationSelected
         */
        Object.defineProperty(ColumnProfileCtrl.prototype,
            'columnAggregationSelected', {
                enumerable: true,
                configurable: true,
                get: function () {
                    return this.datasetAggregationsService.columnAggregationSelected;
                }
            });

        /**
         * @ngdoc property
         * @name calculationAggregationSelected
         * @propertyOf data-prep.actions-suggestions-stats.controller:ColumnProfileCtrl
         * @description The selected aggregation calculation.
         * This is bound to {@link data-prep.suggestions-stats:SuggestionsStatsAggregationsService SuggestionsStatsAggregationsService}.calculationAggregationSelected
         */
        Object.defineProperty(ColumnProfileCtrl.prototype,
            'calculationAggregationSelected', {
                enumerable: true,
                configurable: true,
                get: function () {
                    return this.datasetAggregationsService.calculationAggregationSelected;
                }
            });

        /**
         * @ngdoc property
         * @name numericColumns
         * @propertyOf data-prep.actions-suggestions-stats.controller:ColumnProfileCtrl
         * @description The numeric columns list of the dataset.
         * This is bound to {@link data-prep.suggestions-stats:SuggestionsStatsAggregationsService SuggestionsStatsAggregationsService}.numericColumns
         */
        Object.defineProperty(ColumnProfileCtrl.prototype,
            'numericColumns', {
                enumerable: true,
                configurable: true,
                get: function () {
                    return this.datasetAggregationsService.numericColumns;
                }
            });

        /**
         * @ngdoc property
         * @name barChartValueKey
         * @propertyOf data-prep.actions-suggestions-stats.controller:ColumnProfileCtrl
         * @description barChartValueKey is used to display value in the tooltip of horizontal-barchart
         * This is bound to {@link data-prep.suggestions-stats:SuggestionsStatsAggregationsService SuggestionsStatsAggregationsService}.barChartValueKey
         */
        Object.defineProperty(ColumnProfileCtrl.prototype,
            'barChartValueKey', {
                enumerable: true,
                configurable: true,
                get: function () {
                    return this.datasetAggregationsService.barChartValueKey;
                }
            });


        /**
         * @ngdoc property
         * @name barChartValueKeyLabel
         * @propertyOf data-prep.actions-suggestions-stats.controller:ColumnProfileCtrl
         * @description barChartValueKeyLabel is used to display value label in the tooltip of horizontal-barchart
         * This is bound to {@link data-prep.suggestions-stats:SuggestionsStatsAggregationsService SuggestionsStatsAggregationsService}.barChartValueKeyLabel
         */
        Object.defineProperty(ColumnProfileCtrl.prototype,
            'barChartValueKeyLabel', {
                enumerable: true,
                configurable: true,
                get: function () {
                    return this.datasetAggregationsService.barChartValueKeyLabel;
                }
            });
    }

    Object.defineProperty(ColumnProfileCtrl.prototype,
        'processedData', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.statisticsService.data;
            }
        });

    angular.module('data-prep.column-profile')
        .controller('ColumnProfileCtrl', ColumnProfileCtrl);
})();