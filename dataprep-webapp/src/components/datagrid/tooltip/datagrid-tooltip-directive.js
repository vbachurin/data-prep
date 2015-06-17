(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.datagrid-tooltip.directive:DatagridTooltip
     * @description This directive display a tooltip with a customizable content for the datagrid. This content is base
     * on the `records` and `key` attributes. The displayed value is `record[key]`.
     *
     *  Watchers:
     * <ul>
     *    <li>Toggle textarea when {@link data-prep.datagrid-tooltip.controller:DatagridTooltipCtrl controller} edit mode change</li>
     * </ul>
     *
     * @restrict E
     * @usage
     <datagrid-tooltip
             record="record"
             key="colId"
             position="position"
             requested-state="showTooltip">
     </datagrid-tooltip>
     * @param {object} record The object containing the text to display
     * @param {string} key The key of the value to display
     * @param {object} position Attribute delegated to {@link talend.widget.directive:TalendTooltip talend-tooptip} widget. <br/>{x: number, y: number} - the position where to display the tooltip
     * @param {boolean} requestedState Attribute delegated to {@link talend.widget.directive:TalendTooltip talend-tooptip} widget. <br/>Show/hide tooltip if not blocked
     */
    function DatagridTooltip($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/datagrid/tooltip/datagrid-tooltip.html',
            scope: {
                record: '=',
                key: '=',
                position: '=',
                requestedState: '=',
                htmlStr: '='
            },
            bindToController: true,
            controller: 'DatagridTooltipCtrl',
            controllerAs: 'tooltipCtrl',
            link: function(scope, iElement, iAttrs, ctrl) {
                /**
                 * On edition mode enable, we focus on textarea.
                 * $timeout is necessary because the textarea is not visible yet until angular process the digest cycle
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

