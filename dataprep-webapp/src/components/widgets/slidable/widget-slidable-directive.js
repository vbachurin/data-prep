(function() {
    'use strict';

    function TalendSlidable($window) {
        return {
            restrict: 'E',
            transclude: true,
            replace: true,
            templateUrl: 'components/widgets/slidable/slidable.html',
            scope: {
                side: '@',
                visible: '=',
                resizable: '@'
            },
            bindToController: true,
            controllerAs: 'slidableCtrl',
            controller: function() {
                var vm = this;

                vm.toggle = function() {
                    vm.visible = ! vm.visible;
                };
            },
            link: function(scope, iElement, iAttrs, ctrl) {
                if(ctrl.resizable) {
                    var localStorageWidthKey = 'data-prep-' + ctrl.resizable + '-width';
                    var width = $window.localStorage.getItem(localStorageWidthKey) || iElement.width() + 'px';

                    iElement.resizable({
                        handles: ctrl.side === 'right' ? 'w' : 'e',
                        start: function() {
                            iElement.addClass('no-transition');
                        },
                        stop: function(event, ui) {
                            iElement.removeClass('no-transition');
                            width = ui.size.width + 'px';
                            $window.localStorage.setItem(localStorageWidthKey, width);
                        },
                        resize: function(event, ui) {
                            iElement.css('left', 'auto');
                            iElement.css('right', 'auto');
                            iElement.css('flex', '0 ' + ui.size.width + 'px');
                        }
                    });

                    scope.$watch(
                        function() {
                            return ctrl.visible;
                        },
                        function(visible) {
                            if(visible) {
                                iElement.css('flex', '0 ' + width);
                                iElement.resizable('option', 'disabled', false);
                            }
                            else {
                                iElement.css('flex', '');
                                iElement.resizable('option', 'disabled', true);
                            }
                        }
                    );
                }
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendSlidable', TalendSlidable);
})();