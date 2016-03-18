/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/


/**
 * @ngdoc controller
 * @name data-prep.dataset-header.controller:DatasetHeaderCtrl
 * @description Dataset list header controller.
 * @requires data-prep.services.state.constant:state
 * @requires data-prep.services.state.service:StateService
 * @requires data-prep.services.utils.service:StorageService
 * @requires data-prep.services.folder.service:FolderService
 */
export default class DatasetHeaderCtrl {
    constructor(state, StateService, StorageService, FolderService) {
        'ngInject';
        this.state = state;
        this.StateService = StateService;
        this.StorageService = StorageService;
        this.FolderService = FolderService;
    }

    $onInit() {
        this.FolderService.refreshDatasetsSort();
        this.FolderService.refreshDatasetsOrder();
    }

    /**
     * @ngdoc method
     * @name openFolderModal
     * @methodOf data-prep.dataset-header.controller:DatasetHeaderCtrl
     * @description Reset folder name and show folder modal
     */
    openFolderModal() {
        this.folderName = '';
        this.folderNameModal = true;
    }

    /**
     * @ngdoc method
     * @name addFolder
     * @methodOf data-prep.dataset-header.controller:DatasetHeaderCtrl
     * @description Create a new folder
     */
    addFolder() {
        this.folderNameForm.$commitViewValue();

        const pathToCreate = (this.state.inventory.currentFolder.path ? this.state.inventory.currentFolder.path : '') + '/' + this.folderName;
        this.FolderService.create(pathToCreate)
            .then(() => {
                this.FolderService.getContent(this.state.inventory.currentFolder);
                this.folderNameModal = false;
            });
    }

    /**
     * @ngdoc method
     * @name sort
     * @methodOf data-prep.dataset-header.controller:DatasetHeaderCtrl
     * @description sort dataset by sortType by calling refreshDatasets from DatasetService
     * @param {object} sortType Criteria to sort
     */
    updateSortBy(sortType) {
        if (this.state.inventory.sort.id === sortType.id) {
            return;
        }

        const oldSort = this.state.inventory.sort;

        this.StateService.setDatasetsSort(sortType);
        this.StorageService.setDatasetsSort(sortType.id);

        this.FolderService.getContent(this.state.inventory.currentFolder)
            .catch(() => {
                this.StateService.setDatasetsSort(oldSort);
                this.StorageService.setDatasetsSort(oldSort.id);
            });
    }

    /**
     * @ngdoc method
     * @name sort
     * @methodOf data-prep.dataset-header.controller:DatasetHeaderCtrl
     * @description sort dataset in order (ASC or DESC) by calling refreshDatasets from DatasetService
     * @param {object} order Sort order ASC(ascending) or DESC(descending)
     */
    updateSortOrder(order) {
        if (this.state.inventory.order.id === order.id) {
            return;
        }

        const oldOrder = this.state.inventory.order;

        this.StateService.setDatasetsOrder(order);
        this.StorageService.setDatasetsOrder(order.id);

        this.FolderService.getContent(this.state.inventory.currentFolder)
            .catch(() => {
                this.StateService.setDatasetsOrder(oldOrder);
                this.StorageService.setDatasetsOrder(oldOrder.id);
            });
    }
}