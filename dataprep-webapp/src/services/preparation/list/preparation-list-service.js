(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.preparation.service:PreparationListService
     * @description Preparation list service. This service holds the preparations list and adapt them for the application.<br/>
     * <b style="color: red;">WARNING : do NOT use this service directly.
     * {@link data-prep.services.preparation.service:PreparationService PreparationService} must be the only entry point for preparations</b>
     * @requires data-prep.services.preparation.service:PreparationRestService
     */
    function PreparationListService($q, PreparationRestService) {
        var self = this;
        var preparationsPromise;

        /**
         * @ngdoc property
         * @name preparations
         * @propertyOf data-prep.services.preparation.service:PreparationListService
         * @description the preparations list
         */
        self.preparations = null;

        /**
         * @ngdoc method
         * @name refreshPreparations
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @description Refresh the preparations list
         * @returns {promise} The process promise
         */
        self.refreshPreparations = function() {
            if(!preparationsPromise) {
                preparationsPromise = PreparationRestService.getPreparations()
                    .then(function (response) {
                        preparationsPromise = null;
                        self.preparations = response.data;

                        return self.preparations;
                    });
            }
            return preparationsPromise;
        };

        /**
         * @ngdoc method
         * @name getPreparationsPromise
         * @methodOf data-prep.services.preparation.service:PreparationService
         * @description Return preparation promise that resolve current preparation list if not empty, or call GET service
         * @returns {promise} The process promise
         */
        self.getPreparationsPromise = function() {
            return self.preparations === null ? self.refreshPreparations() : $q.when(self.preparations);
        };

        /**
         * @ngdoc method
         * @name create
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {string} datasetId The dataset id
         * @param {string} name The preparation name
         * @description Create a new preparation
         * @returns {promise} The POST promise
         */
        this.create = function(datasetId, name) {
            return PreparationRestService.create(datasetId, name)
                .then(function(response) {
                                    self.refreshPreparations();
                                    return response;
                                });
        };

        /**
         * @ngdoc method
         * @name update
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preparation id
         * @param {string} name The new preparation name
         * @description Update the current preparation name
         * @returns {promise} The PUT promise
         */
        this.update = function(preparationId, name) {
            return PreparationRestService.update(preparationId, name)
                .then(self.refreshPreparations);
        };

        /**
         * @ngdoc method
         * @name delete
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {object} preparation The preparation to delete
         * @description Delete a preparation from backend and from its internal list
         * @returns {promise} The DELETE promise
         */
        self.delete = function(preparation) {
            return PreparationRestService.delete(preparation.id)
                .then(function() {
                    var index = self.preparations.indexOf(preparation);
                    self.preparations.splice(index, 1);
                });
        };

        /**
         * @ngdoc method
         * @name updateStep
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {object} preparationId The preparation id to update
         * @param {object} step The step to update
         * @param {object} parameters The new action parameters
         * @description Update a step with new parameters
         * @returns {promise} The PUT promise
         */
        this.updateStep = function(preparationId, step, parameters) {
            return PreparationRestService.updateStep(preparationId, step.transformation.stepId, step.transformation.name, parameters);
        };

        /**
         * @ngdoc method
         * @name appendStep
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {object} preparationId The preparation id
         * @param {object} action The action name
         * @param {object} parameters The new action parameters
         * @description Append a step to the preparation
         * @returns {promise} The POST promise
         */
        this.appendStep = PreparationRestService.appendStep;

        /**
         * @ngdoc method
         * @name refreshMetadataInfos
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {object[]} datasets The datasets to inject
         * @description [PRIVATE] Inject the corresponding dataset to every preparation
         * @returns {promise} The process promise
         */
        self.refreshMetadataInfos = function(datasets) {
            return self.getPreparationsPromise()
                .then(function(preparations) {
                    _.forEach(preparations, function(prep) {
                        var correspondingDataset = _.find(datasets, function(dataset) {
                            return dataset.id === prep.dataSetId;
                        });
                        prep.dataset = correspondingDataset;
                    });

                    return preparations;
                });
        };
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationListService', PreparationListService);
})();