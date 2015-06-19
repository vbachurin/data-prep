(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendAccordions
     * @description Accordions directive. This is paired with accordions item directive.
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
    function TalendAccordions() {
        return {
            restrict: 'E',
            replace: true,
            transclude : true,
            template: '<ul><ng-transclude></ng-transclude></ul>',
            controller: 'TalendAccordionsCtrl',
            controllerAs: 'accordionsCtrl'
        };
    }

    angular.module('talend.widget')
        .directive('talendAccordions', TalendAccordions);
})();