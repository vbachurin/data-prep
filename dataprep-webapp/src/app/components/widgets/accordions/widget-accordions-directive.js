/**
 * @ngdoc directive
 * @name talend.widget.directive:TalendAccordions
 * @description Accordions directive. This is paired with accordions item directive.
 * @restrict E
 * @usage
 <talend-accordions>
     <talend-accordions-item on-open='fn' default='true'>
         <div class="trigger"></div>
         <div class="content"></div>
     </talend-accordions-item>
     <talend-accordions-item>
         <div class="trigger"></div>
         <div class="content"></div>
     </talend-accordions-item>
     <talend-accordions-item>
         <div class="trigger"></div>
         <div class="content"></div>
     </talend-accordions-item>
 </talend-accordions>
 * @param {div} trigger The trigger element that will be injected in the trigger transclusion point
 * @param {div} content The content element that is shown/hidden
 * @param {function} on-open The function to execute on accordion item open
 * @param {boolean} default The default accordion to open
 */
export default function TalendAccordions() {
    return {
        restrict: 'E',
        transclude: true,
        template: '<ul ng-transclude></ul>',
        controller: 'TalendAccordionsCtrl',
        controllerAs: 'accordionsCtrl'
    };
}