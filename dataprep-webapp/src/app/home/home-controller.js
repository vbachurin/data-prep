(function() {
    'use strict';

    function HomeCtrl(toaster, DatasetService, DatasetListService, TalendConfirmService) {
        var vm = this;

        /**
         * Array of all uploading datasets
         * @type {Array}
         */
        vm.uploadingDatasets = [];

        /**
         * Upload dataset : Step 1 - file selected
         */
        vm.uploadDatasetFile = function() {
            var file = vm.datasetFile[0];

            // remove file extension and ask final name
            var name = file.name.replace(/\.[^/.]+$/, '');
            vm.datasetName = name;

            // show dataset name popup
            vm.datasetNameModal = true;
        };

        /**
         * Upload dataset : Step 2 - name entered
         */
        vm.uploadDatasetName = function() {
            var file = vm.datasetFile[0];
            var name = vm.datasetName;

            // if the name exists, ask for update or creation
            vm.existingDatasetFromName = DatasetListService.getDatasetByName(name);
            if(vm.existingDatasetFromName) {
                TalendConfirmService.confirm(null, 'Do you want to update existing "' + vm.datasetName + '" dataset ?')
                    .then(function() {
                        vm.updateExistingDataset();
                    }, function(cause) {
                        if(cause !== 'dismiss') {
                            vm.createDatasetFromExistingName();
                        }
                    });
            }
            // create with requested name
            else {
                createDataset(file, name);
            }
        };

        /**
         * Upload dataset : Step 3 - Create a new dataset with a unique name (add (n))
         */
        vm.createDatasetFromExistingName = function() {
            var file = vm.datasetFile[0];
            var name = vm.datasetName;
            name = DatasetListService.getUniqueName(name);
            createDataset(file, name);
        };

        /**
         * Upload dataset : Step 3 bis - Update existing dataset
         */
        vm.updateExistingDataset = function() {
            var file = vm.datasetFile[0];
            var existingDataset = vm.existingDatasetFromName;

            updateDataset(file, existingDataset);
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
                    DatasetListService.refreshDatasets();
                    toaster.pop('success', 'New dataset', 'The dataset "' + dataset.name + '" has been created');
                })
                .catch(function() {
                    dataset.error = true;
                    toaster.pop('error', 'Upload error', 'An error occurred during file upload');
                });
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
                    DatasetListService.refreshDatasets();
                    toaster.pop('success', 'Dataset update', 'The dataset "' + dataset.name + '" has been updated');
                })
                .catch(function() {
                    dataset.error = true;
                    toaster.pop('error', 'Upload error', 'An error occurred during file upload');
                });
        };
    }

    angular.module('data-prep')
        .controller('HomeCtrl', HomeCtrl);
})();