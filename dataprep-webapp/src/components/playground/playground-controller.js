(function () {
    'use strict';

    function PlaygroundCtrl(PlaygroundService, PreparationListService) {
        var vm = this;
        vm.playgroundService = PlaygroundService;

        /**
         * Create a preparation or update existing preparation name
         */
        vm.changeName = function() {
            var cleanName = vm.preparationName.trim();
            if(cleanName) {
                PlaygroundService.createOrUpdatePreparation(cleanName);
            }
        };

        /**
         * Refresh preparation lists (called on playground close)
         */
        vm.refreshPreparations = function() {
            PreparationListService.refreshPreparations();
        };
    }

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

    Object.defineProperty(PlaygroundCtrl.prototype,
        'metadata', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.playgroundService.currentMetadata;
            }
        });

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

    angular.module('data-prep.playground')
        .controller('PlaygroundCtrl', PlaygroundCtrl);
})();

