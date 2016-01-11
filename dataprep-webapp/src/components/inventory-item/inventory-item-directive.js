(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.inventoryItem.directive:InventoryItem
     * @description This directive display an inventory item
     * @restrict E
     */
    function InventoryItem() {
        return {
            restrict: 'E',
            templateUrl: 'components/inventory-item/inventory-item.html',
            replace: true,
            bindToController: true,
            controllerAs: 'inventoryItemCtrl',
            controller: 'InventoryItemCtrl',
            scope: {
                item: '=',
                details: '@',
                type: '@',
                fileModel: '=',
                open: '=',
                rename: '=',
                remove: '=',
                update: '=',
                copy: '=',
                processCertification: '=',
                toogleFavorite: '=',
                actionsEnabled: '='
            }
        };
    }

    angular.module('data-prep.inventory-item')
        .directive('inventoryItem', InventoryItem);
})();