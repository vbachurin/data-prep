(function() {
    'use strict';

    function TalendBadge($timeout) {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/widgets/badge/badge.html',
            scope: {
                onClose: '&',
                onChange: '&',
                text: '@',
                obj: '='
            },
            bindToController: true,
            controllerAs: 'badgeCtrl',
            controller: 'BadgeCtrl',
            link: function(scope, iElement, iAttrs, ctrl) {
                /**
                 * Change input width, depending on its content
                 * @param input - the target input
                 */
                var adjustWidth = function(input) {
                    var width = ((ctrl.value.length + 1) * 7) + 'px';
                    input.css('width', width);
                };

                $timeout(function() {
                    var input = iElement.find('input.editable-input').eq(0);
                    adjustWidth(input);

                    //Keydown (ESC and ENTER) listeners
                    input.keydown(function(event) {
                        //ENTER : change filter
                        if(event.keyCode === 13) {
                            input.blur();
                        }

                        //ESC : reset value and stop ESC key propagation
                        if(event.keyCode === 27) {
                            input.val(ctrl.obj.value);
                            ctrl.value = ctrl.obj.value;

                            input.blur();
                            event.stopPropagation();
                        }

                        adjustWidth(input);
                    });

                    //Blur listener : impact change
                    input.blur(ctrl.manageChange);

                    //Adjust input size
                    if(ctrl.obj) {
                        scope.$watch(
                            function() {
                                return ctrl.obj.value;
                            },
                            function() {
                                adjustWidth(input);
                            });
                    }
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendBadge', TalendBadge);
})();