(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.transformation-menu.controller:TransformMenuCtrl
     * @description Transformation menu item controller.
     * @requires data-prep.services.playground.service:PlaygroundService
     */
    function TransformMenuCtrl(PlaygroundService) {
        var vm = this;

        /**
         * @ngdoc method
         * @name select
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @description Menu selected. 3 choices :
         * <ul>
         *     <li>divider : no action</li>
         *     <li>no parameter and no choice is required : transformation call</li>
         *     <li>parameter or choice required : show modal</li>
         * </ul>
         */
        vm.select = function () {
            if (vm.menu.isDivider) {
                return;
            }

            if (vm.menu.parameters || vm.menu.items) {
                vm.showModal = true;
            }
            else {
                vm.transform();
            }
        };

        /**
         * @ngdoc method
         * @name transform
         * @methodOf data-prep.transformation-menu.controller:TransformMenuCtrl
         * @param {object} params The transformation params
         * @description Perform a transformation on the column
         */
        vm.transform = function (params) {
            PlaygroundService.appendStep(vm.menu.name, vm.column, params)
                .then(function() {
                    vm.showModal = false;
                });
        };
    }

    angular.module('data-prep.transformation-menu')
        .controller('TransformMenuCtrl', TransformMenuCtrl);
})();