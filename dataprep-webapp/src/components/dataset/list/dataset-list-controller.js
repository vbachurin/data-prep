(function() {
    'use strict';

    function DatasetListCtrl($rootScope, $q, DatasetService) {
        var vm = this;

        /**
         * Dataset list
         * @type {Array}
         */

        vm.datasets = [];

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
         * Refresh dataset list on refresh event receive
         */
        $rootScope.$on('talend.datasets.refresh', function() {
            vm.refreshDatasets();
        });

        /**
         * Refresh dataset list
         */
        vm.refreshDatasets = function() {
            DatasetService.refreshDatasets()
                .then(function(data) {
                    vm.datasets = data;
                });
        };

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
                $rootScope.$emit('talend.dataset.open', {metadata: vm.lastSelectedMetadata, data: vm.lastSelectedData});
            });
        };

        /**
         * Delete a dataset
         * @param dataset - the dataset to delete
         */
        vm.delete = function(dataset) {
            DatasetService.deleteDataset(dataset)
                .then(function() {
                    vm.refreshDatasets();
                });
        };

        vm.refreshDatasets();
    }

    angular.module('data-prep-dataset')
        .controller('DatasetListCtrl', DatasetListCtrl);
})();