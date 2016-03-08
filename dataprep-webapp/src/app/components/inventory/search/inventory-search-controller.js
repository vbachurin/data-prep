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
 * @name data-prep.inventory-search.controller:InventorySearchCtrl
 * @description InventorySearchCtrl controller.
 * @requires data-prep.services.state.constant:state
 * @requires data-prep.services.state.service:StateService
 * @requires data-prep.services.inventory.service:InventoryService
 * @requires data-prep.services.datasetWorkflowService:UploadWorkflowService
 * @requires data-prep.services.folder.service:FolderService
 * @requires data-prep.services.preparation.service:PreparationService
 *
 */
export default function InventorySearchCtrl($state, $stateParams, state, UploadWorkflowService, StateService, InventoryService, FolderService, PreparationService) {
    'ngInject';
    var vm = this;
    vm.state = state;
    vm.uploadWorkflowService = UploadWorkflowService;
    vm.folderService = FolderService;
    vm.preparationService = PreparationService;

    vm.searchInput = '';

    /**
     * @ngdoc method
     * @name search
     * @methodOf data-prep.inventory-search.controller:InventorySearchCtrl
     * @description Search based on searchInput
     */
    vm.search = function () {
        InventoryService.search(vm.searchInput)
        .then((response)=> {
                vm.results = response;
        });
    };
}