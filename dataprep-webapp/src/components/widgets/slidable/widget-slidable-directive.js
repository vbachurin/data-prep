(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendSlidable
     * @description Slidable widget.<br/>
     * If the slidable is resizable, the widget keeps the last width in localstorage so the element is resized in creation<br/>
     * To customize the slidable width, use the sass mixin to set properly all the flexbox element size
      <pre>

            .my-slidable-element {
               &#64;include slidable-size(300px, 20px);
            }

      </pre>
     * Parameters :
     * <ul>
     *     <li>First param (300px) is the panel width</li>
     *     <li>Second param (20px) is action bar (used to show/hide) width</li>
     * </ul>
     * @restrict E
     * @usage
      <talend-slidable
           visible="visible"
           side="right"
           resizable="custom-namespace">
               Content
      </talend-slidable>
     * @param {boolean} visible Controle the slidable visibility
     * @param {string} side `left` (default) | right. This defines the action bar and the resize bar position
     * @param {string} resizable Pass unique ID that will be used to store custom size in local storage (key = {data-prep-' + resizable_namespace + '-width}).<br/>
     * Resize feature is disabled by default and enabled if the attribute si set
     */
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
                    var localStorageWidthKey = 'dataprep.' + ctrl.resizable + '.width';
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