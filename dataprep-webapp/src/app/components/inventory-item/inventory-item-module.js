import InventoryItemCtrl from './inventory-item-controller';
import InventoryItem from './inventory-item-directive';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.data-prep.inventory-item
     * @description This module contains the controller and directives to manage an inventory item
     * @requires talend.widget
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.inventory-item',
        [
            'pascalprecht.translate',
            'talend.widget',
            'data-prep.services.utils'
        ])
        .controller('InventoryItemCtrl', InventoryItemCtrl)
        .directive('inventoryItem', InventoryItem);
})();