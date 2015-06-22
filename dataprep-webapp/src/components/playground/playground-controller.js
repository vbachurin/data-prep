(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.playground.controller:PlaygroundCtrl
     * @description Playground controller.
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.preparation.service:PreparationService
     * @requires data-prep.services.playground.service:PreviewService
     */
    function PlaygroundCtrl($state, $stateParams, PlaygroundService, PreparationService, PreviewService, RecipeService, RecipeBulletService) {
        var vm = this;
        vm.playgroundService = PlaygroundService;
        vm.previewService = PreviewService;
        vm.editionMode = true;
        vm.recipeService = RecipeService;

        /**
         * @ngdoc method
         * @name activateDeactivateAllsteps
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description activates or deactivates all the steps of the recipe
         */
        vm.activateDeactivateAllsteps = function(){
            RecipeBulletService.toggleAllSteps();
        };

        /**
         * @ngdoc method
         * @name editionModeFn
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description just changes the editionMode variable
         */
        vm.editionModeFn = function(){
            vm.editionMode = !vm.editionMode;
        };

        /**
         * @ngdoc method
         * @name confirmNewPrepName
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description confirms the new preparation name
         */
        vm.confirmNewPrepName = function(){
            console.log('avant changeName -------');
            vm.changeName();
            vm.editionModeFn();
        };

        /**
         * @ngdoc method
         * @name cancelPrepNameEdition
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description cancels the new preparation name and sets the preparation name to the original one
         */
        vm.cancelPrepNameEdition = function(){
            vm.preparationName = PlaygroundService.originalPreparationName;
            vm.editionModeFn();
        };

        /**
         * @ngdoc method
         * @name changeName
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description Create a preparation or update existing preparation name if it already exists
         */
        vm.changeName = function() {
            var cleanName = vm.preparationName.trim();
            console.log(cleanName,'-----------------------');
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
     * @name isThereRecipe
     * @propertyOf data-prep.playground.controller:PlaygroundCtrl
     * @description checks if there are steps in the preparation
     * It is bound to {@link data-prep.services.recipe.service:RecipeService RecipeService} property length of the returned recipe by getRecipe() function
     * @type boolean
     */
    Object.defineProperty(PlaygroundCtrl.prototype,
        'isThereRecipe', {
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
                if(firstStep){
                    return !firstStep.inactive?true:false;
                }else{
                    return null;
                }

            },
            set:function(){}
        });

    angular.module('data-prep.playground')
        .controller('PlaygroundCtrl', PlaygroundCtrl);
})();

