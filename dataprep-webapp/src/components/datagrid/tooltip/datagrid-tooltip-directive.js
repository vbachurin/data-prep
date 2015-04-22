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
    function DatagridTooltip($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/datagrid/tooltip/datagrid-tooltip.html',
            scope: {
                record: '=',
                key: '=',
                position: '=',
                requestedState: '='
            },
            bindToController: true,
            controller: 'DatagridTooltipCtrl',
            controllerAs: 'tooltipCtrl',
            link: function(scope, iElement, iAttrs, ctrl) {
                /**
                 * On edition mode enable, we focus on textarea
                 */
                scope.$watch(
                    function() { return ctrl.editMode; },
                    function(newValue) {
                        if(newValue) {
                            $timeout(function() {
                                iElement.find('textarea').eq(0).focus();
                            });
                        }
                    }
                );
            }
        };
    }

    angular.module('data-prep.datagrid-tooltip')
        .directive('datagridTooltip', DatagridTooltip);
})();

