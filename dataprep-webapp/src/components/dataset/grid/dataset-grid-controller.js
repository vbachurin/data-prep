(function () {
    'use strict';

    function DatasetGridCtrl(DatasetGridService) {
        var vm = this;
        vm.datasetGridService = DatasetGridService;
    }

    Object.defineProperty(DatasetGridCtrl.prototype,
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

    Object.defineProperty(DatasetGridCtrl.prototype,
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

    Object.defineProperty(DatasetGridCtrl.prototype,
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

    angular.module('data-prep-dataset')
        .controller('DatasetGridCtrl', DatasetGridCtrl);
})();

