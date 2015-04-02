(function() {
    'use strict';

    function ColumnProfileCtrl($scope, DatasetGridService, StatisticsService) {
        var vm = this;
        vm.datasetGridService = DatasetGridService;

        $scope.$watch(
            function() {
                return DatasetGridService.selectedColumn;
            },
            function(column) {
                if(column && column.type === 'string') {
                    vm.distribution = StatisticsService.getDistribution(column.id);
                    vm.chartConfig = {
                        credits: {
                            enabled: false
                        },
                        options: {
                            chart: {
                                type: 'bar'
                            },
                            exporting: {
                                enabled: false
                            },
                            plotOptions: {
                                series: {
                                    cursor: 'pointer',
                                    point: {
                                        events: {
                                            click: function () {
                                                console.log('Category: ' + this.category + ', value: ' + this.y);
                                            }
                                        }
                                    }
                                }
                            },
                            tooltip: {
                                hideDelay: 0,
                                positioner: function(boxWidth, boxHeight, point) {
                                    return {
                                        x: 0,
                                        y: point.plotY + 55
                                    };
                                }
                            }
                        },
                        size: {
                            height: vm.distribution.length * 20 + 150,
                            width: 220
                        },
                        xAxis: {
                            categories: _.map(vm.distribution, function(item) {return item.colValue;}),
                            lineWidth: 0,
                            minorGridLineWidth: 0,
                            lineColor: 'transparent',
                            labels: {
                                enabled: true
                            },
                            minorTickLength: 0,
                            tickLength: 0
                        },
                        yAxis: {
                            title: {
                                text: ''
                            },
                            max: vm.distribution[0].frequency
                        },
                        title: {
                            text: column.id
                        },
                        series: [{
                            id: column.id,
                            name: 'number of item',
                            data: _.map(vm.distribution, function(item) {return item.frequency;}),
                            showInLegend: false
                        }],
                        loading: false
                    };
                }
                else {
                    vm.distribution = null;
                }
            }
        );
    }

    Object.defineProperty(ColumnProfileCtrl.prototype,
        'selectedColumnId', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.selectedColumnId;
            }
        });

    angular.module('data-prep.column-profile')
        .controller('ColumnProfileCtrl', ColumnProfileCtrl);
})();