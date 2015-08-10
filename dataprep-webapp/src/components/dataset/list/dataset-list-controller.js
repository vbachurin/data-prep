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
     */
    function DatasetListCtrl($window, $stateParams, DatasetService, PlaygroundService, TalendConfirmService, MessageService, UploadWorkflowService) {
        var vm = this;

        var sortSelectedKey = 'dataprep.dataset.sort';
        var sortOrderSelectedKey = 'dataprep.dataset.sortOrder';

        vm.datasetService = DatasetService;
        vm.uploadWorkflowService = UploadWorkflowService;

        /**
         * @ngdoc property
         * @name sortList
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description List of supported sort.
         * @type {object[]}
         */
        vm.sortList = [
            {id: 'name', name: 'NAME_SORT'},
            {id: 'date', name: 'DATE_SORT'}
        ];

        /**
         * @ngdoc property
         * @name orderList
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description List of sorting order.
         * @type {object[]}
         */
        vm.orderList = [
            {id: 'asc', name: 'ASC_ORDER'},
            {id: 'desc', name: 'DESC_ORDER'}
        ];

        /**
         * @ngdoc property
         * @name sortSelected
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Selected sort. If sort is not in cache, Default sort is used
         * @type {object}
         */
        var savedSort = $window.localStorage.getItem(sortSelectedKey);
        vm.sortSelected = !savedSort ? vm.sortList[1] : _.find(vm.sortList, {id: savedSort});

        /**
         * @ngdoc property
         * @name sortOrderSelected
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description Selected sort order. If order is not in cache, default order is used
         * @type {object}
         */
        var savedSortOrder = $window.localStorage.getItem(sortOrderSelectedKey);
        vm.sortOrderSelected = !savedSortOrder ? vm.orderList[1] : _.find(vm.orderList, {id: savedSortOrder});

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

            DatasetService.refreshDatasets(vm.sortSelected.id, vm.sortOrderSelected.id)
                .then(function() {
                    $window.localStorage.setItem(sortSelectedKey, vm.sortSelected.id);
                })
                .catch(function() {
                    vm.sortSelected = oldSort;
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

            DatasetService.refreshDatasets(vm.sortSelected.id,  vm.sortOrderSelected.id)
                .then(function() {
                    $window.localStorage.setItem(sortOrderSelectedKey, vm.sortOrderSelected.id);
                })
                .catch(function() {
                    vm.sortOrderSelected = oldSortOrder;
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
