/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

'use strict';

const sortList = [
    {id: 'name', name: 'NAME_SORT', property: 'name'},
    {id: 'date', name: 'DATE_SORT', property: 'created'}
];

const orderList = [
    {id: 'asc', name: 'ASC_ORDER'},
    {id: 'desc', name: 'DESC_ORDER'}
];

export const inventoryState = {
    preparations: null,
    datasets: null,
    sortList: sortList,
    orderList: orderList,
    sort: sortList[1],
    order: orderList[1],

    currentFolder: {id: '', path: ''}, // currentFolder is initialized with root value
    currentFolderContent: {},
    foldersStack: [],
    menuChildren: []
};


export function InventoryStateService() {
    return {
        setPreparations: setPreparations,
        removePreparation: removePreparation,
        setDatasets: setDatasets,
        removeDataset: removeDataset,
        setSort: setSort,
        setOrder: setOrder,

        setCurrentFolder: setCurrentFolder,
        setCurrentFolderContent: setCurrentFolderContent,
        setFoldersStack: setFoldersStack,
        setMenuChildren: setMenuChildren
    };

    function consolidateDatasetsPreparations() {

        if (inventoryState.datasets && inventoryState.preparations) {

            //Consolidate Preparations
            _.forEach(inventoryState.preparations, function (prep) {
                prep.dataset = _.find(inventoryState.datasets, function (dataset) {
                    return dataset.id === prep.dataSetId;
                });
            });

            //Consolidate Datasets
            // group preparation per dataset
            var datasetPreps = _.groupBy(inventoryState.preparations, function (preparation) {
                return preparation.dataSetId;
            });

            // reset default preparation for all datasets
            _.forEach(inventoryState.datasets, function (dataset) {
                var preparations = datasetPreps[dataset.id];
                dataset.preparations = preparations || [];
                dataset.preparations = _.sortByOrder(dataset.preparations, 'lastModificationDate', false);
            });

            // reset default preparation for all datasets of the current folder
            _.forEach(inventoryState.currentFolderContent.datasets, function (dataset) {
                var preparations = datasetPreps[dataset.id];
                dataset.preparations = preparations || [];
                dataset.preparations = _.sortByOrder(dataset.preparations, 'lastModificationDate', false);
            });
        }
    }

    /**
     * @ngdoc method
     * @name setPreparations
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {object} preparations The preparations list
     * @description Set preparations in Inventory
     */
    function setPreparations(preparations) {
        inventoryState.preparations = preparations;
        consolidateDatasetsPreparations();
    }

    /**
     * @ngdoc method
     * @name removePreparation
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {object} preparation The preparation
     * @description Remove a preparation
     */
    function removePreparation(preparation) {
        inventoryState.preparations = _.reject(inventoryState.preparations, {id: preparation.id});
    }

    /**
     * @ngdoc method
     * @name setDatasets
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {object} datasets The datasets list
     * @description Set datasets in Inventory
     */
    function setDatasets(datasets) {
        inventoryState.datasets = datasets;
        consolidateDatasetsPreparations();
    }

    /**
     * @ngdoc method
     * @name removeDataset
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {object} dataset The dataset
     * @description Remove a dataset
     */
    function removeDataset(dataset) {
        inventoryState.datasets = _.reject(inventoryState.datasets, {id: dataset.id});
    }

    /**
     * @ngdoc method
     * @name setSort
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {object} sort The sort type
     * @description Set rhe sort type
     */
    function setSort(sort) {
        inventoryState.sort = sort;
    }

    /**
     * @ngdoc method
     * @name setOrder
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {object} order The order
     * @description Set the order
     */
    function setOrder(order) {
        inventoryState.order = order;
    }

    /**
     * @ngdoc method
     * @name setCurrentFolder
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {object} folder The current folder
     * @description Update the current folder
     */
    function setCurrentFolder(folder) {
        inventoryState.currentFolder = folder;
    }

    /**
     * @ngdoc method
     * @name setCurrentFolderContent
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {object} children The content of the current folder
     */
    function setCurrentFolderContent(children) {
        inventoryState.currentFolderContent = children;

        if (inventoryState.preparations) {
            // group preparation per dataset
            var datasetPreps = _.groupBy(inventoryState.preparations, function (preparation) {
                return preparation.dataSetId;
            });

            // reset default preparation for all datasets of the current folder
            _.forEach(inventoryState.currentFolderContent.datasets, function (dataset) {
                var preparations = datasetPreps[dataset.id];
                dataset.preparations = preparations || [];
                dataset.preparations = _.sortByOrder(dataset.preparations, 'lastModificationDate', false);
            });
        }
    }

    /**
     * @ngdoc method
     * @name setFoldersStack
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {object} stack The current folders stack
     */
    function setFoldersStack(stack) {
        inventoryState.foldersStack = stack;
    }

    /**
     * @ngdoc method
     * @name setMenuChildren
     * @methodOf data-prep.services.state.service:InventoryStateService
     * @param {array} children The current children of the current menu entry
     */
    function setMenuChildren(children) {
        inventoryState.menuChildren = children;
    }
}