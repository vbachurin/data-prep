(function () {
    'use strict';

    function DatagridCtrl(DatasetGridService) {
        var vm = this;
        vm.datasetGridService = DatasetGridService;
    }

    Object.defineProperty(DatagridCtrl.prototype,
        'showDataGrid', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.visible;
            },
            set: function(value) {
                this.datasetGridService.visible = value;
            }
        });

    Object.defineProperty(DatagridCtrl.prototype,
        'metadata', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.metadata;
            },
            set: function(value) {
                this.datasetGridService.metadata = value;
            }
        });

    Object.defineProperty(DatagridCtrl.prototype,
        'data', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.data;
            },
            set: function(value) {
                this.datasetGridService.data = value;
            }
        });

    angular.module('data-prep.datagrid')
        .controller('DatagridCtrl', DatagridCtrl);
})();

