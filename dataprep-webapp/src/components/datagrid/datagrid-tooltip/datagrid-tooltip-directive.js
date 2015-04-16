(function () {
    'use strict';

    /**
     * Datagrid tooltip
     *
     * <datagrid-tooltip
     *      record="record"
     *      key="colId"
     *      position="position"
     *      requested-state="showTooltip"></datagrid-tooltip>
     *
     * Attr record : the object containing the text to display
     * Attr key : the key of the value to display
     *
     * Attributes delegated to talend-tooptip widget :
     * Attr position : {x: number, y: number} - the position where to display the tooltip
     * Attr requested-state : show/hide tooltip if not blocked
     *
     */
    function DatagridTooltip() {
        return {
            restrict: 'E',
            templateUrl: 'components/datagrid/datagrid-tooltip/datagrid-tooltip.html',
            scope: {
                record: '=',
                key: '=',
                position: '=',
                requestedState: '='
            },
            bindToController: true,
            controller: function() {},
            controllerAs: 'tooltipCtrl'
        };
    }

    angular.module('data-prep.datagrid-tooltip')
        .directive('datagridTooltip', DatagridTooltip);
})();

