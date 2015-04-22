(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.datagrid-tooltip.directive:DatagridTooltip
     * @description Datagrid tooltip
     <pre><datagrid-tooltip
           record="record"
           key="colId"
           position="position"
           requested-state="showTooltip"></datagrid-tooltip>
     </pre>

     <table>
        <tr>
            <th>Directive own attributes</th>
            <th>Description</th>
        </tr>
        <tr>
            <td>record</td>
            <td>the object containing the text to display</td>
        </tr>
        <tr>
            <td>key</td>
            <td>the key of the value to display</td>
        </tr>
     </table>
     <table>
        <tr>
            <th>Attributes delegated to {@link talend.widget.directive:TalendTooltip talend-tooptip} widget</th>
            <th>Description</th>
        </tr>
        <tr>
            <td>position</td>
            <td>{x: number, y: number} - the position where to display the tooltip</td>
        </tr>
        <tr>
            <td>requested-state</td>
            <td>show/hide tooltip if not blocked</td>
        </tr>
     </table>

     Watchers:
     <ul>
        <li>Toggle textarea when {@link data-prep.datagrid-tooltip.controller:DatagridTooltipCtrl controller} edit mode change</li>
     </ul>

     * @restrict E
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

