(function() {
    'use strict';

    function PreparationListCtrl(PreparationService) {
        var vm = this;

        var refreshPreparations = function() {
            PreparationService.getPreparations()
                .then(function(result) {
                    vm.preparations = result.data;
                });
        };

        refreshPreparations();
    }

    angular.module('data-prep.preparation-list')
        .controller('PreparationListCtrl', PreparationListCtrl);
})();