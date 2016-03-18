/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

/**
 * @ngdoc directive
 * @name talend.widget.directive:Typeahead
 * @description This directive create an input with a dropdown element.
 * @param {function} search function called when input changes
 */
export default function Typeahead() {
    'ngInject';

    return {
        restrict: 'E',
        transclude: true,
        templateUrl: 'app/components/widgets/typeahead/typeahead.html',
        scope: {
            search: '&'
        },
        bindToController: true,
        controller: 'TypeaheadCtrl',
        controllerAs: 'typeaheadCtrl',
        link: {
            post: (scope, iElement, iAttrs, ctrl) => {
                let body = angular.element('body').eq(0);
                let input = iElement.find('.input-search');
                let menu = iElement.find('.typeahead-menu');

                let hideMenu = () => {
                    menu.removeClass('show-menu');
                };

                let showMenu = () => {
                    menu.addClass('show-menu');
                    menu.css('width', input[0].getBoundingClientRect().width + 'px');
                };

                input.click((event) => {
                    event.stopPropagation();
                });

                menu.click((event) => {
                    event.stopPropagation();
                    hideMenu();
                });

                menu.keydown((event) => {
                    if (event.keyCode === 27) {
                        hideMenu();
                        event.stopPropagation();
                    }
                });

                body.click(hideMenu);

                iElement.on('$destroy', () => {
                    scope.$destroy();
                });
                scope.$on('$destroy', () => {
                    body.off('click', hideMenu);
                });

                scope.$watch('typeaheadCtrl.searchString', (newValue) => {
                    if (newValue) {
                        let isVisible = menu.hasClass('show-menu');
                        if (!isVisible) {
                            showMenu();
                        }
                        ctrl.search({value: ctrl.searchString})
                    } else {
                        hideMenu();
                    }
                });
            }
        }
    };
}