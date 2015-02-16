(function() {
    'use strict';

    function HomeCtrl($rootScope, $window, DatasetService) {
        var vm = this;

        /**
         * Array of all uploading datasets
         * @type {Array}
         */
        vm.uploadingDatasets = [];

        /**
         * Open the upload file input
         */
        vm.openDatasetFileSelection = function() {
            angular.element('#datasetFile').trigger('click');
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
                    $rootScope.$emit('talend.datasets.refresh');
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
                    $rootScope.$emit('talend.datasets.refresh');
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
            var existingDataset = DatasetService.getDatasetByName(name);
            if(existingDataset && $window.confirm('Do you want to update existing "' + name + '" dataset ?')) {
                updateDataset(file, existingDataset);
            }
            else {
                name = existingDataset ? DatasetService.getUniqueName(name) : name;
                createDataset(file, name);
            }
        };
    }

    angular.module('data-prep')
        .controller('HomeCtrl', HomeCtrl);
})();