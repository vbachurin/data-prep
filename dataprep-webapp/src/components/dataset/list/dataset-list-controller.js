(function() {
    'use strict';

    function DatasetListCtrl($q, toaster, DatasetService, DatasetListService, DatasetGridService, TalendConfirmService) {
        var vm = this;
        vm.datasetListService = DatasetListService;

        /**
         * Last selected dataset metadata
         * @type {dataset}
         */
        vm.lastSelectedMetadata = null;

        /**
         * Last selected records and columns
         * @type {data}
         */
        vm.lastSelectedData = null;

        /**
         * Get the dataset data and display data modal
         * @param dataset - the dataset to open
         */
        vm.open = function(dataset) {
            var getDataPromise;
            if(vm.lastSelectedMetadata && dataset.id === vm.lastSelectedMetadata.id) {
                getDataPromise = $q.when(true);
            }
            else {
                getDataPromise = DatasetService.getDataFromId(dataset.id, false)
                    .then(function(data) {
                        vm.lastSelectedMetadata = dataset;
                        vm.lastSelectedData = data;
                    });
            }
            getDataPromise.then(function() {
                DatasetGridService.setDataset(vm.lastSelectedMetadata, vm.lastSelectedData);
                DatasetGridService.show();
            });
        };

        /**
         * Delete a dataset
         * @param dataset - the dataset to delete
         */
        vm.delete = function(dataset) {
            var explainationsText = 'You are going to permanently delete the dataset "' + dataset.name + '".';
            var confirmText = 'This operation cannot be undone. Are you sure ?';
            TalendConfirmService.confirm(explainationsText, confirmText).then(function() {
                DatasetService.deleteDataset(dataset)
                    .then(function() {
                        if(dataset === vm.lastSelectedMetadata) {
                            vm.lastSelectedMetadata = null;
                            vm.lastSelectedData = null;
                        }

                        toaster.pop('success', 'Remove dataset', 'The dataset "' + dataset.name + '" has been removed.');
                        DatasetListService.refreshDatasets();
                    });
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

    angular.module('data-prep-dataset')
        .controller('DatasetListCtrl', DatasetListCtrl);
})();