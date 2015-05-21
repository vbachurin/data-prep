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
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.utils.service:MessageService
     * @requires talend.widget.service:TalendConfirmService
     */
    function DatasetListCtrl($log,$stateParams,$state,DatasetService, PlaygroundService, TalendConfirmService, MessageService) {
        var vm = this;
        vm.datasetService = DatasetService;

        /**
         * @ngdoc method
         * @name open
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Initiate a new preparation from dataset
         * @param {object} dataset - the dataset to open
         */
        vm.open = function(dataset) {
            PlaygroundService.initPlayground(dataset,false)
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

        vm.openDraft = function(dataset){
            $log.debug("openDraf type: " + dataset.type);
            if (dataset.type){
                if (dataset.type == 'application/vnd.ms-excel'){
                    $state.go( 'nav.home.datasets-previewxls', {datasetid:dataset.id} );
                    return;
                }else{
                    MessageService.error('PREVIEW_NOT_READY_FOR_TYPE_TITLE', 'PREVIEW_NOT_READY_FOR_TYPE_TITLE', {type: 'dataset'});
                }
            } else{
                MessageService.error('FILE_FORMAT_ANALYSIS_NOT_READY_TITLE', 'FILE_FORMAT_ANALYSIS_NOT_READY_TITLE', {type: 'dataset'});
                // TODO force list refresh??
            }
        };

        vm.update = function(dataset){
            console.log('update');
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
                console.log("loadUrlSelectedDataset:"+$stateParams.datasetid);
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
