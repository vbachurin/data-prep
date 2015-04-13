(function() {
    'use strict';

    function DatasetListCtrl($stateParams, DatasetService, DatasetListService, PlaygroundService, TalendConfirmService, MessageService) {
        var vm = this;
        vm.datasetListService = DatasetListService;

        /**
         * Initiate a new preparation from dataset
         * @param dataset - the dataset to open
         */
        vm.open = function(dataset) {
            PlaygroundService.initPlayground(dataset)
                .then(PlaygroundService.show);
        };

        /**
         * Delete a dataset
         * @param dataset - the dataset to delete
         */
        vm.delete = function(dataset) {
            TalendConfirmService.confirm({disableEnter: true}, ['DELETE_PERMANENTLY', 'NO_UNDONE_CONFIRM'], {type: 'dataset', name: dataset.name})
                .then(function() {
                    return DatasetService.deleteDataset(dataset);
                })
                .then(function() {
                    MessageService.success('REMOVE_SUCCESS_TITLE', 'REMOVE_SUCCESS', {type: 'dataset', name: dataset.name});
                    DatasetListService.refreshDatasets();
                });
        };

        /**
         * Load playground with provided dataset id, if present in route param
         * @param datasets - list of all user's datasets
         */
        var loadUrlSelectedPreparation = function(datasets) {
            if($stateParams.datasetid) {
                var selectedDataset = _.find(datasets, function(dataset) {
                    return dataset.id === $stateParams.datasetid;
                });
                if(selectedDataset) {
                    vm.open(selectedDataset);
                }
            }
        };

        DatasetListService.getDatasetsPromise()
            .then(loadUrlSelectedPreparation);
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