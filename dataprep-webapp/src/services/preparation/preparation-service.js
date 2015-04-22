(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.preparation.service:PreparationService
     * @description Preparation service. This service provides the entry point to preparation REST api. It holds the loaded preparation.
     */
    function PreparationService($http, RestURLs) {
        var self = this;

        /**
         * @ngdoc property
         * @name currentPreparation
         * @propertyOf data-prep.services.preparation.service:PreparationService
         * @description the currently loaded preparation
         */
        this.currentPreparation = null;

        /**
         * @ngdoc method
         * @name adaptTransformAction
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} action - the action to adapt
         * @param {object} parameters - the action parameters
         * @description [PRIVATE] Adapt transformation action to api
         * @returns {object} - the adapted action
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
         * @ngdoc method
         * @name getPreparations
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description Get All the user's preparations
         * @returns {promise} - the GET promise
         */
        this.getPreparations = function() {
            return $http.get(RestURLs.preparationUrl);
        };

        /**
         * @ngdoc method
         * @name create
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} datasetId - the dataset id
         * @param {string} name - the preparation name
         * @description Create a new preparation
         * @returns {promise} - the POST promise
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
         * @ngdoc method
         * @name update
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} name - the new preparation name
         * @description Update the current preparation name
         * @returns {promise} - the PUT promise
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
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} preparation - the preparation to delete
         * @description Delete a preparation
         * @returns {promise} - the DELETE promise
         */
        this.delete = function(preparation) {
            return $http.delete(RestURLs.preparationUrl + '/' + preparation.id);
        };

        /**
         * @ngdoc method
         * @name append
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} datasetId - the dataset id for creation
         * @param {string} action - the action to append
         * @param {object} parameters - the action parameters
         * @description Append a new transformation in the current preparation. If the preparation does not exists yet, it is created
         * @returns {promise} - the POST promise
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
         * @ngdoc method
         * @name getContent
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} version - the version (step id) to load
         * @description Get preparation records at the specific 'version' step
         * @returns {promise} - the GET promise
         */
        this.getContent = function(version) {
            return $http.get(RestURLs.preparationUrl + '/' + self.currentPreparation + '/content?version=' + version);
        };

        /**
         * @ngdoc method
         * @name getDetails
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description Get current preparation details
         * @returns {promise} - the GET promise
         */
        this.getDetails = function() {
            return $http.get(RestURLs.preparationUrl + '/' + self.currentPreparation + '/details');
        };
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationService', PreparationService);
})();