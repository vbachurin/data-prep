(function () {
    'use strict';

    function DatagridCtrl(DatasetGridService) {
        var vm = this;
        vm.datasetGridService = DatasetGridService;
    }

    Object.defineProperty(DatagridCtrl.prototype,
        'metadata', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.metadata;
            }
        });

    Object.defineProperty(DatagridCtrl.prototype,
        'data', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.data;
            }
        });

    Object.defineProperty(DatagridCtrl.prototype,
        'dataView', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.dataView;
            }
        });

    Object.defineProperty(DatagridCtrl.prototype,
        'filters', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.filters;
            }
        });
    angular.module('data-prep.datagrid')
        .controller('DatagridCtrl', DatagridCtrl);
})();

