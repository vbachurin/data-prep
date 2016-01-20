(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.inventory-item.controller:InventoryItemCtrl
     * @description InventoryItemCtrl controller.
     */
    function InventoryItemCtrl($state) {
        var vm = this;

        vm.openOnClick = function openOnClick(item) {
            if(vm.actionsEnabled){
                if(item.defaultPreparations && item.defaultPreparations.length){
                    $state.go('nav.home.preparations', {prepid: item.defaultPreparations[0].id});
                }
                else{
                    vm.open(item);
                }
            }
        };
    }
    angular.module('data-prep.inventory-item')
        .controller('InventoryItemCtrl', InventoryItemCtrl);
})();
