(function() {
    'use strict';

    function DatasetListCtrl(DatasetService, DatasetListService, PlaygroundService, TalendConfirmService, MessageService) {
        var vm = this;
        vm.datasetListService = DatasetListService;

        /**
         * Initiate a new preparation from dataset
         * @param dataset - the dataset to open
         */
        vm.open = function(dataset) {
            PlaygroundService.initPlayground(dataset);
            PlaygroundService.show();
        };

        /**
         * Delete a dataset
         * @param dataset - the dataset to delete
         */
        vm.delete = function(dataset) {
            TalendConfirmService.confirm({disableEnter: true}, ['DELETE_PERMANENTLY', 'NO_UNDONE_CONFIRM'], {dataset: dataset.name})
                .then(function() {
                    return DatasetService.deleteDataset(dataset);
                })
                .then(function() {
                    MessageService.success('DATASET_REMOVE_SUCCESS_TITLE', 'DATASET_REMOVE_SUCCESS', {dataset: dataset.name});
                    DatasetListService.refreshDatasets();
                });
        };

        DatasetListService.refreshDatasets();
    }

    Object.defineProperty(DatasetListCtrl.prototype,
        'datasets', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetListService.datasets;
            },
            set: function(value) {
                this.datasetListService.datasets = value;
            }
        });

    angular.module('data-prep.dataset-list')
        .controller('DatasetListCtrl', DatasetListCtrl);
})();