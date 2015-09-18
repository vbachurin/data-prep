(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:DatasetListSortService
     * @description Dataset list sort parameters service
     */
    function DatasetListSortService($window) {
        var sortLocalStorageKey = 'org.talend.dataprep.dataset.sort';
        var orderLocalStorageKey = 'org.talend.dataprep.dataset.order';

        /**
         * @ngdoc property
         * @name sortList
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description List of available sort.
         * @type {object[]}
         */
        var sortList = [
            {id: 'name', name: 'NAME_SORT'},
            {id: 'date', name: 'DATE_SORT'}
        ];

        /**
         * @ngdoc property
         * @name orderList
         * @propertyOf data-prep.dataset-list.controller:DatasetListCtrl
         * @description List of available order.
         * @type {object[]}
         */
        var orderList = [
            {id: 'asc', name: 'ASC_ORDER'},
            {id: 'desc', name: 'DESC_ORDER'}
        ];

        /**
         * @ngdoc property
         * @name datasetsSort
         * @propertyOf data-prep.services.dataset.service:DatasetListSortService
         * @description The selected sort parameter
         */
        var sort = getDefaultSort();

        /**
         * @ngdoc property
         * @name datasetsOrder
         * @propertyOf data-prep.services.dataset.service:DatasetListSortService
         * @description The selected sort order
         */
        var order = getDefaultOrder();

        return {
            getSortList: getSortList,
            getSort: getSort,
            getSortItem: getSortItem,
            setSort: setSort,

            getOrderList: getOrderList,
            getOrder: getOrder,
            getOrderItem: getOrderItem,
            setOrder: setOrder
        };

        /**
         * @ngdoc method
         * @name getDefaultSort
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Returns the sort parameter from localStorage if it exists, or the sort by default otherwise
         */
        function getDefaultSort() {
            var savedSort = $window.localStorage.getItem(sortLocalStorageKey);
            return savedSort ? savedSort : sortList[1].id;
        }

        /**
         * @ngdoc method
         * @name getDefaultOrder
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Returns the sort order from localStorage if it exists, or the sort order by default otherwise
         */
        function getDefaultOrder() {
            var savedSortOrder = $window.localStorage.getItem(orderLocalStorageKey);
            return savedSortOrder ? savedSortOrder : orderList[1].id;
        }

        /**
         * @ngdoc method
         * @name getSortList
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Returns the list of available sort parameters
         */
        function getSortList() {
            return sortList.slice(0);
        }

        /**
         * @ngdoc method
         * @name getOrderList
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Returns the list of available sort order parameters
         */
        function getOrderList() {
            return orderList.slice(0);
        }

        /**
         * @ngdoc method
         * @name getSort
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Returns the actual sort parameter
         */
        function getSort() {
            return sort;
        }

        /**
         * @ngdoc method
         * @name getSortItem
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Returns the actual sort parameter item from sort list
         */
        function getSortItem() {
            return _.find(sortList, {id: sort});
        }

        /**
         * @ngdoc method
         * @name getOrderItem
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Returns the actual order parameter item from order list
         */
        function getOrderItem() {
            return _.find(orderList, {id: order});
        }

        /**
         * @ngdoc method
         * @name getOrder
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Returns the actual order parameter
         */
        function getOrder() {
            return order;
        }

        /**
         * @ngdoc method
         * @name setSort
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Update the sort parameter locally and in localStorage
         */
        function setSort(id) {
            sort = id;
            $window.localStorage.setItem(sortLocalStorageKey, id);
        }

        /**
         * @ngdoc method
         * @name setOrder
         * @methodOf data-prep.services.dataset.service:DatasetListSortService
         * @description Update the order parameter locally and in localStorage
         */
        function setOrder(id) {
            order = id;
            $window.localStorage.setItem(orderLocalStorageKey, id);
        }
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetListSortService', DatasetListSortService);
})();