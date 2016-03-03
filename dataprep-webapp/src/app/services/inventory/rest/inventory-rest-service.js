/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

/**
 * @ngdoc service
 * @name data-prep.services.inventory.service:InventoryRestService
 * @description Inventory service.
 */
export default function InventoryRestService(state, $q, $http) {
    'ngInject';

    return {
        search: search
    };

    /**
     * @ngdoc method
     * @name search
     * @methodOf data-prep.services.inventory.service:InventoryRestService
     * @param {String} search string
     * @param {Promise} deferredAbort abort request when resolved
     */
    function search(searchString, deferredAbort) {
        return $q.when({
            datasets: _.cloneDeep(state.inventory.currentFolderContent.datasets),
            folders: _.cloneDeep(state.inventory.currentFolderContent.folders),
            preparations: _.cloneDeep(state.inventory.preparations)
        });
        //return $http({
        //    url: RestURLs.datasetUrl,
        //    method: 'POST',
        //    timeout: deferredAbort.promise,
        //    data: searchString
        //});
    }
}