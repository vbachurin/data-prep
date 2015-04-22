(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid.controller:DatagridCtrl
     * @description Dataset grid controller.
     */
    function DatagridCtrl($timeout) {
        var vm = this;
        var tooltipPromise, tooltipHidePromise;
        var tooltipDelay = 300;

        /**
         * @ngdoc method
         * @name cancelTooltip
         * @methodOf data-prep.datagrid.controller:DatagridCtrl
         * @description [PRIVATE] Cancel the current tooltip promise
         */
        var cancelTooltip = function() {
            if(tooltipPromise) {
                $timeout.cancel(tooltipPromise);
            }
            if(tooltipHidePromise) {
                $timeout.cancel(tooltipHidePromise);
            }
        };

        /**
         * @ngdoc method
         * @name createTooltip
         * @methodOf data-prep.datagrid.controller:DatagridCtrl
         * @param {object} record - the current record
         * @param {string} colId - the column id
         * @param {object} position - the position where to display it {x: number, y: number}
         * @description [PRIVATE] Update the tooltip component and display with a delay
         */
        var createTooltip = function(record, colId, position) {
            var delay = vm.showTooltip ? 0 : tooltipDelay;
            tooltipPromise = $timeout(function() {
                vm.record = record;
                vm.position = position;
                vm.colId = colId;
                vm.showTooltip = true;
            }, delay);
        };

        /**
         * @ngdoc method
         * @name updateTooltip
         * @methodOf data-prep.datagrid.controller:DatagridCtrl
         * @param {object} record - the current record
         * @param {string} colId - the column id
         * @param {object} position - the position where to display it {x: number, y: number}
         * @description Cancel the old tooltip promise if necessary and create a new one
         */
        vm.updateTooltip = function(record, colId, position) {
            cancelTooltip();
            createTooltip(record, colId, position);
        };

        /**
         * @ngdoc method
         * @name hideTooltip
         * @methodOf data-prep.datagrid.controller:DatagridCtrl
         * @description Cancel the old tooltip promise if necessary and hide the tooltip
         */
        vm.hideTooltip = function() {
            cancelTooltip();
            tooltipHidePromise = $timeout(function() {
                vm.showTooltip = false;
            }, tooltipDelay);
        };
    }

    angular.module('data-prep.datagrid')
        .controller('DatagridCtrl', DatagridCtrl);
})();