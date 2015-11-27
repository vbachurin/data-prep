(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.lookup.service:LookupService
     * @description Lookup service. This service provide the entry point to load lookup content
     * @requires data-prep.services.transformations.service:TransformationRestService
     * @requires data-prep.services.datasets.service:DatasetRestService
     * @requires data-prep.services.state.service:StateService
     */
    function LookupService(TransformationRestService, DatasetRestService, StateService) {
        return {
            loadContent: loadContent,
            getActions: getActions
        };

        /**
         * @ngdoc method
         * @name loadContent
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookup The lookup action
         * @description Loads the lookup dataset content
         */
        function loadContent(lookup) {
            DatasetRestService.getContentFromUrl(getDsUrl(lookup))
                .then(function (lookupDsContent) {
                    StateService.setLookupDataset(lookup);
                    StateService.setCurrentLookupData(lookupDsContent);
                });
        }

        /**
         * @ngdoc method
         * @name getActions
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {string} datasetId The dataset id
         * @description Loads the possible lookup datasets
         */
        function getActions(datasetId) {
            return TransformationRestService.getDatasetTransformations(datasetId)
                .then(function (lookup) {
                    StateService.setLookupActions(lookup.data);
                    return lookup.data;
                });
        }

        /**
         * @ngdoc method
         * @name getDsUrl
         * @methodOf data-prep.services.lookup.service:LookupService
         * @param {object} lookup dataset lookup action
         * @returns {String} The url of the lookup dataset
         * @description Extract the dataset url from lookup
         */
        function getDsUrl(lookup) {
            return _.find(lookup.parameters, {name: 'lookup_ds_url'}).default;
        }
    }

    angular.module('data-prep.services.lookup')
        .service('LookupService', LookupService);
})();