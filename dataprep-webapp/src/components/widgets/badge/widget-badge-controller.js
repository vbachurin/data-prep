(function () {
    'use strict';

    /**
     * Badge controller. The directive initialize :
     * - ctrl.onChange and ctrl.onClose functions
     * - ctrl.obj where ctrl.obj.value define an editable value
     *
     * @param $scope
     */
    function BadgeCtrl($scope) {
        var vm = this;
        vm.value = '';

        /**
         * Update filter
         */
        vm.manageChange = function () {
            if (vm.obj.value !== vm.value) {
                vm.onChange({
                    obj: vm.obj,
                    newValue: vm.value
                });
            }
        };

        /**
         * Close the badge
         */
        vm.close = function () {
            vm.onClose({obj: vm.obj});
        };

        //Bind editable text to input value
        if (vm.obj) {
            $scope.$watch(
                function () {
                    return vm.obj.value;
                },
                function () {
                    vm.value = vm.obj.value;
                });
        }
    }

    angular.module('talend.widget')
        .controller('BadgeCtrl', BadgeCtrl);
})();