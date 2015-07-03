(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.playground.service:PlaygroundService
     * @description Playground service. This service provides the entry point to load properly the playground
     * @requires data-prep.services.dataset.service:DatasetService
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.filter.service:FilterService
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.transformation.service:TransformationCacheService
     * @requires data-prep.services.transformation.service:ColumnSuggestionService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.utils.service:MessageService
     * @requires data-prep.services.statistics:StatisticsService
     */
    function PlaygroundService($rootScope, $q, DatasetService, DatagridService, FilterService, RecipeService,
                               TransformationCacheService, ColumnSuggestionService, PreparationService, MessageService, StatisticsService) {
        var self = this;
        self.toggleHappened = null;

        /**
         * @ngdoc property
         * @name visible
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description the visibility control
         */
        self.visible = false;

        /**
         * @ngdoc property
         * @name currentMetadata
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description the loaded metadata
         */
        self.currentMetadata = null;

        /**
         * @ngdoc property
         * @name originalPreparationName
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description the original preparation name - used to check if the name has changed
         */
        self.originalPreparationName = '';

        /**
         * @ngdoc property
         * @name preparationName
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description the current preparation
         */
        self.preparationName = '';

        /**
         * @ngdoc property
         * @name showRecipe
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description Flag that pilot the recipe panel display
         */
        self.showRecipe = false;

        /**
         * @ngdoc property
         * @name preparationNameEditionMode
         * @propertyOf data-prep.services.playground.service:PlaygroundService
         * @description Flag that the name edition mode.
         * The edition mode is active when user open an existing preparation, and inactive for a new preparation
         */
        self.preparationNameEditionMode = true;

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------VISIBILITY--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name show
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @description Display the playground
         */
        self.show = function show() {
            self.visible = true;
        };

        /**
         * @ngdoc method
         * @name hide
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @description Hide the playground
         */
        self.hide = function hide() {
            self.visible = false;
        };

        //------------------------------------------------------------------------------------------------------
        //-------------------------------------------------INIT/LOAD--------------------------------------------
        //------------------------------------------------------------------------------------------------------
        var reset = function reset(dataset, data) {
            self.currentMetadata = dataset;

            FilterService.removeAllFilters();
            RecipeService.refresh();
            StatisticsService.resetCharts();
            DatagridService.setDataset(dataset, data);
            TransformationCacheService.invalidateCache();
            ColumnSuggestionService.reset();
        };

        var setName = function setName(name) {
            self.preparationName = name;
            self.originalPreparationName = name;
        };

        /**
         * @ngdoc method
         * @name initPlayground
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} dataset The dataset to load
         * @description Initiate a new preparation from dataset.
         - If there is no preparation yet and the dataset to load is still the last loaded, the playground is not changed.
         - Otherwise, the playground is reset with the wanted dataset
         * @returns {Promise} The process promise
         */
        self.initPlayground = function initPlayground(dataset) {
            if(!self.currentMetadata || PreparationService.currentPreparationId || dataset.id !== self.currentMetadata.id) {
                PreparationService.currentPreparationId = null;

                return DatasetService.getContent(dataset.id, false)
                    .then(function(data) {
                        //TODO : temporary fix because asked to.
                        //TODO : when error status during import and get dataset content is managed by backend,
                        //TODO : remove this controle and the 'data-prep.services.utils'/MessageService dependency
                        if(!data || !data.records) {
                            MessageService.error('INVALID_DATASET_TITLE', 'INVALID_DATASET');
                            throw Error('Empty data');
                        }

                        setName('');
                        reset(dataset, data);
                        self.showRecipe = false;
                        self.preparationNameEditionMode = true;
                    });
            }
            else {
                return $q.when(true);
            }
        };

        /**
         * @ngdoc method
         * @name load
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} preparation - the preparation to load
         * @description Load an existing preparation in the playground :
         <ul>
            <li>set name</li>
            <li>set current preparation before any preparation request</li>
            <li>load grid with 'head' version content</li>
            <li>reinit recipe panel with preparation steps</li>
         </ul>
         * @returns {Promise} - the process promise
         */
        self.load = function load(preparation) {
            if(PreparationService.currentPreparationId !== preparation.id) {
                // Update current preparation id before preparation operations
                PreparationService.currentPreparationId = preparation.id;

                $rootScope.$emit('talend.loading.start');
                return PreparationService.getContent('head')
                    .then(function(response) {
                        setName(preparation.name);
                        reset(preparation.dataset ? preparation.dataset : {id: preparation.dataSetId}, response.data);
                        self.showRecipe = true;
                        self.preparationNameEditionMode = false;
                    })
                    .finally(function() {
                        $rootScope.$emit('talend.loading.stop');
                    });
            }
            else {
                return $q.when(true);
            }
        };

        /**
         * @ngdoc method
         * @name loadStep
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {object} step - the preparation step to load
         * @description Load a specific step content in the current preparation, and update the recipe
         * @returns {promise} - the process promise
         */
        self.loadStep = function loadStep(step) {
            //step already loaded
            if(RecipeService.getActiveThresholdStep() === step) {
                return;
            }

            $rootScope.$emit('talend.loading.start');
            return PreparationService.getContent(step.transformation.stepId)
                .then(function(response) {
                    DatagridService.setDataset(self.currentMetadata, response.data);
                    RecipeService.disableStepsAfter(step);
                })
                .finally(function() {
                    $rootScope.$emit('talend.loading.stop');
                });
        };

        //------------------------------------------------------------------------------------------------------
        //------------------------------------------------PREPARATION-------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name createOrUpdatePreparation
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} name The preparation name to create or update
         * @description Create a new preparation or change its name if it already exists
         * @returns {Promise} The process promise
         */
        self.createOrUpdatePreparation = function createOrUpdatePreparation(name) {
            if(self.originalPreparationName !== name) {
                return PreparationService.setName(self.currentMetadata, name)
                    .then(function() {
                        self.originalPreparationName = name;
                        self.preparationName = name;
                    });
            }
            else {
                return $q.reject();
            }
        };

        /**
         * @ngdoc method
         * @name transform
         * @methodOf data-prep.services.playground.service:PlaygroundService
         * @param {string} action The action name
         * @param {object} column The columns metadata
         * @param {object} params The transformation params
         * @description Perform a transformation on the column in the current preparation, refresh the recipe and the
         * data. If there is no preparation yet, PreparationService create it.
         */
        self.appendStep = function appendStep(action, column, params) {
            $rootScope.$emit('talend.loading.start');
            return PreparationService.appendStep(self.currentMetadata, action, column, params)
                .then(function() {
                    return PreparationService.getContent('head');
                })
                .then(function(response) {
                    DatagridService.updateData(response.data);
                    return RecipeService.refresh();
                })
                .then(function() {
                    if(RecipeService.getRecipe().length === 1) { //first step append
                        self.showRecipe = true;
                    }
                })
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        };
    }

    angular.module('data-prep.services.playground')
        .service('PlaygroundService', PlaygroundService);
})();