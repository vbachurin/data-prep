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

        /**
         * Call a transformation action on a dataset
         * @param datasetId - the dataset id
         * @param action - the action name (ex: "uppercase")
         * @param parameters - the parameters corresponding to the action
         * @returns {HttpPromise}
         */
        this.transform = function (datasetId, action, parameters) {
            var actionParam = adaptTransformAction(action, parameters);
            return $http.post(RestURLs.transformUrl + '/' + datasetId + '/?actions=' + jQuery.param(actionParam));
        };

    }

    angular.module('data-prep-transformation')
        .service('TransformationService', TransformationService);
})();