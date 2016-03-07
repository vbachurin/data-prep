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
 * @name data-prep.services.lookup.service:InventoryService
 * @description Inventory service.
 */
export default function InventoryService($q, InventoryRestService, TextFormatService) {
    'ngInject';

    var searchPromise, deferredCancel;

    return {
        search :search
    };

    /**
     * @ngdoc method
     * @name cancelPendingGetRequest
     * @methodOf data-prep.services.lookup.service:InventoryService
     * @description Cancel the pending search GET request
     */
    function cancelPendingGetRequest() {
        if (searchPromise) {
            deferredCancel.resolve('user cancel');
            searchPromise = null;
        }
    }

    /**
     * @ngdoc method
     * @name search
     * @methodOf data-prep.services.lookup.service:InventoryService
     * @param {String} searchValue string
     */
    function search(searchValue) {
        cancelPendingGetRequest();

        deferredCancel = $q.defer();

        searchPromise = InventoryRestService.search(searchValue, deferredCancel)
            .then((response) => {
                if(response.data.datasets) {
                    _.chain(response.data.datasets)
                        .map(highlightDisplayedLabels(searchValue))
                        .value();
                }

                if(response.data.preparations) {
                    _.chain(response.data.preparations)
                        .map(highlightDisplayedLabels(searchValue))
                        .value();
                }

                if(response.data.folders) {
                    _.chain(response.data.folders)
                        .map(highlightDisplayedLabels(searchValue))
                        .value();
                }

                return response.data;
            })
            .finally(() => searchPromise = null);

        return searchPromise;
    }


    function highlightDisplayedLabels(searchValue) {
        return function (item) {
            highlight(item, 'name', searchValue);
            return item;
        };
    }

    function highlight(object, key, highlightText) {
        var originalValue = object[key];
        if (originalValue.toLowerCase().indexOf(highlightText) !== -1) {
            object[key] = originalValue.replace(
                new RegExp('(' + TextFormatService.escapeRegex(highlightText) + ')', 'gi'),
                '<span class="highlighted">$1</span>');
        }
    }



}