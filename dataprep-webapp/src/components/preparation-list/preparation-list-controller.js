(function() {
    'use strict';

    function PreparationListCtrl(PreparationService, PlaygroundService) {
        var vm = this;

        /**
         * Refresh preparation list
         */
        var refreshPreparations = function() {
            PreparationService.getPreparations()
                .then(function(result) {
                    vm.preparations = result.data;
                });
        };

        /**
         * Load a preparation in the playground
         * @param preparation - the preparation to load
         */
        vm.load = function(preparation) {
            PlaygroundService
                .load(preparation)
                .then(PlaygroundService.show);
        };

        refreshPreparations();
    }

    angular.module('data-prep.preparation-list')
        .controller('PreparationListCtrl', PreparationListCtrl);
})();