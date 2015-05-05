(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.home.controller:HomeCtrl
     * @description Home controller.
     * @requires data-prep.services.utils.service:MessageService
     * @requires data-prep.services.dataset.service:DatasetRestService
     * @requires data-prep.services.dataset.service:DatasetListService
     * @requires talend.widget.service:TalendConfirmService
     */
    function HomeCtrl(MessageService, DatasetRestService, DatasetListService, TalendConfirmService) {
        var vm = this;

        /**
         * @ngdoc property
         * @name showRightPanel
         * @propertyOf data-prep.home.controller:HomeCtrl
         * @description Flag that control the right panel display
         * @type {boolean}
         */
        vm.showRightPanel = true;

        /**
         * @ngdoc property
         * @name uploadingDatasets
         * @propertyOf data-prep.home.controller:HomeCtrl
         * @description The current uploading datasets
         * @type {object[]}
         */
        vm.uploadingDatasets = [];

        /**
         * @ngdoc method
         * @name toggleRightPanel
         * @methodOf data-prep.home.controller:HomeCtrl
         * @description Toggle the right panel containing inventory data
         */
        vm.toggleRightPanel = function() {
            vm.showRightPanel = !vm.showRightPanel;
        };

        /**
         * @ngdoc method
         * @name uploadDatasetFile
         * @methodOf data-prep.home.controller:HomeCtrl
         * @description Upload dataset : Step 1 - file selected. It takes the file name, and display the dataset name
         * change modal
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
         * @ngdoc method
         * @name uploadDatasetName
         * @methodOf data-prep.home.controller:HomeCtrl
         * @description Upload dataset : Step 2 - name entered. It ask for override if a dataset with the same name
         * exists, and trigger the upload
         */
        vm.uploadDatasetName = function() {
            var file = vm.datasetFile[0];
            var name = vm.datasetName;

            // if the name exists, ask for update or creation
            vm.existingDatasetFromName = DatasetListService.getDatasetByName(name);
            if(vm.existingDatasetFromName) {
                TalendConfirmService.confirm(null, ['UPDATE_EXISTING_DATASET'], {dataset: vm.datasetName})
                    .then(
                        function() {
                            vm.updateExistingDataset();
                        },
                        function(cause) {
                            if(cause !== 'dismiss') {
                                vm.createDatasetFromExistingName();
                            }
                        }
                    );
            }
            // create with requested name
            else {
                createDataset(file, name);
            }
        };

        /**
         * @ngdoc method
         * @name createDatasetFromExistingName
         * @methodOf data-prep.home.controller:HomeCtrl
         * @description Upload dataset : Step 3 - Create a new dataset with a unique name (add (n)).
         */
        vm.createDatasetFromExistingName = function() {
            var file = vm.datasetFile[0];
            var name = vm.datasetName;
            name = DatasetListService.getUniqueName(name);
            createDataset(file, name);
        };

        /**
         * @ngdoc method
         * @name updateExistingDataset
         * @methodOf data-prep.home.controller:HomeCtrl
         * @description Upload dataset : Step 3 bis - Update existing dataset
         */
        vm.updateExistingDataset = function() {
            var file = vm.datasetFile[0];
            var existingDataset = vm.existingDatasetFromName;

            updateDataset(file, existingDataset);
        };

        /**
         * @ngdoc method
         * @name createDataset
         * @methodOf data-prep.home.controller:HomeCtrl
         * @param {object} file - the file to upload
         * @param {string} name - the dataset name
         * @description [PRIVATE] Create a new dataset
         */
        var createDataset = function(file, name) {
            var dataset = DatasetRestService.fileToDataset(file, name);
            vm.uploadingDatasets.push(dataset);

            DatasetRestService.createDataset(dataset)
                .progress(function(event) {
                    dataset.progress = parseInt(100.0 * event.loaded / event.total);
                })
                .then(function() {
                    vm.uploadingDatasets.splice(vm.uploadingDatasets.indexOf(dataset, 1));
                    DatasetListService.refreshDatasets();
                    MessageService.success('DATASET_CREATE_SUCCESS_TITLE', 'DATASET_CREATE_SUCCESS', {dataset: dataset.name});
                })
                .catch(function() {
                    dataset.error = true;
                    MessageService.error('UPLOAD_ERROR_TITLE', 'UPLOAD_ERROR');
                });
        };

        /**
         * @ngdoc method
         * @name updateDataset
         * @methodOf data-prep.home.controller:HomeCtrl
         * @param {object} file - the file to upload
         * @param {object} existingDataset - the existing dataset
         * @description [PRIVATE] Update existing dataset
         */
        var updateDataset = function(file, existingDataset) {
            var dataset = DatasetRestService.fileToDataset(file, existingDataset.name, existingDataset.id);
            vm.uploadingDatasets.push(dataset);

            DatasetRestService.updateDataset(dataset)
                .progress(function(event) {
                    dataset.progress = parseInt(100.0 * event.loaded / event.total);
                })
                .then(function() {
                    vm.uploadingDatasets.splice(vm.uploadingDatasets.indexOf(dataset, 1));
                    DatasetListService.refreshDatasets();
                    MessageService.success('DATASET_UPDATE_SUCCESS_TITLE', 'DATASET_UPDATE_SUCCESS', {dataset: dataset.name});
                })
                .catch(function() {
                    dataset.error = true;
                    MessageService.error('UPLOAD_ERROR_TITLE', 'UPLOAD_ERROR');
                });
        };

    }

    angular.module('data-prep.home')
        .controller('HomeCtrl', HomeCtrl);
})();