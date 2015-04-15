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
     * Attr position : {x: number, y: number} - the position where to display the tooltip
     * Attr requested-state : show/hide tooltip if not blocked
     *
     * The tooltip state is blocked when the mouse is over it
     */
    function DatagridTooltip($timeout, $window, $document) {
        return {
            restrict: 'E',
            templateUrl: 'components/datagrid/datagrid-tooltip/datagrid-tooltip.html',
            replace: true,
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
                 * Block (mouse over) unblock (mouse leave) the visibility change
                 */
                iElement.hover(
                    function() {
                        ctrl.blockState();
                    },
                    function() {
                        $timeout(ctrl.unblockState);
                    }
                );

                /**
                 * Calculate left/right position.
                 * If the place to display is in the first half of the window, the tooltip is displayed on the right
                 * Otherwise, the tooltip is displayed on the left
                 * @param position - {{x: Number}} the requested position
                 * @returns {{left: String, right: String}}
                 */
                var calculateHorizontalPosition = function(position) {
                    var windowWidth = $window.innerWidth || $document.documentElement.clientWidth || $document.body.clientWidth;

                    if(position.x < windowWidth / 2) {
                        return {
                            left: position.x + 'px',
                            right: 'auto'
                        };

                    }
                    else {
                        return {
                            left: 'auto',
                            right: (windowWidth - position.x) + 'px'
                        };
                    }
                };

                /**
                 * Calculate top/bottom position.
                 * If the place to display is in the first half of the window, the tooltip is displayed on the bottom
                 * Otherwise, the tooltip is displayed on the top
                 * @param position - {{y: Number}} the requested position
                 * @returns {{top: String, bottom: String}}
                 */
                var calculateVerticalPosition = function(position) {
                    var windowHeight = $window.innerHeight || $document.documentElement.clientHeight || $document.body.clientHeight;

                    if(position.y < windowHeight / 2) {
                        return {
                            top: position.y,
                            bottom: 'auto'
                        };
                    }
                    else {
                        return {
                            top: 'auto',
                            bottom: (windowHeight - position.y) + 'px'
                        };
                    }
                };

                /**
                 * Calculate css and update tooltip on requested position change
                 */
                scope.$watch(
                    function() {
                        return ctrl.position;
                    },
                    function(position) {
                        if(position) {
                            var horizontalPosition = calculateHorizontalPosition(position);
                            var verticalPosition = calculateVerticalPosition(position);

                            ctrl.updatePosition(horizontalPosition, verticalPosition);
                        }
                    }
                );
            }
        };
    }

    angular.module('data-prep.datagrid-tooltip')
        .directive('datagridTooltip', DatagridTooltip);
})();

