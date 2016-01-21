(function () {
    'use strict';

    var sortList = [
        {id: 'name', name: 'NAME_SORT', property: 'name'},
        {id: 'date', name: 'DATE_SORT', property: 'created'}
    ];

    var orderList = [
        {id: 'asc', name: 'ASC_ORDER'},
        {id: 'desc', name: 'DESC_ORDER'}
    ];

    var inventoryState = {
        preparations: null,
        datasets: null,
        sortList: sortList,
        orderList: orderList
    };


    function InventoryStateService() {
        return {
            setPreparations: setPreparations,
            removePreparation: removePreparation,
            setDatasets: setDatasets,
            removeDataset: removeDataset,
            getSortItem: getSortItem,
            getOrderItem: getOrderItem
        };

        function setPreparations(preparations) {
            inventoryState.preparations = preparations;
        }

        function removePreparation(preparation) {
            inventoryState.preparations = _.reject(inventoryState.preparations, {id: preparation.id});
        }

        function setDatasets(datasets) {
            inventoryState.datasets = datasets;
        }

        function removeDataset(dataset) {
            inventoryState.datasets = _.reject(inventoryState.datasets, {id: dataset.id});
        }

        function getSortItem(sortId) {
            return _.find(sortList, {id: sortId});
        }

        function getOrderItem(orderId) {
            return _.find(orderList, {id: orderId});
        }

    }

    angular.module('data-prep.services.state')
        .service('InventoryStateService', InventoryStateService)
        .constant('inventoryState', inventoryState);
})();