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
     * @requires data-prep.services.dataset.service:DatasetSheetPreviewService
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires talend.widget.service:TalendConfirmService
     * @requires data-prep.services.utils.service:MessageService
     */
    function DatasetListCtrl($rootScope, $state, $stateParams, DatasetService, DatasetSheetPreviewService, PlaygroundService, TalendConfirmService, MessageService) {
        var vm = this;
        vm.datasetService = DatasetService;

        /**
         * @ngdoc method
         * @name open
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description [PRIVATE] Initiate a new preparation from dataset
         * @param {object} dataset The dataset to open
         */
        var open = function(dataset) {
            PlaygroundService.initPlayground(dataset)
                .then(PlaygroundService.show);
        };

        /**
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Delete a dataset
         * @param {object} dataset - the dataset to delete
         */
        vm.delete = function(dataset) {
            TalendConfirmService.confirm({disableEnter: true}, ['DELETE_PERMANENTLY', 'NO_UNDONE_CONFIRM'], {type: 'dataset', name: dataset.name})
                .then(function() {
                    return DatasetService.delete(dataset);
                })
                .then(function() {
                    MessageService.success('REMOVE_SUCCESS_TITLE', 'REMOVE_SUCCESS', {type: 'dataset', name: dataset.name});
                });
        };

        /**
         * @ngdoc method
         * @name toggleFavorite
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description toogle dataset as Favorite or not
         * @param {object} dataset - the dataset to be set or unset favorite
         */
        vm.toggleFavorite = function(dataset) {
           DatasetService.toggleFavorite(dataset);//just a delegate
        };




        /**
         * @ngdoc method
         * @name openDraft
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Draft management
         * <ul>
         *      <li>File type is not defined : display error, refresh dataset list</li>
         *      <li>File type is excel : redirect to schema selection</li>
         *      <li>File type defined but unknown : display error</li>
         * </ul>
         * @param {object} dataset The dataset draft to open
         */
        vm.openDraft = function (dataset) {
            if (dataset.type === 'application/vnd.ms-excel') {
                DatasetSheetPreviewService.loadPreview(dataset)
                    .then(DatasetSheetPreviewService.display);
            }
            else if (dataset.type) {
                MessageService.error('PREVIEW_NOT_IMPLEMENTED_FOR_TYPE_TITLE', 'PREVIEW_NOT_IMPLEMENTED_FOR_TYPE_TITLE');
            }
            else {
                DatasetService.refreshDatasets();
                MessageService.error('FILE_FORMAT_ANALYSIS_NOT_READY_TITLE', 'FILE_FORMAT_ANALYSIS_NOT_READY_CONTENT');
            }
        };

        /**
         * @ngdoc method
         * @name openDataset
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Try to open a dataset. If it is a draft, we open the draft import wizard instead.
         * @param {object} dataset The dataset to open
         */
        vm.openDataset = function(dataset) {
            if(dataset.draft) {
                vm.openDraft(dataset);
            }
            else {
                $state.go('nav.home.datasets', {datasetid: dataset.id});
            }
        };

        /**
         * @ngdoc method
         * @name processCertification
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Ask certification for a dataset
         * @param {object} dataset - the dataset to ask certifiction for
         */
        vm.processCertification = DatasetService.processCertification;

        /**
         * @ngdoc method
         * @name loadUrlSelectedDataset
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description [PRIVATE] Load playground with provided dataset id, if present in route param
         * @param {object[]} datasets - list of all user's datasets
         */
        var loadUrlSelectedDataset = function(datasets) {
            if($stateParams.datasetid) {
                var selectedDataset = _.find(datasets, function(dataset) {
                    return dataset.id === $stateParams.datasetid;
                });

                if(selectedDataset) {
                    open(selectedDataset);
                }
                else {
                    MessageService.error('PLAYGROUND_FILE_NOT_FOUND_TITLE', 'PLAYGROUND_FILE_NOT_FOUND', {type: 'dataset'});
                }
            }
        };


        /*
         * listen to open dataset event.
         */
        $rootScope.$on('talend.dataset.open', function (event, datasetId) {
            DatasetService.getDatasetById(datasetId).then(vm.openDataset);
      });


        // load the datasets
        DatasetService
            .getDatasets()
            .then(loadUrlSelectedDataset);
    }

    /**
     * @ngdoc property
     * @name datasets
     * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
     * @description The dataset list.
     * This list is bound to {@link data-prep.services.dataset.service:DatasetService DatasetService}.datasetsList()
     */
    Object.defineProperty(DatasetListCtrl.prototype,
        'datasets', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetService.datasetsList();
            }
        });

    angular.module('data-prep.dataset-list')
        .controller('DatasetListCtrl', DatasetListCtrl);
})();
