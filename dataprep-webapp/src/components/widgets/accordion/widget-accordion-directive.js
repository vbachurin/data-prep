(function() {
    'use strict';

    /**
     * <ul class="talend-accordion">
     *      <li>
     *          <a class="talend-accordion-trigger" on-open="fn()">click me to show content</a>
     *          <ul class="submenu">
     *              <li>
     *                  Content Here
     *              </li>
     *          </ul>
     *      </li>
     * </ul>
     *
     * .talend-accordion : on the accordion node delimiter
     * .talend-accordion-trigger : show/hide submenu with the same parent node
     * .talent-accordion-trigger -> on-open : open callback
     * .submenu : submenu to show. It must have the same parent node as the trigger node
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
                };

                /**
                 * Bind click event : hide all submenus and show/hide current submenu
                 */
                iElement.bind('click', function(e){
                    var subMenu = angular.element(this).parent().find('.submenu');
                    var isOpened = subMenu.is(':visible') || subMenu.css('display') === 'block';

                    closeAllAccordions();
                    if(! isOpened) {
                        $timeout(ctrl.onOpen);
                        subMenu.slideToggle('fast');
                    }

                    e.preventDefault();
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendAccordionTrigger', TalendAccordionTrigger);
})();