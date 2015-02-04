(function() {
    'use strict';

    function HomeCtrl($window, DatasetService, datasets) {
        var vm = this;

        /**
         * Array of all uploading datasets
         * @type {Array}
         */
        vm.uploadingDatasets = [];

        /**
         * Dataset list
         */
        vm.datasets = datasets || [];

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
            document.getElementById('datasetFile').click();
        };

        var getUniqueName = function(name) {
            var cleanedName = name.replace(/\([0-1]+\)$/, "").trim();
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
                .then(function(data) {
                    vm.uploadingDatasets.splice(vm.uploadingDatasets.indexOf(dataset, 1));
                    vm.refreshDatasets();
                    $window.alert('Dataset "' + dataset.name + '" updated');
                })
                .catch(function(err) {
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
                .then(function(data) {
                    vm.uploadingDatasets.splice(vm.uploadingDatasets.indexOf(dataset, 1));
                    vm.refreshDatasets();
                    $window.alert('Dataset "' + dataset.name + '" created');
                })
                .catch(function(err) {
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
            var name = file.name.replace(/\.[^/.]+$/, "");
            name = $window.prompt("Enter the dataset name", name) || name;

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

    }

    angular.module('data-prep')
        .controller('HomeCtrl', HomeCtrl);
})();