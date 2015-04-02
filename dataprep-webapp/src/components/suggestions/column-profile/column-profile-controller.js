(function() {
    'use strict';

    function ColumnProfileCtrl($scope, DatasetGridService, StatisticsService) {
        var vm = this;
        vm.datasetGridService = DatasetGridService;

        $scope.$watch(
            function() {
                return DatasetGridService.selectedColumnId;
            },
            function(colId) {
                if(colId) {
                    vm.distribution = StatisticsService.getDistribution(colId);
                    console.log(vm.distribution.length * 20);
                    vm.chartConfig = {
                        options: {
                            chart: {
                                type: 'bar'
                            }
                        },
                        xAxis: {
                            categories: _.map(vm.distribution, function(item) {return item.colVal}),
                            labels: {
                                enabled: true
                            }
                        },
                        series: [{
                            name: 'Number of item',
                            data: _.map(vm.distribution, function(item) {return item.nb})
                        }],
                        title: {
                            text: colId
                        },
                        yAxis: {
                            title: {
                                text: ""
                            }
                        },

                        size: {
                            height: vm.distribution.length * 20 + 150,
                            width: 220
                        },
                        loading: false
                    }

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