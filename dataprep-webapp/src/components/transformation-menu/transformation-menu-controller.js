(function () {
    'use strict';

    function TransformMenuCtrl($rootScope, DatasetGridService, PreparationService, RecipeService) {
        var vm = this;

        /**
         * Menu selected. 3 choices :
         * 1 - divider : no action
         * 2 - no parameter and no choice is required : transformation call
         * 3 - parameter or choice required : show modal
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
         * Perform a transformation on the column
         */
        vm.transform = function (params) {
            $rootScope.$emit('talend.loading.start');

            params = params || {};
            /*jshint camelcase: false */
            params.column_name = vm.column.id;

            PreparationService.append(vm.metadata.id, vm.menu.name, params)
                .then(function() {
                    return PreparationService.getContent('head');
                })
                .then(function(response) {
                    DatasetGridService.updateRecords(response.data.records);
                    RecipeService.refresh();
                    vm.showModal = false;
                })
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        };
    }

    angular.module('data-prep.transformation-menu')
        .controller('TransformMenuCtrl', TransformMenuCtrl);
})();