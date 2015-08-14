(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:DatasetListSortService
     * @description Dataset list sort service
     */
    function DatasetListSortService($window) {

        var self = this;

        var sortSelectedKey = 'dataprep.dataset.sort';
        var sortOrderSelectedKey = 'dataprep.dataset.sortOrder';

        /**
         * @ngdoc property
         * @name sortList
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description List of supported sort.
         * @type {object[]}
         */
        self.sortList = [
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
        self.orderList = [
            {id: 'asc', name: 'ASC_ORDER'},
            {id: 'desc', name: 'DESC_ORDER'}
        ];


        /**
         * @ngdoc property
         * @name datasetsSort
         * @propertyOf data-prep.services.dataset.service:DatasetListSortService
         * @description the dataset list sort
         */
        self.datasetsSort = null;

        /**
         * @ngdoc property
         * @name datasetsOrder
         * @propertyOf data-prep.services.dataset.service:DatasetListSortService
         * @description the dataset list order
         */
        self.datasetsOrder = null;


        /**@ngdoc method
         * @name getDefaultSort
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description return the sort stored in localStorage, otherwise return the sort by default
         */
        function getDefaultSort() {
            var savedSort = $window.localStorage.getItem(sortSelectedKey);
            return !savedSort ? self.sortList[1] : _.find(self.sortList, {id: savedSort});
        }


        /**@ngdoc method
         * @name getDefaultOrder
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description return the sort order stored in localStorage, otherwise return the sort order by default
         */
        function getDefaultOrder() {
            var savedSortOrder = $window.localStorage.getItem(sortOrderSelectedKey);
            return !savedSortOrder ? self.orderList[1] : _.find(self.orderList, {id: savedSortOrder});
        }


        /**@ngdoc method
         * @name setDatasetsSort
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description update the datasets list sort
         */
        function setDatasetsSort(id) {
            self.datasetsSort = id;
            $window.localStorage.setItem(sortSelectedKey, self.datasetsSort);
        }

        /**@ngdoc method
         * @name setDatasetsOrder
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description update the datasets list sort order
         */
        function setDatasetsOrder(id) {
            self.datasetsOrder = id;
            $window.localStorage.setItem(sortOrderSelectedKey, self.datasetsOrder);
        }

        self.getDefaultSort = getDefaultSort ;
        self.getDefaultOrder = getDefaultOrder ;
        self.setDatasetsSort = setDatasetsSort ;
        self.setDatasetsOrder = setDatasetsOrder ;
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetListSortService', DatasetListSortService);
})();