(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.preparation.service:PreparationService
     * @description Preparation list service. his service manage the operations that touches the preparations
     * @requires data-prep.services.preparation.service:PreparationListService
     * @requires data-prep.services.preparation.service:PreparationRestService
     * @requires data-prep.services.dataset.service:DatasetListService
     */
    function PreparationService($q, PreparationListService, PreparationRestService, DatasetListService) {
        var self = this;

        /**
         * @ngdoc property
         * @name currentPreparationId
         * @propertyOf data-prep.services.preparation.service:PreparationService
         * @description The currently loaded preparation
         */
        self.currentPreparationId = null;

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
        self.preparationsList = function() {
            return PreparationListService.preparations;
        };

        /**
         * @ngdoc method
         * @name refreshPreparationsMetadata
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description [PRIVATE] Refresh the default preparation within each dataset
         */
        var consolidatePreparationsAndDatasets = function(response) {
            DatasetListService.refreshDefaultPreparation(self.preparationsList())
                .then(PreparationListService.refreshMetadataInfos);
            return response;
        };

        /**
         * @ngdoc method
         * @name refreshPreparations
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description Refresh the preparations list
         * @returns {promise} The process promise
         */
        self.refreshPreparations = function() {
            return PreparationListService.refreshPreparations()
                .then(consolidatePreparationsAndDatasets);
        };

        /**
         * @ngdoc method
         * @name getPreparations
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description Return preparation promise that resolve current preparation list if not empty, or call GET service
         * @returns {promise} The process promise
         */
        self.getPreparations = function() {
            return self.preparationsList() !== null ?
                $q.when(self.preparationsList()) :
                self.refreshPreparations();
        };

        //---------------------------------------------------------------------------------
        //----------------------------------DETAILS/CONTENT--------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name getContent
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} version The version (step id) to load
         * @description Get preparation records at the specific 'version' step
         * @returns {promise} The GET promise
         */
        self.getContent = function(version) {
            return PreparationRestService.getContent(self.currentPreparationId, version);
        };

        /**
         * @ngdoc method
         * @name getDetails
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description Get current preparation details
         * @returns {promise} The GET promise
         */
        self.getDetails = function() {
            return PreparationRestService.getDetails(self.currentPreparationId);
        };

        //---------------------------------------------------------------------------------
        //-----------------------------------------LIFE------------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name create
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} metadata The dataset metadata
         * @param {string} name The preparation name
         * @description Create a new preparation, and keep the current preparation id
         * @returns {promise} The POST promise
         */
        self.create = function(metadata, name) {
            return PreparationListService.create(metadata.id, name)
                .then(consolidatePreparationsAndDatasets)
                .then(function(resp) {
                    self.currentPreparationId = resp.data;
                });
        };

        /**
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} preparation The preparation to delete
         * @description Delete a preparation
         * @returns {promise} The DELETE promise
         */
        self.delete = function(preparation) {
            return PreparationListService.delete(preparation)
                .then(consolidatePreparationsAndDatasets);
        };

        //---------------------------------------------------------------------------------
        //----------------------------------------UPDATE-----------------------------------
        //---------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name setName
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} metadata The dataset metadata
         * @param {string} name The preparation name
         * @description Create a new preparation if no preparation is loaded, update the name otherwise
         * @returns {promise} The POST promise
         */
        self.setName = function(metadata, name) {
            if(self.currentPreparationId) {
                return PreparationListService.update(self.currentPreparationId, name)
                    .then(function(result) {
                        self.currentPreparationId = result.data;
                    })
                    .then(consolidatePreparationsAndDatasets);
            }
            else {
                return self.create(metadata, name);
            }
        };

        /**
         * @ngdoc method
         * @name updateStep
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} step The step to update
         * @param {object} parameters The new action parameters
         * @description Update a step with new parameters
         * @returns {promise} The PUT promise
         */
        self.updateStep = function(step, parameters) {
            parameters = parameters || {};
            self.insertColumnInfo(parameters, step.column);
            return PreparationListService.updateStep(self.currentPreparationId, step, parameters);
        };


        /**
         * @ngdoc method
         * @name insertColumnInfo
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} parameters The parameters to update the column from
         * @param {object} column The update source.
         */
        self.insertColumnInfo = function(parameters, column) {
            /*jshint camelcase: false */
            parameters.column_id = column.id;
            parameters.column_name = column.name;
        };

        /**
         * @ngdoc method
         * @name appendStep
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} metadata The target metadata
         * @param {object} action The action name
         * @param {object} column The target column
         * @param {object} parameters The new action parameters
         * @description Append a step. If the preparation does not exists, it is created
         * @returns {promise} The PUT promise
         */
        self.appendStep = function(metadata, action, column, parameters) {
            parameters = parameters || {};
            self.insertColumnInfo(parameters, column);
            var promise = self.currentPreparationId ? $q.when(self.currentPreparationId) : self.create(metadata, 'New preparation');

            return promise.then(function() {
                return PreparationListService.appendStep(self.currentPreparationId, action, parameters);
            });
        };

        /**
         * @ngdoc method
         * @name paramsHasChanged
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {object} step The step to update
         * @param {object} newParams The new action parameters
         * @description Check if the parameters has changed
         * @returns {boolean} true if parameters has changed, false otherwise
         */
        self.paramsHasChanged = function(step, newParams) {
            newParams = newParams || {};
            self.insertColumnInfo(newParams, step.column);
            return JSON.stringify(newParams) !== JSON.stringify(step.actionParameters.parameters);
        };

        //---------------------------------------------------------------------------------
        //----------------------------------------PREVIEW----------------------------------
        //---------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name getPreviewDiff
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} currentStep The current loaded step
         * @param {string} previewStep The target preview step
         * @param {string} recordsTdpId The records TDP ids to preview
         * @param {string} canceler The canceler promise
         * @description POST Preview diff between 2 unchanged steps of a recipe
         * @returns {promise} The POST promise
         */
        self.getPreviewDiff = function(currentStep, previewStep, recordsTdpId, canceler) {
            return PreparationRestService.getPreviewDiff(self.currentPreparationId, currentStep, previewStep, recordsTdpId, canceler);
        };

        /**
         * @ngdoc method
         * @name getPreviewUpdate
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @param {string} currentStep The current loaded step
         * @param {string} updateStep The target step to update
         * @param {string} newParams The new parameters
         * @param {string} recordsTdpId The records TDP ids to preview
         * @param {string} canceler The canceler promise
         * @description POST preview diff between 2 same actions but with 1 updated step
         * @returns {promise} The POST promise
         */
        this.getPreviewUpdate = function(currentStep, updateStep, newParams, recordsTdpId, canceler) {
            return PreparationRestService.getPreviewUpdate(self.currentPreparationId, currentStep, updateStep, newParams, recordsTdpId, canceler);
        };
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationService', PreparationService);
})();