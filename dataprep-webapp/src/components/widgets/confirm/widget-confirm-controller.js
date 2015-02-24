(function() {
    'use strict';

    function TalendConfirmCtrl($scope, TalendConfirmService) {
        var vm = this;
        vm.modalState = true;
        vm.buttonClicked = false;

        /**
         * Watch the modal state to reject confirm promise on modal dismiss
         */
        $scope.$watch(
            function() {
                return vm.modalState;
            },
            function(newValue) {
                if(! newValue && ! vm.buttonClicked) {
                    TalendConfirmService.reject('dismiss');
                }
            }
        );

        /**
         * Valid action : resolve confirm promise
         */
        vm.valid = function() {
            vm.buttonClicked = true;
            TalendConfirmService.resolve();
        };

        /**
         * Cancel action : reject confirm promise
         */
        vm.cancel = function() {
            vm.buttonClicked = true;
            TalendConfirmService.reject(null);
        };
    }

    angular.module('talend.widget')
        .controller('TalendConfirmCtrl', TalendConfirmCtrl);
})();