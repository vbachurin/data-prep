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
     * @requires data-prep.services.dataset.service:DatasetListSortService
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires talend.widget.service:TalendConfirmService
     * @requires data-prep.services.utils.service:MessageService
     * @requires data-prep.services.uploadWorkflowService.service:UploadWorkflowService
     * @requires data-prep.services.state.service:StateService
     * @requires data-prep.services.onboarding:OnboardingService
     */
    function DatasetListCtrl($rootScope, $stateParams, DatasetService, DatasetListSortService, PlaygroundService,
                             TalendConfirmService, MessageService, UploadWorkflowService, StateService) {
        var vm = this;

        vm.datasetService = DatasetService;
        vm.uploadWorkflowService = UploadWorkflowService;

        /**
         * @ngdoc property
         * @name sortList
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description The sort list
         * @type {array}
         */
        vm.sortList = DatasetListSortService.getSortList();

        /**
         * @ngdoc property
         * @name orderList
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description The sort order list
         * @type {string}
         */
        vm.orderList = DatasetListSortService.getOrderList();

        /**
         * @ngdoc property
         * @name sortSelected
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Selected sort.
         * @type {object}
         */
        vm.sortSelected = DatasetListSortService.getSortItem();

        /**
         * @ngdoc property
         * @name sortOrderSelected
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Selected sort order.
         * @type {object}
         */
        vm.sortOrderSelected = DatasetListSortService.getOrderItem();

        /**
         * @ngdoc method
         * @name sort
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description sort dataset by sortType by calling refreshDatasets from DatasetService
         * @param {object} sortType Criteria to sort
         */
        vm.updateSortBy = function(sortType) {
            if(vm.sortSelected === sortType) {
                return;
            }

            var oldSort = vm.sortSelected;
            vm.sortSelected = sortType;
            DatasetListSortService.setSort(sortType.id);

            DatasetService.refreshDatasets()
                .catch(function() {
                    vm.sortSelected = oldSort;
                    DatasetListSortService.setSort(oldSort.id);
                });
        };

        /**
         * @ngdoc method
         * @name sort
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description sort dataset in order (ASC or DESC) by calling refreshDatasets from DatasetService
         * @param {object} order Sort order ASC(ascending) or DESC(descending)
         */
        vm.updateSortOrder = function(order) {
            if(vm.sortOrderSelected === order) {
                return;
            }

            var oldSort = vm.sortOrderSelected;
            vm.sortOrderSelected = order;
            DatasetListSortService.setOrder(order.id);

            DatasetService.refreshDatasets()
                .catch(function() {
                    vm.sortOrderSelected = oldSort;
                    DatasetListSortService.setOrder(oldSort.id);
                });
        };

        /**
         * @ngdoc method
         * @name open
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description [PRIVATE] Initiate a new preparation from dataset
         * @param {object} dataset The dataset to open
         */
        var open = function(dataset) {
            PlaygroundService.initPlayground(dataset)
                .then(StateService.showPlayground);
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
         * @name clone
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Clone a dataset
         * @param {object} dataset - the dataset to clone
         */
        vm.clone = function(dataset){
            // TODO ask a new name?
            DatasetService.clone(dataset)
                .then(function() {
                          MessageService.success('CLONE_SUCCESS_TITLE', 'CLONE_SUCCESS');
                });
        };

        /**
         * @ngdoc method
         * @name showRenameInput
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description save a copy of the original dataset name to restore it if the new name has not been saved.
         * @param {object} dataset - the dataset to clone
         */
        vm.showRenameInput = function(dataset){
            dataset.originalName = dataset.name;
            dataset.showChangeName=true;

            _.each( vm.datasets,function(current){
                if (current.id!=dataset.id){
                    vm.cancelRename(current);
                }
            });
        };

        /**
         * @ngdoc method
         * @name cancelRename
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description restore original dataset name
         * @param {object} dataset - the dataset to clone
         */
        vm.cancelRename = function(dataset){
            if (dataset.originalName){
                dataset.name = dataset.originalName;
            }
            dataset.showChangeName=false;
        };

        /**
         * @ngdoc method
         * @name doRename
         * @methodOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Trigger the dataset rename in the backend
         */
        vm.rename = function(dataset){
            $rootScope.$emit('talend.loading.start');
            return DatasetService.update(dataset)
                .then(function(){
                  dataset.showChangeName = false;
                })
                .then(function() {
                          MessageService.success('RENAME_SUCCESS_TITLE', 'RENAME_SUCCESS');
                })
                //hide loading screen
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        };

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
