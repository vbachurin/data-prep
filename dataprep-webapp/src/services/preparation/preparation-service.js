(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.preparation.service:PreparationService
     * @description Preparation list service. this service manage the operations that touches the preparations
     * @requires data-prep.services.preparation.service:PreparationListService
     * @requires data-prep.services.preparation.service:PreparationRestService
     * @requires data-prep.services.dataset.service:DatasetListService
     */
    function PreparationService($q, PreparationListService, PreparationRestService, DatasetListService) {
        return {
            //get, refresh preparations
            preparationsList: preparationsList,
            refreshPreparations: refreshPreparations,
            getPreparations: getPreparations,

            //details, content
            getContent: getContent,
            getDetails: getDetails,

            //preparation lifecycle
            create: create,
            delete: deletePreparation,
            setName: setName,

            //preparation steps lifecycle
            copyImplicitParameters: copyImplicitParameters,
            paramsHasChanged: paramsHasChanged,
            appendStep: appendStep,
            updateStep: updateStep,
            removeStep: removeStep,

            //preview
            getPreviewDiff: getPreviewDiff,
            getPreviewUpdate: getPreviewUpdate,
            getPreviewAdd: getPreviewAdd
        };

        //---------------------------------------------------------------------------------
        //------------------------------------GET/REFRESH----------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name preparationsList
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description Return the preparations list. See {@link data-prep.services.preparation.service:PreparationListService PreparationListService}.preparations
         * @returns {object[]} The preparations list
         */
        function preparationsList() {
            return PreparationListService.preparations;
        }

        /**
         * @ngdoc method
         * @name refreshPreparationsMetadata
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description [PRIVATE] Refresh the default preparation within each dataset
         */
        function consolidatePreparationsAndDatasets(response) {
            DatasetListService.refreshDefaultPreparation(preparationsList())
                .then(PreparationListService.refreshMetadataInfos);
            return response;
        }

        /**
         * @ngdoc method
         * @name refreshPreparations
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description Refresh the preparations list
         * @returns {promise} The process promise
         */
        function refreshPreparations() {
            return PreparationListService.refreshPreparations()
                .then(consolidatePreparationsAndDatasets);
        }

        /**
         * @ngdoc method
         * @name getPreparations
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description Return preparation promise that resolve current preparation list if not empty, or call GET service
         * @returns {promise} The process promise
         */
        function getPreparations() {
            return preparationsList() !== null ?
                $q.when(preparationsList()) :
                refreshPreparations();
        }

        //---------------------------------------------------------------------------------
        //----------------------------------DETAILS/CONTENT--------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name getContent
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} preparationId The preparation id
         * @param {string} version The version (step id) to load
         * @param {int} sample The wanted sample size (or null for the full preparation content).
         * @description Get preparation records at the specific 'version' step
         * @returns {promise} The GET promise
         */
        function getContent(preparationId, version, sample) {
            return PreparationRestService.getContent(preparationId, version, sample);
        }

        /**
         * @ngdoc method
         * @name getDetails
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} preparationId The preparation id
         * @description Get current preparation details
         * @returns {promise} The GET promise
         */
        function getDetails(preparationId) {
            return PreparationRestService.getDetails(preparationId);
        }

        //---------------------------------------------------------------------------------
        //-----------------------------------------LIFE------------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name create
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} datasetId The dataset id
         * @param {string} name The preparation name
         * @description Create a new preparation, and keep the current preparation id
         * @returns {promise} The POST promise
         */
        function create(datasetId, name) {
            return PreparationListService.create(datasetId, name)
                .then(consolidatePreparationsAndDatasets);
        }

        /**
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} preparation The preparation to delete
         * @description Delete a preparation
         * @returns {promise} The DELETE promise
         */
        function deletePreparation(preparation) {
            return PreparationListService.delete(preparation)
                .then(consolidatePreparationsAndDatasets);
        }

        //---------------------------------------------------------------------------------
        //----------------------------------------UPDATE-----------------------------------
        //---------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name setName
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} preparationId The preparation id
         * @param {string} name The preparation name
         * @description Create a new preparation if no preparation is loaded, update the name otherwise
         * @returns {promise} The POST promise
         */
        function setName(preparationId, name) {
            return PreparationListService.update(preparationId, name)
                .then(consolidatePreparationsAndDatasets);
        }

        /**
         * @ngdoc method
         * @name copyImplicitParameters
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} parameters The parameters to copy into
         * @param {object} originalParameters The original parameters containing the implicit parameters values
         * @description Copy the original implicit parameters values into new parameters
         */
        function copyImplicitParameters(parameters, originalParameters) {
            /*jshint camelcase: false */
            parameters.scope = originalParameters.scope;

            if('column_id' in originalParameters) {
                parameters.column_id = originalParameters.column_id;
            }

            if('column_name' in originalParameters) {
                parameters.column_name = originalParameters.column_name;
            }

            if('row_id' in originalParameters) {
                parameters.row_id = originalParameters.row_id;
            }
        }

        /**
         * @ngdoc method
         * @name appendStep
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} preparationId The preparation id
         * @param {object | array} actionParams The transformation(s) configuration {action: string, parameters: {object}}
         * @param {string} insertionStepId The insertion point step id. (Head = 'head' | falsy | head_step_id)
         * @description Append a step.
         * @returns {promise} The PUT promise
         */
        function appendStep(preparationId, actionParams, insertionStepId) {
            return PreparationRestService.appendStep(preparationId, actionParams, insertionStepId);
        }

        /**
         * @ngdoc method
         * @name updateStep
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} preparationId The preparation id
         * @param {object} step The step to update
         * @param {object} parameters The new action parameters
         * @description Update a step with new parameters
         * @returns {promise} The PUT promise
         */
        function updateStep(preparationId, step, parameters) {
            return PreparationRestService.updateStep(
                preparationId,
                step.transformation.stepId,
                {action: step.transformation.name, parameters: parameters}
            );
        }

        /**
         * @ngdoc method
         * @name removeStep
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} preparationId The preparation id
         * @param {object} stepId The step to delete
         * @param {boolean} singleMode Delete only the target step if true, all steps from target otherwise
         * @description Delete a step.
         * @returns {promise} The DELETE promise
         */
        function removeStep(preparationId, stepId, singleMode) {
            return PreparationRestService.removeStep(preparationId, stepId, singleMode);
        }

        /**
         * @ngdoc method
         * @name paramsHasChanged
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} step The step to update
         * @param {object} newParams The new action parameters
         * @description Check if the parameters has changed
         * @returns {boolean} true if parameters has changed, false otherwise
         */
        function paramsHasChanged(step, newParams) {
            return JSON.stringify(newParams) !== JSON.stringify(step.actionParameters.parameters);
        }

        //---------------------------------------------------------------------------------
        //----------------------------------------PREVIEW----------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name getPreviewDiff
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} preparationId The preparation id
         * @param {string} currentStep The current loaded step
         * @param {string} previewStep The target preview step
         * @param {string} recordsTdpId The records TDP ids to preview
         * @param {string} canceler The canceler promise
         * @description POST Preview diff between 2 unchanged steps of a recipe
         * @returns {promise} The POST promise
         */
        function getPreviewDiff(preparationId, currentStep, previewStep, recordsTdpId, canceler) {
            return PreparationRestService.getPreviewDiff(preparationId, currentStep, previewStep, recordsTdpId, canceler);
        }

        /**
         * @ngdoc method
         * @name getPreviewUpdate
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} preparationId The preparation id
         * @param {string} currentStep The current loaded step
         * @param {string} updateStep The target step to update
         * @param {string} newParams The new parameters
         * @param {string} recordsTdpId The records TDP ids to preview
         * @param {string} canceler The canceler promise
         * @description POST preview diff between 2 same actions but with 1 updated step
         * @returns {promise} The POST promise
         */
        function getPreviewUpdate(preparationId, currentStep, updateStep, newParams, recordsTdpId, canceler) {
            return PreparationRestService.getPreviewUpdate(preparationId, currentStep, updateStep, newParams, recordsTdpId, canceler);
        }

        /**
         * @ngdoc method
         * @name getPreviewAdd
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} preparationId The preparation id
         * @param {string} datasetId The dataset id
         * @param {string} action The action name to add
         * @param {string} params The action parameters
         * @param {string} recordsTdpId The records TDP ids to preview
         * @param {string} canceler The canceler promise
         * @description POST preview diff between the preparation head and a new added transformation
         * @returns {promise} The POST promise
         */
        function getPreviewAdd(preparationId, datasetId, action, params, recordsTdpId, canceler) {
            return PreparationRestService.getPreviewAdd(preparationId, datasetId, action, params, recordsTdpId, canceler);
        }
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationService', PreparationService);
})();