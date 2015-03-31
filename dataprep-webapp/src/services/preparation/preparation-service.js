(function() {
    'use strict';

    function PreparationService($http, RestURLs) {
        var self = this;
        this.currentPreparation = null;

        /**
         * Adapt transformation action to api
         * @param action - the action name
         * @param parameters - the action parameters
         * @returns {{actions: {action: *, parameters: *}[]}}
         */
        var adaptTransformAction = function(action, parameters) {
            return {
                actions: [{
                    action: action,
                    parameters: parameters
                }]
            };
        };

        /**
         * Get All the user preparations
         * @returns HttpPromise
         */
        this.getPreparations = function() {
            return $http.get(RestURLs.preparationUrl);
        };

        /**
         * Create a new preparation
         * @param datasetId - the dataset id
         * @param name - the name of the new preparation
         * @returns HttpPromise
         */
        this.create = function(datasetId, name) {
            var request = {
                method: 'POST',
                url: RestURLs.preparationUrl,
                data: {
                    name: name,
                    dataSetId: datasetId
                }
            };

            return $http(request).then(function(resp) {
                self.currentPreparation = resp.data;
            });
        };

        /**
         * Update the current preparation name
         * @param name
         */
        this.update = function(name) {
            var request = {
                method: 'PUT',
                url: RestURLs.preparationUrl + '/' + self.currentPreparation,
                headers: {
                    'Content-Type': 'application/json'
                },
                data: {name: name}
            };

            return $http(request);
        };

        /**
         * Append a new transformation in the current preparation
         * @return HttpPromise
         */
        this.append = function(datasetId, action, parameters) {
            var appendOperation = function() {
                var actionParam = adaptTransformAction(action, parameters);
                var request = {
                    method: 'POST',
                    url: RestURLs.preparationUrl + '/' + self.currentPreparation + '/actions',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    data: actionParam
                };

                return $http(request);
            };

            if(!self.currentPreparation) {
                return self.create(datasetId, 'New preparation').then(appendOperation);
            }
            return appendOperation();
        };

        /**
         * Get preparation records at the 'version' step
         * @param version - the content version. 'head' or step id
         * @return HttpPromise
         */
        this.getContent = function(version) {
            return $http.get(RestURLs.preparationUrl + '/' + self.currentPreparation + '/content?version=' + version);
        };

        /**
         * Get current preparation details
         * @returns HttpPromise
         */
        this.getDetails = function() {
            return $http.get(RestURLs.preparationUrl + '/' + self.currentPreparation + '/details');
        };
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationService', PreparationService);
})();