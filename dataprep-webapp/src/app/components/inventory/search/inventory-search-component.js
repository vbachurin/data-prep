/**
 * @ngdoc directive
 * @name data-prep.data-prep.inventory-search
 * @description This directive display an inventory search
 * @restrict E
 *
 * @usage
 * <inventory-search
 * </inventory-search>

 */

import InventorySearchCtrl from './inventory-search-controller';

const InventorySearch = {
    templateUrl: 'app/components/inventory/search/inventory-search.html',
    controller: InventorySearchCtrl
};

export default InventorySearch;