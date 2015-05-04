(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.playground.controller:PlaygroundCtrl
     * @description Playground controller.
     * @requires data-prep.services.playground.service:PlaygroundService
     * @requires data-prep.services.preparation.service:PreparationListService
     * @requires data-prep.services.preparation.service:PreviewService
     */
    function PlaygroundCtrl($state, $stateParams, PlaygroundService, PreparationListService, PreviewService) {
        var vm = this;
        vm.playgroundService = PlaygroundService;
        vm.previewService = PreviewService;

        /**
         * @ngdoc method
         * @name changeName
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description Create a preparation or update existing preparation name if it already exists
         */
        vm.changeName = function() {
            var cleanName = vm.preparationName.trim();
            if(cleanName) {
                PlaygroundService.createOrUpdatePreparation(cleanName);
            }
        };

        /**
         * @ngdoc method
         * @name close
         * @methodOf data-prep.playground.controller:PlaygroundCtrl
         * @description Playground close callback. It change the location and refresh the preparations if needed
         */
        vm.close = function() {
            PreparationListService.refreshPreparations();
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

    angular.module('data-prep.playground')
        .controller('PlaygroundCtrl', PlaygroundCtrl);
})();

