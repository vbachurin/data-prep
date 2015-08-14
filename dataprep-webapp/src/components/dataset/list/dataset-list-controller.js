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
     * @requires talend.widget.service:TalendConfirmService
     * @requires data-prep.services.utils.service:MessageService
     * @requires data-prep.services.uploadWorkflowService:UploadWorkflowService
     * @requires data-prep.services.dataset.service:DatasetListSortService
     */
    function DatasetListCtrl($stateParams, DatasetService, PlaygroundService, TalendConfirmService, MessageService, UploadWorkflowService, DatasetListService, DatasetListSortService) {
        var vm = this;

        vm.datasetListSortService = DatasetListSortService;
        vm.datasetService = DatasetService;
        vm.uploadWorkflowService = UploadWorkflowService;
        vm.datasetListService = DatasetListService;

        /**
         * @ngdoc property
         * @name sortSelected
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Selected sort. If sort is not in cache, Default sort is used
         * @type {object}
         */
        vm.sortSelected = vm.datasetListSortService.getDefaultSort();
        vm.datasetListSortService.setDatasetsSort(vm.sortSelected.id);

        /**
         * @ngdoc property
         * @name sortOrderSelected
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Selected sort order. If order is not in cache, default order is used
         * @type {object}
         */
        vm.sortOrderSelected = vm.datasetListSortService.getDefaultOrder();
        vm.datasetListSortService.setDatasetsOrder(vm.sortOrderSelected.id);

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

            vm.datasetListSortService.setDatasetsSort(vm.sortSelected.id);

            DatasetService.refreshDatasets()
                .then(function() {

                })
                .catch(function() {
                    vm.sortSelected = oldSort;
                    vm.datasetListSortService.setDatasetsSort(vm.sortSelected.id);
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

            var oldSortOrder = vm.sortOrderSelected;
            vm.sortOrderSelected = order;

            vm.datasetListSortService.setDatasetsOrder(vm.sortOrderSelected.id);

            DatasetService.refreshDatasets()
                .then(function() {

                })
                .catch(function() {
                    vm.sortOrderSelected = oldSortOrder;
                    vm.datasetListSortService.setDatasetsOrder(vm.sortOrderSelected.id);
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
     * This list is bound to {@link data-prep.services.dataset.service:DatasetListService DatasetListService}.datasets
     */
    Object.defineProperty(DatasetListCtrl.prototype,
        'datasets', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetListService.datasets;
            }
        });


    /**
     * @ngdoc property
     * @name sortList
     * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
     * @description The dataset list sort.
     * This list is bound to {@link data-prep.services.dataset.service:DatasetListSortService DatasetListSortService}.sortList
     */
    Object.defineProperty(DatasetListCtrl.prototype,
        'sortList', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetListSortService.sortList;
            }
        });


    /**
     * @ngdoc property
     * @name orderList
     * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
     * @description The dataset list sort order.
     * This list is bound to {@link data-prep.services.dataset.service:DatasetListSortService DatasetListSortService}.orderList
     */
    Object.defineProperty(DatasetListCtrl.prototype,
        'orderList', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.datasetListSortService.orderList;
            }
        });

    angular.module('data-prep.dataset-list')
        .controller('DatasetListCtrl', DatasetListCtrl);
})();
