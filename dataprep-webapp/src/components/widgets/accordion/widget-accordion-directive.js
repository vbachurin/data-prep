(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendAccordion
     * @description This directive create an accordion item, within a ".talend-accordion" tag
     * @restrict C
     * @usage
     <ul class="talend-accordion">
           <li>
               <a class="talend-accordion-trigger" on-open="callback()">
                   click me to show content
                   <div class="talend-accordion-no-trigger">Click on me will not trigger the accordion</div>
               </a>
               <ul class="submenu">
                   <li>
                       Content Here
                   </li>
               </ul>
           </li>
     </ul>
     * @param {class} talend-accordion On the accordion node delimiter
     * @param {class} talend-accordion-trigger Hide submenus with the same parent node and toggle the target submenu
     * @param {function} on-open On the same element that has `talend-accordion-trigger` class. Provide an `open` callback
     * @param {class} talend-accordion-no-trigger Element with that class will not trigger the accordion (within a trigger element)
     * @param {class} submenu Submenu to show. It must have the same parent node as the trigger node
     */
    function TalendAccordionTrigger($timeout) {
        return {
            restrict: 'C',
            scope : {
                onOpen: '&'
            },
            bindToController: true,
            controller: function() {},
            controllerAs: 'accordionCtrl',
            link: function(scope, iElement, iAttr, ctrl) {
                /**
                 * Close all the submenus contained is the closest .talend-accordion parent
                 */
                var closeAllAccordions = function() {
                    iElement.closest('.talend-accordion').find('.submenu').slideUp('fast');
                    iElement.closest('.talend-accordion').find('.open').removeClass('open');
                };

                /**
                 * Bind click event : hide all submenus and show/hide current submenu
                 */
                iElement.bind('click', function(e){
                    var noTrigger = angular.element(e.target).hasClass('talend-accordion-no-trigger');

                    if(!noTrigger) {
                        var parent = angular.element(this).parent();
                        var subMenu = angular.element(this).parent().find('.submenu');
                        var isOpened = subMenu.is(':visible') || subMenu.css('display') === 'block';

                        closeAllAccordions();
                        if(! isOpened) {
                            $timeout(ctrl.onOpen);
                            parent.addClass('open');
                            subMenu.slideToggle('fast');
                        }
                    }

                    e.preventDefault();
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendAccordionTrigger', TalendAccordionTrigger);
})();