(function() {
    'use strict';

    function TalendConfirmCtrl($scope, TalendConfirmService) {
        var vm = this;
        vm.modalState = true;
        vm.buttonClicked = false;

        $scope.$watch(function() {
            return vm.modalState;
        }, function(newValue) {
            if(! newValue && ! vm.buttonClicked) {
                TalendConfirmService.reject('dismiss');
            }
        });

        vm.valid = function() {
            vm.buttonClicked = true;
            TalendConfirmService.resolve();
        };

        vm.cancel = function() {
            vm.buttonClicked = true;
            TalendConfirmService.reject();
        };
    }

    angular.module('talend.widget')
        .controller('TalendConfirmCtrl', TalendConfirmCtrl);
})();