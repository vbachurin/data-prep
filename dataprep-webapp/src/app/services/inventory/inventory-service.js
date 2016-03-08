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
     * @description Search inventory items
     */
    function search(searchValue) {
        cancelPendingGetRequest();

        deferredCancel = $q.defer();

        searchPromise = InventoryRestService.search(searchValue, deferredCancel)
            .then((response) => {
                return addHtmlLabels(searchValue, response.data);
            })
            .finally(() => searchPromise = null);

        return searchPromise;
    }

    /**
     * @ngdoc method
     * @name addHtmlLabels
     * @param {String} searchValue string
     * @param {Object} data data to process
     * @description add html label to data based on searchValue
     */
    function addHtmlLabels(searchValue, data) {
        if(data.datasets) {
            _.chain(data.datasets)
                .map(highlightDisplayedLabels(searchValue))
                .value();
        }
        if(data.preparations) {
            _.chain(data.preparations)
                .map(highlightDisplayedLabels(searchValue))
                .value();
        }
        if(data.folders) {
            _.chain(data.folders)
                .map(highlightDisplayedLabels(searchValue))
                .value();
        }
        return data;
    }

    /**
     * @ngdoc method
     * @name highlightDisplayedLabels
     * @param {String} searchValue string
     * @description define property to highlight
     */
    function highlightDisplayedLabels(searchValue) {
        return function (item) {
            highlight(item, 'name', searchValue);
            return item;
        };
    }

    /**
     * @ngdoc method
     * @name highlight
     * @param {Object} object
     * @param {Integer} key
     * @param {String} highlightText text to highlight
     * @description highlight an item of the object
     */
    function highlight(object, key, highlightText) {
        var originalValue = object[key];
        if (originalValue.toLowerCase().indexOf(highlightText) !== -1) {
            object[key] = originalValue.replace(
                new RegExp('(' + TextFormatService.escapeRegex(highlightText) + ')', 'gi'),
                '<span class="highlighted">$1</span>');
        }
    }



}