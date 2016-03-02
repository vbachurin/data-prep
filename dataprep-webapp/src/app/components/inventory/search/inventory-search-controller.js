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
export default function InventorySearchCtrl(EasterEggsService) {
    'ngInject';
    var vm = this;
    //--------------------------------------------------------------------------------------------------------------
    //----------------------------------------------SEARCH----------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------

    vm.searchInput = '';
    vm.search = function () {
        // just in case something funny happens...
        EasterEggsService.enableEasterEgg(vm.searchInput);
    };

}