/*global _:false */

(function() {
    'use strict';

    function HomeCtrl($window, DatasetService) {
        var vm = this;

        /**
         * Array of all uploading datasets
         * @type {Array}
         */
        vm.uploadingDatasets = [];

        /**
         * Dataset list
         * @type {Array}
         */
        vm.datasets = [];
        DatasetService.getDatasets()
            .then(function(data) {
                vm.datasets = data;
            });

        /**
         * Selected dataset
         * @type {{id: string, name: string}}
         */
        vm.selectedDataset = null;

        /**
         * Selected dataset data
         * @type {{columns: Array, records: Array}}
         */
        vm.selectedData = null;

        /**
         * Refresh dataset list
         */
        vm.refreshDatasets = function() {
            DatasetService.getDatasets().then(function(data) {
                vm.datasets = data;
            });
        };

        /**
         * Check if an existing dataset already has the provided name
         */
        var getDatasetByName = function(name) {
            return _.find(vm.datasets, function(dataset) {
                return dataset.name === name;
            });
        };

        /**
         * Open the upload file input
         */
        vm.openDatasetFileSelection = function() {
            angular.element('#datasetFile').trigger('click');
        };

        /**
         * Get unique name by adding '(num)' at the end
         * @param name - requested name
         * @returns string - the resulting name
         */
        var getUniqueName = function(name) {
            var cleanedName = name.replace(/\([0-1]+\)$/, '').trim();
            var result = cleanedName;

            var index = 1;
            while(getDatasetByName(result)) {
                result = cleanedName + ' (' + index + ')';
                index ++;
            }

            return result;
        };

        /**
         * Update existing dataset
         * @param file - the new file
         * @param existingDataset - the existing dataset
         */
        var updateDataset = function(file, existingDataset) {
            var dataset = DatasetService.fileToDataset(file, existingDataset.name, existingDataset.id);
            vm.uploadingDatasets.push(dataset);

            DatasetService.updateDataset(dataset)
                .progress(function(event) {
                    dataset.progress = parseInt(100.0 * event.loaded / event.total);
                })
                .then(function() {
                    vm.uploadingDatasets.splice(vm.uploadingDatasets.indexOf(dataset, 1));
                    vm.refreshDatasets();
                    $window.alert('Dataset "' + dataset.name + '" updated');
                })
                .catch(function() {
                    dataset.error = true;
                    $window.alert('An error occurred');
                });
        };

        /**
         * Create a new dataset
         */
        var createDataset = function(file, name) {
            var dataset = DatasetService.fileToDataset(file, name);
            vm.uploadingDatasets.push(dataset);

            DatasetService.createDataset(dataset)
                .progress(function(event) {
                    dataset.progress = parseInt(100.0 * event.loaded / event.total);
                })
                .then(function() {
                    vm.uploadingDatasets.splice(vm.uploadingDatasets.indexOf(dataset, 1));
                    vm.refreshDatasets();
                    $window.alert('Dataset "' + dataset.name + '" created');
                })
                .catch(function() {
                    dataset.error = true;
                    $window.alert('An error occurred');
                });
        };

        /**
         * Upload dataset file
         */
        vm.uploadDatasetFile = function() {
            var file = vm.datasetFile[0];

            // remove file extension and ask final name
            var name = file.name.replace(/\.[^/.]+$/, '');
            name = $window.prompt('Enter the dataset name', name) || name;

            // if the name exists, ask for update or creation
            var existingDataset = getDatasetByName(name);
            if(existingDataset && $window.confirm('Do you want to update existing "' + name + '" dataset ?')) {
                updateDataset(file, existingDataset);
            }
            else {
                name = existingDataset ? getUniqueName(name) : name;
                createDataset(file, name);
            }
        };

        /**
         * Delete a dataset
         * @param dataset - the dataset to delete
         */
        vm.deleteDataset = function(dataset) {
            DatasetService.deleteDataset(dataset)
                .success(function() {
                    if(dataset === vm.selectedDataset) {
                        vm.selectedDataset = null;
                        vm.selectedData = null;
                    }
                    vm.refreshDatasets();
                });
        };

        /**
         * Get the selected dataset
         * @param dataset - the selected dataset
         */
        vm.openDataset = function(dataset) {
            DatasetService.getData(dataset)
                .success(function(data) {
                    vm.selectedDataset = dataset;
                    vm.selectedData = data;
                });
        };
    }

    angular.module('data-prep')
        .controller('HomeCtrl', HomeCtrl);
})();