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
 */
export default function InventorySearchCtrl($state, $stateParams, state, EasterEggsService, UploadWorkflowService, StateService) {
    'ngInject';
    var vm = this;
    vm.state = state;
    vm.uploadWorkflowService = UploadWorkflowService;

    //--------------------------------------------------------------------------------------------------------------
    //----------------------------------------------SEARCH----------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------

    vm.searchInput = '';
    vm.search = function () {
        // just in case something funny happens...
        //EasterEggsService.enableEasterEgg(vm.searchInput);
    };

    vm.goToFolder = function goToFolder(folder) {
        $state.go('nav.index.datasets', {folderPath: folder.path});
    };

    vm.openPreparation = function openPreparation(preparation) {
        StateService.setPreviousState('nav.index.datasets');
        StateService.setPreviousStateOptions({folderPath: $stateParams.folderPath});
        $state.go('playground.preparation', {prepid: preparation.id});
    };
}