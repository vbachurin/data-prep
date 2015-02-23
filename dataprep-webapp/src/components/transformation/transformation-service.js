(function () {
    'use strict';

    function TransformationService($http, RestURLs) {
        var adaptTransformAction = function(action, parameters) {
            return {
                actions: [{
                    action: action,
                    parameters: parameters
                }]
            };
        };

        this.getTransformations = function(datasetId, column) {
            var cleanDatasetId = encodeURIComponent(datasetId);
            var cleanColumn = encodeURIComponent(column);
            return $http.get(RestURLs.datasetUrl + '/' + cleanDatasetId + '/' + cleanColumn + '/actions');
        };

        /**
         * Call a transformation action on a dataset
         * @param datasetId - the dataset id
         * @param action - the action name (ex: "uppercase")
         * @param parameters - the parameters corresponding to the action
         * @returns {HttpPromise}
         */
        this.transform = function (datasetId, action, parameters) {
            var actionParam = adaptTransformAction(action, parameters);
            var request = {
                method: 'POST',
                url: RestURLs.transformUrl + '/' + datasetId,
                headers: {
                    'Content-Type': 'application/json'
                },
                data: actionParam
            };

            return $http(request);
        };

    }

    angular.module('data-prep-transformation')
        .service('TransformationService', TransformationService);
})();