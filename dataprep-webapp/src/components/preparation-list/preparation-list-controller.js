(function() {
    'use strict';

    function PreparationListCtrl(PreparationService, PreparationListService, PlaygroundService, TalendConfirmService, MessageService) {
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
                    MessageService.success('PREPARATION_REMOVE_SUCCESS_TITLE', 'PREPARATION_REMOVE_SUCCESS', {preparation: preparation.name});
                    PreparationListService.refreshPreparations();
                });
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