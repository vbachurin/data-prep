(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name talend.widget.controller:TalendTooltipCtrl
     * @description Tooltip directive
     */
    function TalendTooltipCtrl($scope) {
        var vm = this;
        var blocked = false;
        vm.innerState = vm.requestedState;

        /**
         * @ngdoc method
         * @name blockState
         * @methodOf talend.widget.controller:TalendTooltipCtrl
         * @description Block the display
         */
        vm.blockState = function() {
            blocked = true;
        };

        /**
         * @ngdoc method
         * @name unblockState
         * @methodOf talend.widget.controller:TalendTooltipCtrl
         * @description Unblock the display
         */
        vm.unblockState = function() {
            blocked = false;
            vm.innerState = vm.requestedState;
        };

        /**
         * @ngdoc method
         * @name updatePosition
         * @methodOf talend.widget.controller:TalendTooltipCtrl
         * @param {object} horizontalPosition - {left: (number | string); right: (number | string)}
         * @param {object} verticalPosition - {top: (number | string); bottom: (number | string)}
         * @description Change the position of the tooltip
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

