(function () {
    'use strict';

    function DatasetPlaygroundCtrl(DatasetGridService) {
        var vm = this;
        vm.datasetGridService = DatasetGridService;
    }

    Object.defineProperty(DatasetPlaygroundCtrl.prototype,
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

    Object.defineProperty(DatasetPlaygroundCtrl.prototype,
        'metadata', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetGridService.metadata;
            }
        });

    angular.module('data-prep.dataset-playground')
        .controller('DatasetPlaygroundCtrl', DatasetPlaygroundCtrl);
})();

