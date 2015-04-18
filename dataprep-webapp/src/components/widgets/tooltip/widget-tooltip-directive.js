(function() {
    'use strict';

    /**
     * Configurable tooltip widget
     *
     * <talend-tooltip
     *      record="record"
     *      key="colId"
     *      position="position"
     *      requested-state="showTooltip"></talend-tooltip>
     *
     * Attr position : {x: number, y: number} - the position where to display the tooltip
     * Attr requested-state : show/hide tooltip if not blocked
     *
     * The tooltip state is blocked when the mouse is over it
     */
    function TalendTooltip($window, $document, $timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/tooltip/tooltip.html',
            replace: true,
            transclude: true,
            scope: {
                position: '=',
                requestedState: '='
            },
            bindToController: true,
            controller: 'TalendTooltipCtrl',
            controllerAs: 'talendTooltipCtrl',
            link: function(scope, iElement, iAttrs, ctrl) {
                /**
                 * Block (mouse over | focus in) unblock (mouse leave | focus out) the visibility change
                 */
                var hasFocus, isOver;
                var processBlockUnblock = function() {
                    if(hasFocus || isOver) {
                        ctrl.blockState();
                    }
                    else {
                        $timeout(ctrl.unblockState);
                    }
                };

                iElement.hover(
                    function() {
                        isOver = true;
                        processBlockUnblock();
                    },
                    function() {
                        isOver = false;
                        processBlockUnblock();
                    });
                iElement.focusin(function() {
                    hasFocus = true;
                    processBlockUnblock();
                });
                iElement.focusout(function() {
                    hasFocus = false;
                    processBlockUnblock();
                });

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

    angular.module('talend.widget')
        .directive('talendTooltip', TalendTooltip);
})();