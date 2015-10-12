(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name talend.widget.controller:BadgeCtrl
     * @description Badge controller. The directive initialize :
     * <ul>
     *     <li>ctrl.onChange and ctrl.onClose functions</li>
     *     <li>ctrl.obj where ctrl.obj.value define an editable value</li>
     * </ul>
     * Watchers
     * <ul>
     *     <li>bind obj.value to the input value</li>
     * </ul>
     */
    function BadgeCtrl($scope, $translate) {
        var vm = this;
        vm.value = '';

        switch (vm.type){
            case 'contains':
                vm.sign = ' ≅ ';
                break;
            case 'exact':
                vm.sign = ' = ';
                break;
            case 'inside_range':
                vm.sign = ' in ';
                break;
            default:
                vm.sign = $translate.instant('COLON');
        }

        /**
         * @ngdoc method
         * @name manageChange
         * @methodOf talend.widget.controller:BadgeCtrl
         * @description Trigger the change callback
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
         * @ngdoc method
         * @name close
         * @methodOf talend.widget.controller:BadgeCtrl
         * @description Trigger the close callback
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