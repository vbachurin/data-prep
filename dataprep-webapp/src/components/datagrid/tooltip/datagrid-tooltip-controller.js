(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid-tooltip.controller:DatagridTooltipCtrl
     * @description Datagrid tooltip controller.<br/>
     Watcher:
     <ul>
        <li>Turn off edition mode on new tooltip. We watch the position (a new position is a new tooltip)</li>
     </ul>
     */
    function DatagridTooltipCtrl($scope) {
        var vm = this;
        var lastEdited = {};
        vm.editMode = false;

        /**
         * @ngdoc method
         * @name edit
         * @methodOf data-prep.datagrid-tooltip.controller:DatagridTooltipCtrl
         * @description Enable edit mode. The input value is reset if the cell is different from last edited cell.
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