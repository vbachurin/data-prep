(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.preparation.service:PreparationListService
     * @description Preparation list service. This service holds the preparations list and adapt them for the application.
      It uses PreparationRestService to get the preparations, and DatasetListService to get the datasets
     * @requires data-prep.services.preparation.service:PreparationRestService
     * @requires data-prep.services.dataset.service:DatasetListService
     */
    function PreparationListService($q, PreparationRestService, DatasetListService) {
        var self = this;

        /**
         * @ngdoc property
         * @name preparations
         * @propertyOf data-prep.services.preparation.service:PreparationListService
         * @description the preparations list
         */
        self.preparations = null;

        /**
         * @ngdoc method
         * @name adaptMetadataInfos
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {object[]} preparations - the preparations to adapt
         * @param {object[]} datasets - the datasets to inject
         * @description [PRIVATE] Inject the corresponding dataset to every preparation
         */
        var adaptMetadataInfos = function(preparations, datasets) {
            _.forEach(preparations, function(prep) {
                var correspondingDataset = _.find(datasets, function(dataset) {
                    return dataset.id === prep.dataSetId;
                });
                prep.dataset = correspondingDataset;
            });
        };

        /**
         * @ngdoc method
         * @name setDefaultPreparation
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {object[]} AllPreparations - the preparations to use
         * @param {object[]} datasets - the datasets to update
         * @description [PRIVATE] Set the default preparation for the given dataset if any
         */
        var setDefaultPreparation = function(AllPreparations, datasets) {

            // group preparation per dataset
            var datasetPreps = _.groupBy(AllPreparations, function(preparation){
                return preparation.dataSetId;
            });

            // reset default preparation for all datasets
            _.forEach(datasets, function(dataset){
                var preparations = datasetPreps[dataset.id];
                dataset.defaultPreparation = preparations && preparations.length === 1 ?  preparations[0] : null;
            });
        };

        /**
         * @ngdoc method
         * @name refreshPreparations
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @description Refresh the preparations list
         * @returns {Promise} - the process promise
         */
        self.refreshPreparations = function() {
            return $q.all([PreparationRestService.getPreparations(), DatasetListService.getDatasetsPromise()])
                .then(function(results) {
                    var preparationResult = results[0];
                    var datasets = results[1];
                    adaptMetadataInfos(preparationResult.data, datasets);
                    setDefaultPreparation(preparationResult.data, datasets);
                    self.preparations = preparationResult.data;

                    return self.preparations;
                });
        };

        /**
         * @ngdoc method
         * @name getPreparationsPromise
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @description Return preparation promise that resolve current preparation list if not empty, or call GET service
         * @returns {Promise} - the process promise
         */
        self.getPreparationsPromise = function() {
            if(self.preparations === null) {
                return self.refreshPreparations();
            }
            else {
                return $q.when(self.preparations);
            }
        };

        /**
         * @ngdoc method
         * @name getContent
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {string} preparationId The preparation id to load
         * @param {string} version The version (step id) to load
         * @description Get preparation records at the specific 'version' step
         * @returns {promise} The GET promise
         */
        this.getContent = PreparationRestService.getContent;

        /**
         * @ngdoc method
         * @name getDetails
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {string} preparationId The preparation id to load
         * @description Get current preparation details
         * @returns {promise} The GET promise
         */
        this.getDetails = PreparationRestService.getDetails;

        /**
         * @ngdoc method
         * @name create
         * @methodOf data-prep.services.preparation.service:PreparationListService
         * @param {string} datasetId The dataset id
         * @param {string} name The preparation name
         * @description Create a new preparation
         * @returns {promise} The POST promise
         */
        this.create = PreparationRestService.create;

        /**
         * @ngdoc method
         * @name update
         * @methodOf data-prep.services.preparation.service:PreparationRestService
         * @param {string} preparationId The preparation id
         * @param {string} name The new preparation name
         * @description Update the current preparation name
         * @returns {promise} The PUT promise
         */
        this.update = PreparationRestService.update;

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
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationListService', PreparationListService);
})();