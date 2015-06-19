(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendAccordionsItem
     * @description Accordions item directive. This MUST be used with accordions directive.
     * @restrict E
     * @usage
     <talend-accordions>
         <talend-accordions-item>
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
     * @param {div} content The content element that will be injected in the content transclusion point
     */
    function talendAccordionsItem() {
        return {
            restrict: 'E',
            replace: true,
            transclude : true,
            templateUrl: 'components/widgets/accordions-new/accordions-item.html',
            require: '^^talendAccordions',
            scope: {
                'default': '='
            },
            bindToController: true,
            controller: function() {},
            controllerAs: 'accordionsItemCtrl',
            link: function(scope, iElement, iAttrs, accordionsCtrl) {
                //register itself
                var ctrl = scope.accordionsItemCtrl;
                accordionsCtrl.register(ctrl);
                if(ctrl.default) {
                    accordionsCtrl.toggle(ctrl);
                }

                //open function
                ctrl.toggle = function toggle() {
                    accordionsCtrl.toggle(ctrl);
                };

                //place trigger element in the trigger zone
                function attachElement(id, targetId) {
                    var elementToAttach = iElement.find('>#transclusion').find('#' + id).eq(0);
                    if(elementToAttach.length) {
                        elementToAttach.detach();
                        iElement.find('>#' + targetId).append(elementToAttach);
                    }
                }
                attachElement('trigger', 'trigger-transclusion');
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendAccordionsItem', talendAccordionsItem);
})();