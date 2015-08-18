(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.preparation.service:PreparationRestService
     * @description Preparation service. This service provides the entry point to preparation REST api. It holds the loaded preparation.<br/>
     * <b style="color: red;">WARNING : do NOT use this service directly.
     * {@link data-prep.services.preparation.service:PreparationService PreparationService} must be the only entry point for preparations</b>
     */
    function PreparationRestService($http, RestURLs) {
        return {
            //lifecycle
            create: create,
            update: update,
            delete: deletePreparation,

            //step
            appendStep: appendStep,
            updateStep: updateStep,
            removeStep: removeStep,

            //getter : list, content, details
            getPreparations: getPreparations,
            getContent: getContent,
            getDetails: getDetails,

            //preview
            getPreviewDiff: getPreviewDiff,
            getPreviewUpdate: getPreviewUpdate,
            getPreviewAdd: getPreviewAdd,
        };

        //---------------------------------------------------------------------------------
        //----------------------------------------GETTERS----------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name getPreparations
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @description Get All the user's preparations
         * @returns {promise} - the GET promise
         */
        function getPreparations() {
            return $http.get(RestURLs.preparationUrl);
        }

        /**
         * @ngdoc method
         * @name getContent
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preparation id to load
         * @param {string} version The version (step id) to load
         * @param {int} sample optional sample size
         * @description Get preparation records at the specific 'version' step
         * @returns {promise} The GET promise
         */
        function getContent(preparationId, version, sample) {
            var url = RestURLs.preparationUrl + '/' + preparationId + '/content?version=' + version;
            if (sample) {
                url += '&sample='+sample;
            }
            return $http.get(url);
        }

        /**
         * @ngdoc method
         * @name getDetails
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preparation id to load
         * @description Get current preparation details
         * @returns {promise} The GET promise
         */
        function getDetails(preparationId) {
            return $http.get(RestURLs.preparationUrl + '/' + preparationId + '/details');
        }

        //---------------------------------------------------------------------------------
        //---------------------------------------LIFECYCLE---------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name create
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} datasetId The dataset id
         * @param {string} name The preparation name
         * @description Create a new preparation
         * @returns {promise} The POST promise
         */
        function create(datasetId, name) {
            var request = {
                method: 'POST',
                url: RestURLs.preparationUrl,
                data: {
                    name: name,
                    dataSetId: datasetId
                }
            };

            return $http(request);
        }

        /**
         * @ngdoc method
         * @name update
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preparation id
         * @param {string} name The new preparation name
         * @description Update the current preparation name
         * @returns {promise} The PUT promise
         */
        function update(preparationId, name) {
            var request = {
                method: 'PUT',
                url: RestURLs.preparationUrl + '/' + preparationId,
                headers: {
                    'Content-Type': 'application/json'
                },
                data: {name: name}
            };

            return $http(request);
        }

        /**
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {object} preparationId The preparation id to delete
         * @description Delete a preparation
         * @returns {promise} The DELETE promise
         */
        function deletePreparation(preparationId) {
            return $http.delete(RestURLs.preparationUrl + '/' + preparationId);
        }

        //---------------------------------------------------------------------------------
        //-----------------------------------------STEPS-----------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name adaptTransformAction
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} action - the action to adapt
         * @param {object} parameters - the action parameters
         * @description [PRIVATE] Adapt transformation action to api
         * @returns {object} - the adapted action
         */
        function adaptTransformAction (action, parameters) {
            return {
                actions: [{
                    action: action,
                    parameters: parameters
                }]
            };
        }

        /**
         * @ngdoc method
         * @name appendStep
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {object} preparationId The preparation id
         * @param {string} action - the action to append
         * @param {object} parameters - the action parameters
         * @description Append a new transformation in the current preparation.
         * @returns {promise} - the POST promise
         */
        function appendStep(preparationId, action, parameters) {
            var actionParam = adaptTransformAction(action, parameters);
            var request = {
                method: 'POST',
                url: RestURLs.preparationUrl + '/' + preparationId + '/actions',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: actionParam
            };

            return $http(request);
        }

        /**
         * @ngdoc method
         * @name updateStep
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preaparation id to update
         * @param {string} stepId The step to update
         * @param {string} action The action name
         * @param {object} parameters The new action parameters
         * @description Update a step with new parameters
         * @returns {promise} The PUT promise
         */
        function updateStep(preparationId, stepId, action, parameters) {
            var actionParam = adaptTransformAction(action, parameters);
            var request = {
                method: 'PUT',
                url: RestURLs.preparationUrl + '/' + preparationId + '/actions/' + stepId,
                headers: {
                    'Content-Type': 'application/json'
                },
                data: actionParam
            };

            return $http(request);
        }

        /**
         * @ngdoc method
         * @name removeStep
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preaparation id to update
         * @param {string} stepId The step to delete
         * @description Delete a step
         * @returns {promise} The DELETE promise
         */
        function removeStep(preparationId, stepId) {
            return $http.delete(RestURLs.preparationUrl + '/' + preparationId + '/actions/' + stepId);
        }

        //---------------------------------------------------------------------------------
        //----------------------------------------PREVIEW----------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name getPreviewDiff
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preparation id to preview
         * @param {string} currentStep The current loaded step
         * @param {string} previewStep The target preview step
         * @param {string} recordsTdpId The records TDP ids to preview
         * @param {string} canceler The canceler promise
         * @description POST Preview diff between 2 unchanged steps of a recipe
         * @returns {promise} The POST promise
         */
        function getPreviewDiff(preparationId, currentStep, previewStep, recordsTdpId, canceler) {
            var params = {
                tdpIds: recordsTdpId,
                currentStepId: currentStep.transformation.stepId,
                previewStepId: previewStep.transformation.stepId,
                preparationId: preparationId
            };

            var request = {
                method: 'POST',
                url: RestURLs.previewUrl + '/diff',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: params,
                timeout: canceler.promise
            };

            return $http(request);
        }

        /**
         * @ngdoc method
         * @name getPreviewUpdate
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preparation id to preview
         * @param {string} currentStep The current loaded step
         * @param {string} updateStep The target step to update
         * @param {string} newParams The new parameters
         * @param {string} recordsTdpId The records TDP ids to preview
         * @param {string} canceler The canceler promise
         * @description POST preview diff between 2 same actions but with 1 updated step
         * @returns {promise} The POST promise
         */
        function getPreviewUpdate(preparationId, currentStep, updateStep, newParams, recordsTdpId, canceler) {
            var actionParam = {
                action : {
                    action: updateStep.actionParameters.action,
                    parameters: newParams
                },
                tdpIds: recordsTdpId,
                currentStepId: currentStep.transformation.stepId,
                updateStepId: updateStep.transformation.stepId,
                preparationId: preparationId
            };

            var request = {
                method: 'POST',
                url: RestURLs.previewUrl + '/update',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: actionParam,
                timeout: canceler.promise
            };

            return $http(request);
        }

        /**
         * @ngdoc method
         * @name getPreviewAdd
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preparation id to preview
         * @param {string} datasetId The dataset id to preview
         * @param {string} action The action to add
         * @param {string} params The action parameters
         * @param {string} recordsTdpId The records TDP ids to preview
         * @param {string} canceler The canceler promise
         * @description POST preview diff between the preparation head and a new added transformation
         * @returns {promise} The POST promise
         */
        function getPreviewAdd(preparationId, datasetId, action, params, recordsTdpId, canceler) {
            var actionParam = {
                action : {
                    action: action,
                    parameters: params
                },
                tdpIds: recordsTdpId,
                datasetId: datasetId,
                preparationId: preparationId
            };

            var request = {
                method: 'POST',
                url: RestURLs.previewUrl + '/add',
                headers: {
                    'Content-Type': 'application/json'
                },
                data: actionParam,
                timeout: canceler.promise
            };

            return $http(request);
        }
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationRestService', PreparationRestService);
})();