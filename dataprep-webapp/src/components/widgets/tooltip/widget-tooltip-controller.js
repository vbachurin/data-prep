(function () {
    'use strict';

    function TalendTooltipCtrl($scope) {
        var vm = this;
        var blocked = false;
        vm.innerState = vm.requestedState;

        /**
         * Block visibility change
         */
        vm.blockState = function() {
            blocked = true;
        };

        /**
         * Unblock visibility change and update state to the last requested state
         */
        vm.unblockState = function() {
            blocked = false;
            vm.innerState = vm.requestedState;
        };

        /**
         * Update tooltip position
         * @param horizontalPosition - {left: (number | string); right: (number | string)}
         * @param verticalPosition - {top: (number | string); bottom: (number | string)}
         */
        vm.updatePosition = function(horizontalPosition, verticalPosition) {
            vm.style = {
                left: horizontalPosition.left,
                right: horizontalPosition.right,
                top: verticalPosition.top,
                bottom: verticalPosition.bottom
            };
        };

        /**
         * Update visibility state if not blocked when requested state change
         */
        $scope.$watch(
            function() {
                return vm.requestedState;
            },
            function(newValue) {
                if(!blocked) {
                    vm.innerState = newValue;
                }
            }
        );
    }

    angular.module('talend.widget')
        .controller('TalendTooltipCtrl', TalendTooltipCtrl);
})();

