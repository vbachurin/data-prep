(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.playground.controller:PlaygroundCtrl
     * @description Playground controller.<br/>
     * Watchers :
     * <ul>
     *     <li>Recipe length : display recipe panel on first step application</li>
     * </ul>
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.playground.service:PreviewService
     */
    function PlaygroundCtrl($state, $stateParams, PlaygroundService, PreparationService, PreviewService, RecipeService, RecipeBulletService) {
        var vm = this;
        vm.playgroundService = PlaygroundService;
        vm.previewService = PreviewService;
        vm.recipeService = RecipeService;

        /**
         * @ngdoc method
         * @name toggleRecipe
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description Toggle all the steps of the recipe
         */
        vm.toggleRecipe = RecipeBulletService.toggleRecipe;

        /**
         * @ngdoc method
         * @name toggleEditionMode
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description Toggle the edition mode flag
         */
        vm.toggleEditionMode = function toggleEditionMode(){
            vm.editionMode = !vm.editionMode;
        };

        /**
         * @ngdoc method
         * @name confirmPrepNameEdition
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description Change the preparation name
         */
        vm.confirmPrepNameEdition = function confirmPrepNameEdition(){
            vm.changeName();
            vm.toggleEditionMode();
        };

        /**
         * @ngdoc method
         * @name cancelPrepNameEdition
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description cancels the new preparation name and sets the preparation name to the original one
         */
        vm.cancelPrepNameEdition = function(){
            vm.preparationName = PlaygroundService.originalPreparationName;
            vm.toggleEditionMode();
        };

        /**
         * @ngdoc method
         * @name changeName
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description Create a preparation or update existing preparation name if it already exists
         */
        vm.changeName = function() {
            var cleanName = vm.preparationName.trim();
            if(cleanName) {
                PlaygroundService.createOrUpdatePreparation(cleanName)
                    .then(function() {
                        $state.go('nav.home.preparations', {prepid : PreparationService.currentPreparationId}, {location:'replace', inherit:false} );
                    });
            }
        };

        /**
         * @ngdoc method
         * @name close
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description Playground close callback. It change the location and refresh the preparations if needed
         */
        vm.close = function() {
            PreparationService.refreshPreparations();
            if($stateParams.prepid) {
                $state.go('nav.home.preparations', {prepid: null});
            }
            else if($stateParams.datasetid) {
                $state.go('nav.home.datasets', {datasetid: null});
            }
        };
    }

    /**
     * @ngdoc property
     * @name showPlayground
     * @propertyOf data-prep.playground.controller:PlaygroundCtrl
     * @description Flag that controls the display of the playground.
     * It is bound to {@link data-prep.services.playground.service:PlaygroundService PlaygroundService} property
     */
    Object.defineProperty(PlaygroundCtrl.prototype,
        'showPlayground', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.playgroundService.visible;
            },
            set: function(value) {
                this.playgroundService.visible = value;
            }
        });

    /**
     * @ngdoc property
     * @name metadata
     * @propertyOf data-prep.playground.controller:PlaygroundCtrl
     * @description The loaded metadata
     * It is bound to {@link data-prep.services.playground.service:PlaygroundService PlaygroundService} property
     */
    Object.defineProperty(PlaygroundCtrl.prototype,
        'metadata', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.playgroundService.currentMetadata;
            }
        });

    /**
     * @ngdoc property
     * @name preparationName
     * @propertyOf data-prep.playground.controller:PlaygroundCtrl
     * @description The preparation name
     * It is bound to {@link data-prep.services.playground.service:PlaygroundService PlaygroundService} property
     */
    Object.defineProperty(PlaygroundCtrl.prototype,
        'preparationName', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.playgroundService.preparationName;
            },
            set: function(value) {
                this.playgroundService.preparationName = value;
            }
        });

    /**
     * @ngdoc property
     * @name previewInProgress
     * @propertyOf data-prep.playground.controller:PlaygroundCtrl
     * @description Flag that defines if a preview is in progress
     * It is bound to {@link data-prep.services.dataset.service:PreviewService PreviewService} property
     */
    Object.defineProperty(PlaygroundCtrl.prototype,
        'previewInProgress', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.previewService.previewInProgress();
            }
        });

    /**
     * @ngdoc property
     * @name hasRecipe
     * @propertyOf data-prep.playground.controller:PlaygroundCtrl
     * @description Checks if there are steps in the preparation
     * It is bound to {@link data-prep.services.recipe.service:RecipeService RecipeService}.getRecipe() length
     * @type boolean
     */
    Object.defineProperty(PlaygroundCtrl.prototype,
        'hasRecipe', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.recipeService.getRecipe().length;
            }
        });

    /**
     * @ngdoc property
     * @name hasActiveStep
     * @propertyOf data-prep.playground.controller:PlaygroundCtrl
     * @description checks if there is at least 1 active step, by checking the 1st step in the recipe
     * It is bound to {@link data-prep.services.recipe.service:RecipeService RecipeService} status of the 1st step in the returned recipe array by the getRecipe() function
     * @type boolean
     */
    Object.defineProperty(PlaygroundCtrl.prototype,
        'hasActiveStep', {
            enumerable: true,
            configurable: false,
            get: function () {
                var firstStep = this.recipeService.getRecipe()[0];
                return firstStep && !firstStep.inactive;
            },
            set: function () {}
        });

    /**
     * @name showRecipe
     * @propertyOf data-prep.playground.controller:PlaygroundCtrl
     * @description The flag that pilots the recipe panel display
     * It is bound to {@link data-prep.services.playground.service:PlaygroundService PlaygroundService} property
     */
    Object.defineProperty(PlaygroundCtrl.prototype,
        'showRecipe', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.playgroundService.showRecipe;
            },
            set: function(value) {
                this.playgroundService.showRecipe = value;
            }
        });

    /**
     * @name editionMode
     * @propertyOf data-prep.playground.controller:PlaygroundCtrl
     * @description The flag that pilots the recipe panel display
     * It is bound to {@link data-prep.services.playground.service:PlaygroundService PlaygroundService} property
     */
    Object.defineProperty(PlaygroundCtrl.prototype,
        'editionMode', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.playgroundService.preparationNameEditionMode;
            },
            set: function(value) {
                this.playgroundService.preparationNameEditionMode = value;
            }
        });

    angular.module('data-prep.playground')
        .controller('PlaygroundCtrl', PlaygroundCtrl);
})();

