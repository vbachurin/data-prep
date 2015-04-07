(function() {
    'use strict';

    function PreparationListCtrl($stateParams, PreparationListService, PlaygroundService) {
        var vm = this;
        vm.preparationListService = PreparationListService;

        /**
         * Load a preparation in the playground
         * @param preparation - the preparation to load
         */
        vm.load = function(preparation) {
            PlaygroundService
                .load(preparation)
                .then(PlaygroundService.show);
        };

        /**
         * Load playground with provided preparation id, if present in route param
         * @param preparations - list of all user's preparation
         */
        var loadUrlSelectedPreparation = function(preparations) {
            if($stateParams.prepid) {
                var selectedPrep = _.find(preparations, function(preparation) {
                    return preparation.id === $stateParams.prepid;
                });
                if(selectedPrep) {
                    vm.load(selectedPrep);
                }
            }
        };

        /**
         * Load preparations list if needed, and load playground if route has preparation id
         */
        PreparationListService.getPreparationsPromise()
            .then(loadUrlSelectedPreparation);
    }

    Object.defineProperty(PreparationListCtrl.prototype,
        'preparations', {
            enumerable: true,
            configurable: false,
            get: function () {
                return this.preparationListService.preparations;
            }
        });

    angular.module('data-prep.preparation-list')
        .controller('PreparationListCtrl', PreparationListCtrl);
})();