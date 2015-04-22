(function() {
    'use strict';

    function DatagridCtrl($timeout) {
        var vm = this;
        var tooltipPromise, tooltipHidePromise;
        var tooltipDelay = 300;

        /**
         * Cancel the current tooltip promise
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
         * Update the tooltip component and display with a delay
         * @param record - the current record
         * @param position - the position where to display it
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
         * Cancel the old tooltip promise if necessary and create a new one
         * @param record - the current record
         * @param position - the position where to display it
         */
        vm.updateTooltip = function(record, colId, position) {
            cancelTooltip();
            createTooltip(record, colId, position);
        };

        /**
         * Cancel the old tooltip promise if necessary and hide the tooltip
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