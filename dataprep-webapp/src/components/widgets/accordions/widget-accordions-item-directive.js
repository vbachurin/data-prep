(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendAccordionsItem
     * @description Accordions item directive. This MUST be used with accordions directive.
     * @restrict E
     * @usage
     <talend-accordions>
         <talend-accordions-item on-open='fn' default='true'>
             <div id="trigger"></div>
             <div id="content"></div>
         </talend-accordions-item>
         <talend-accordions-item>
             <div id="trigger"></div>
             <div id="content"></div>
         </talend-accordions-item>
         <talend-accordions-item>
             <div id="trigger"></div>
             <div id="content"></div>
         </talend-accordions-item>
     </talend-accordions>
     * @param {div} trigger The trigger element that will be injected in the trigger transclusion point
     * @param {div} content The content element that is shown/hidden
     * @param {function} on-open The function to execute on accordion item open
     * @param {boolean} default The default accordion to open
     */
    function talendAccordionsItem($timeout) {
        return {
            restrict: 'E',
            replace: true,
            transclude : true,
            templateUrl: 'components/widgets/accordions/accordions-item.html',
            require: '^^talendAccordions',
            scope: {
                'default': '=',
                onOpen: '&'
            },
            bindToController: true,
            controller: function() {},
            controllerAs: 'accordionsItemCtrl',
            link: function(scope, iElement, iAttrs, accordionsCtrl) {
                var ctrl = scope.accordionsItemCtrl;
                var contentElement;

                /**
                 * @ngdoc method
                 * @name getContentElement
                 * @methodOf talend.widget.directive:TalendAccordionsItem
                 * @description [PRIVATE] Get content element. If it is defined, we save it to serve it directly next time
                 */
                var getContentElement = function getContentElement() {
                    if(contentElement) {
                        return contentElement;
                    }
                    var fetchContent = iElement.find('#content').eq(0);
                    if(fetchContent.length) {
                        contentElement = fetchContent;
                    }
                    return fetchContent;
                };

                /**
                 * @ngdoc method
                 * @name registerToParent
                 * @methodOf talend.widget.directive:TalendAccordionsItem
                 * @description [PRIVATE] Register itself to parent
                 */
                var registerToParent = function registerToParent() {
                    accordionsCtrl.register(ctrl);
                    if(ctrl.default) {
                        accordionsCtrl.toggle(ctrl);
                    }
                };

                /**
                 * @ngdoc method
                 * @name attachTriggerElement
                 * @methodOf talend.widget.directive:TalendAccordionsItem
                 * @description [PRIVATE] Place trigger element in the trigger zone
                 */
                var attachTriggerElement = function attachTriggerElement() {
                    $timeout(function() {
                        var elementToAttach = iElement.find('#trigger').eq(0);
                        iElement.find('>#trigger-transclusion').append(elementToAttach);
                    });
                };

                /**
                 * @ngdoc method
                 * @name attachUnregisterOnDestroy
                 * @methodOf talend.widget.directive:TalendAccordionsItem
                 * @description [PRIVATE] Unregister itself on element destroy
                 */
                var attachUnregisterOnDestroy = function attachUnregisterOnDestroy() {
                    scope.$on('$destroy', function() {
                        accordionsCtrl.unregister(ctrl);
                    });
                };

                registerToParent();
                attachTriggerElement();
                attachUnregisterOnDestroy();

                //toggle function triggered on click
                ctrl.toggle = function toggle() {
                    accordionsCtrl.toggle(ctrl);
                };

                //slide animation to open/hide content
                scope.$watch(function() {
                    return ctrl.active;
                }, function(active) {
                    if(active) {
                        getContentElement().slideDown('fast');
                        iElement.addClass('open');
                        ctrl.onOpen();
                    }
                    else {
                        getContentElement().slideUp('fast');
                        iElement.removeClass('open');
                    }
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendAccordionsItem', talendAccordionsItem);
})();