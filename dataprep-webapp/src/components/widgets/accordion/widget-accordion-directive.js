(function() {
    'use strict';

    function TalendAccordion() {
        return {
            restrict: 'C',
            bindToController: true,
            link: function(scope, iElement) {
                var closeAllAccordions = function() {
                    iElement.find('.submenu').slideUp('fast');
                };

                iElement.find('.talend-accordion-trigger').bind('click', function(e){
                    var subMenu = angular.element(this).parent().find('.submenu');
                    var isOpened = subMenu.is(':visible') || subMenu.css('display') === 'block';

                    closeAllAccordions();
                    if(! isOpened) {
                        subMenu.slideToggle('fast');
                    }
                    e.preventDefault();
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendAccordion', TalendAccordion);
})();