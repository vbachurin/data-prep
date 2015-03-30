(function() {
    'use strict';

    function PreparationListCtrl(PreparationListService, PlaygroundService) {
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

        PreparationListService.refreshPreparations();
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