(function() {
    'use strict';

    function PreparationListCtrl($stateParams, PreparationListService, PlaygroundService, PreparationService, TalendConfirmService, MessageService) {
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
         * Delete a preparation
         * @param preparation - the preparation to delete
         */
        vm.delete = function(preparation) {
            TalendConfirmService.confirm({disableEnter: true}, ['DELETE_PERMANENTLY', 'NO_UNDONE_CONFIRM'], {type:'preparation', name: preparation.name})
                .then(function() {
                    return PreparationService.delete(preparation);
                })
                .then(function() {
                    MessageService.success('REMOVE_SUCCESS_TITLE', 'REMOVE_SUCCESS', {type:'preparation', name: preparation.name});
                    PreparationListService.refreshPreparations();
                });
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