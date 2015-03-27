(function () {
    'use strict';

    function PlaygroundCtrl(PlaygroundService) {
        var vm = this;
        vm.playgroundService = PlaygroundService;

        vm.changeName = function() {
            var cleanName = vm.preparationName.trim();
            if(cleanName) {
                PlaygroundService.createOrUpdatePreparation(cleanName);
            }
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

