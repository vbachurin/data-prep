(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.dataset-list.controller:DatasetListCtrl
     * @description Dataset list controller.
     On creation, it fetch dataset list from backend and load playground if 'datasetid' query param is provided
     <br/>
     Watchers :
     <ul>
        <li>datasets : on dataset list change, set the default preparation id in each element</li>
     </ul>
     * @requires data-prep.services.dataset.service:DatasetService
     * @requires data-prep.services.dataset.service:DatasetListService
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.utils.service:MessageService
     * @requires talend.widget.service:TalendConfirmService
     * @requires data-prep.services.preparation:PreparationListService
     */
    function DatasetListCtrl($scope, $stateParams, DatasetService, DatasetListService, PlaygroundService, TalendConfirmService, MessageService, PreparationListService) {
        var vm = this;
        vm.datasetListService = DatasetListService;

        /**
         * @ngdoc method
         * @name open
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Initiate a new preparation from dataset
         * @param {Object} dataset - the dataset to open
         */
        vm.open = function(dataset) {
            PlaygroundService.initPlayground(dataset)
                .then(PlaygroundService.show);
        };

        /**
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Delete a dataset
         * @param {Object} dataset - the dataset to delete
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

        vm.update = function(dataset){
            console.log("update");
        }

        /**
         * @ngdoc method
         * @name loadUrlSelectedDataset
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description [PRIVATE] Load playground with provided dataset id, if present in route param
         * @param {Object[]} datasets - list of all user's datasets
         */
        var loadUrlSelectedDataset = function(datasets) {
            if($stateParams.datasetid) {
                var selectedDataset = _.find(datasets, function(dataset) {
                    return dataset.id === $stateParams.datasetid;
                });

                if(selectedDataset) {
                    vm.open(selectedDataset);
                }
                else {
                    MessageService.error('PLAYGROUND_FILE_NOT_FOUND_TITLE', 'PLAYGROUND_FILE_NOT_FOUND', {type: 'dataset'});
                }
            }
        };


        /**
         * @ngdoc method
         * @name setDefaultPreparation
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description [PRIVATE] Set the default preparation id in the dataset.
         * If a dataset has only one preparation, it's its default. Otherwise, there is no default
         * @param {Object} dataset - the dataset to process
         */
        var setDefaultPreparation = function(dataset) {
            var preparations = PreparationListService.getDatasetPreparations(dataset);
            if (preparations.length === 1) {
                dataset.defaultPreparationId = preparations[0].id;
            }
        };

        // load the datasets
        DatasetListService
            .getDatasetsPromise()
            .then(loadUrlSelectedDataset);

        // add a watcher on datasets so that the default preparation is set for each dataset
        $scope.$watch(
            function() {
                return vm.datasets;
            },
            function(newValue) {
                // make sure the preparations are loaded before looking for default dataset
                PreparationListService.getPreparationsPromise()
                    .then(function() {
                        _.forEach(newValue, setDefaultPreparation);
                    });
            });
    }

    /**
     * @ngdoc property
     * @name datasets
     * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
     * @description The dataset list. This list is bound to {@link data-prep.services.dataset.service:DatasetListService DatasetListService} datasets list
     */
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