(function() {
    'use strict';

    function ColumnProfileCtrl($scope, $timeout, DatagridService, StatisticsService, FilterService) {
        var vm = this;
        vm.datasetGridService = DatagridService;
        vm.chartConfig = {};

        /**
         * Add a 'contains' filter in the angular context
         * @param columnId - the column id
         * @param value - the phrase
         */
        var addFilter = function(columnId, value) {
            $timeout(FilterService.addFilter.bind(null, 'contains', columnId, {phrase: value}));
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

        /**
         * Pie specific highcharts options
         * @param clickFn - the click callback
         * @returns {{credits, exporting, legend, plotOptions}|{exporting: {enabled: boolean}, legend: {enabled: boolean}}}
         */
        var initPieChartOptions = function(clickFn) {
            var options = initCommonChartOptions(clickFn);
            options.chart = {
                type: 'pie'
            };
            options.plotOptions.pie = {
                cursor: 'pointer',
                dataLabels: {
                    enabled: true
                }
            };
            return options;
        };

        /**
         * Bar specific highcharts options
         * @param clickFn - the click callback
         * @returns {{credits, exporting, legend}|{exporting: {enabled: boolean}, legend: {enabled: boolean}}}
         */
        var initBarChartOptions = function(clickFn) {
            var options = initCommonChartOptions(clickFn);
            options.chart = {
                type: 'bar'
            };
            options.tooltip = {
                hideDelay: 0,
                    positioner: function(boxWidth, boxHeight, point) {
                    return {
                        x: 0,
                        y: point.plotY + 55
                    };
                }
            };
            return options;
        };

        /**
         * Bar chart X axis configuration
         * @param categories
         * @returns {{categories: *, lineWidth: number, minorGridLineWidth: number, lineColor: string, labels: {enabled: boolean}, minorTickLength: number, tickLength: number}}
         */
        var initBarXAxis = function(categories) {
            return {
                categories: categories,
                lineWidth: 0,
                minorGridLineWidth: 0,
                lineColor: 'transparent',
                labels: {enabled: true},
                minorTickLength: 0,
                tickLength: 0
            };
        };

        /**
         * Bar chart Y axis configuration
         * @param min - the min value to display
         * @param max - tha max value to display
         * @returns {{title: {text: string}, min: *, max: *}}
         */
        var initBarYAxis = function(min, max) {
            return {
                title: {text: ''},
                min: min,
                max: max
            };
        };

        /**
         * Bar chart size depending on the number of element
         * @param nbElements - the number of element
         * @returns {{height: number, width: number}}
         */
        var initBarChartSize = function(nbElements) {
            return {
                height: nbElements * 20 + 150,
                width: 220
            };
        };

        //------------------------------------------------------------------------------------------------------
        //-----------------------------------------------CHARTS TYPES-------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * Test if column has a geo type
         * @param column
         * @returns {boolean}
         */
        var isGeo = function(column) {
            return column.id.toLowerCase() === 'state';
        };

        /**
         * Test if column has a range type
         * @param column
         * @returns {boolean}
         */
        var isRange = function(column) {
            return column.type === 'numeric' || column.type === 'integer' || column.type === 'float' || column.type === 'double' ;
        };

        /**
         * Test if column is a classic (bar) distribution chart
         * @param column
         * @returns {boolean}
         */
        var isBar = function(column) {
            return column.type === 'string';
        };

        /**
         * Test if column has a pie type
         * @param column
         * @returns {boolean}
         */
        var isPie = function(column) {
            return column.type === 'boolean';
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
                addFilter(column.id, this['hc-key'].substring(3));
                console.log('State: '  + this['hc-key'] + ', value: ' + this.value);
            };

            vm.stateDistribution = StatisticsService.getGeoDistribution(column);

            var data = vm.stateDistribution.data;
            var min = data[data.length - 1].value;
            var max = data[0].value;

            vm.chartConfig = {
                options: initGeoChartOptions(geoChartAction, min, max),
                chartType: 'map',
                title: {text: column.id + ' distribution'},
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

        /**
         * Init a range distribution chart
         * @param column
         */
        var buildRangeDistribution = function(column) {
            var barChartAction = function () {
                var category = this.category;
                var metadata = _.find(this.series.userOptions.metadata, function(metadata) {
                    return metadata.key === category;
                });
                console.log(metadata);
            };

            vm.rangeDistribution = StatisticsService.getRangeDistribution(column);
            var categories = _.map(vm.rangeDistribution, 'key');
            var max = _.max(vm.rangeDistribution, 'frequency').frequency;

            vm.chartConfig = {
                options: initBarChartOptions(barChartAction),
                size: initBarChartSize(vm.rangeDistribution.length),
                xAxis: initBarXAxis(categories),
                yAxis: initBarYAxis(0, max),
                title: {text: column.id},
                series: [{
                    id: column.id,
                    name: 'number of item',
                    data: _.map(vm.rangeDistribution, 'frequency'),
                    showInLegend: false,
                    metadata: vm.rangeDistribution
                }],
                loading: false
            };
        };

        /**
         * Init a bar distribution chart
         * @param column
         */
        var buildBarDistribution = function(column) {
            var barChartAction = function () {
                addFilter(column.id, this.category);
                console.log('Category: ' + this.category + ', value: ' + this.y);
            };

            vm.distribution = StatisticsService.getDistribution(column.id);
            var categories = _.map(vm.distribution, 'colValue');

            vm.chartConfig = {
                options: initBarChartOptions(barChartAction),
                size: initBarChartSize(vm.distribution.length),
                xAxis: initBarXAxis(categories),
                yAxis: initBarYAxis(0, vm.distribution[0].frequency),
                title: {text: column.id},
                series: [{
                    id: column.id,
                    name: 'number of item',
                    data: _.map(vm.distribution, 'frequency'),
                    showInLegend: false
                }],
                loading: false
            };
        };

        /**
         * Init a pie distribution chart
         * @param column
         */
        var buildPieDistribution = function(column) {
            var pieChartAction = function () {
                addFilter(column.id, this.name);
                console.log('Category: ' + this.name + ', value: ' + this.y);
            };

            vm.pieDistribution = StatisticsService.getDistribution(column.id);

            vm.chartConfig = {
                options: initPieChartOptions(pieChartAction),
                size: {
                    height: 300
                },
                title: {text: column.id},
                series: [{
                    id: column.id,
                    name: 'number of item',
                    data: _.map(vm.pieDistribution, function(item) {
                        return [item.colValue, item.frequency];
                    })
                }],
                loading: false
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
                return DatagridService.selectedColumn;
            },
            function(column) {
                vm.distribution = null;
                vm.rangeDistribution = null;
                vm.stateDistribution = null;
                vm.pieDistribution = null;

                if(! column) {
                    return;
                }

                if(isGeo(column)) {
                    buildGeoDistribution(column);
                }
                else if(isRange(column)) {
                    buildRangeDistribution(column);
                }
                else if(isBar(column)) {
                    buildBarDistribution(column);
                }
                else if(isPie(column)) {
                    buildPieDistribution(column);
                }
            }
        );
    }

    Object.defineProperty(ColumnProfileCtrl.prototype,
        'selectedColumn', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.selectedColumn;
            }
        });

    angular.module('data-prep.column-profile')
        .controller('ColumnProfileCtrl', ColumnProfileCtrl);
})();