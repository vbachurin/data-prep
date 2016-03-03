/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

import InventorySearchCtrl from './inventory-search-controller';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.data-prep.inventory-search
     * @description This module contains the component to manage an inventory search
     * @requires talend.widget
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.inventory-search',
        [
            'data-prep.services.utils',
            'data-prep.services.easter-eggs',
            'talend.widget',
            'data-prep.services.datasetWorkflowService',
            'data-prep.services.state',
            'data-prep.services.inventory'
        ])
        .component('inventorySearch', {
            templateUrl: 'app/components/inventory/search/inventory-search.html',
            controller: InventorySearchCtrl
        });
})();

