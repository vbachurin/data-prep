(function() {
    'use strict';

    function StorageService($window) {
        var PREFIX = 'org.talend.dataprep.';

        return {
            setAggregation: setAggregation,
            getAggregation: getAggregation,
            removeAggregation: removeAggregation
        };

        //--------------------------------------------------------------------------------------------------------------
        //---------------------------------------------------Aggregation------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        function getAggregationKey(datasetId, preparationId, columnId) {
            var key = PREFIX + 'aggregation.';
            key += (datasetId ? datasetId : '') + '.';
            key += (preparationId ? preparationId : '') + '.';
            key += columnId;

            return key;
        }

        function setAggregation(datasetId, preparationId, columnId, aggregation) {
            var key = getAggregationKey(datasetId, preparationId, columnId);
            $window.localStorage.setItem(key, JSON.stringify(aggregation));
        }

        function getAggregation(datasetId, preparationId, columnId) {
            var key = getAggregationKey(datasetId, preparationId, columnId);
            var item = $window.localStorage.getItem(key);
            return JSON.parse(item);
        }

        function removeAggregation(datasetId, preparationId, columnId) {
            var key = getAggregationKey(datasetId, preparationId, columnId);
            $window.localStorage.removeItem(key);
        }
    }

    angular.module('data-prep.services.utils')
        .service('StorageService', StorageService);
})();