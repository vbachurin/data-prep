(function() {
    'use strict';

    function DatagridTooltipCtrl($scope) {
        var vm = this;
        var lastEdited = {};
        vm.editMode = false;

        /**
         * Enable edit mode
         * The input value is reset if the cell is different from last edited cell
         */
        vm.edit = function() {
            if(lastEdited.record !== vm.record || lastEdited.key !== vm.key) {
                lastEdited = {
                    record: vm.record,
                    key: vm.key
                };
                vm.inputValue = vm.record[vm.key];
            }
            vm.editMode = true;
        };

        /**
         * Reset edit mode on new tooltip
         */
        $scope.$watch(
            function() { return vm.position; },
            function() { vm.editMode = false; }
        );
    }

    angular.module('data-prep.datagrid-tooltip')
        .controller('DatagridTooltipCtrl', DatagridTooltipCtrl);
})();