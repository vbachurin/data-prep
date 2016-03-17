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
 * @description This directive create an input with a dropdown element.<br/>
 * @param {boolean} closeOnSelect Default `true`. If set to false, dropdown will not close on inner item click
 * @param {string} forceSide Force display on the specified side (left | right)
 * @param {string} searchString input model
 * @param {function} onChange function called when input changes
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
            post: function (scope, iElement, iAttrs, ctrl) {
                var body = angular.element('body').eq(0);
                var input = iElement.find('.input-search');
                var menu = iElement.find('.typeahead-menu');

                function hideMenu() {
                    menu.removeClass('show-menu');
                }

                function showMenu() {
                    menu.addClass('show-menu');
                    menu.css('width', input[0].getBoundingClientRect().width + 'px');
                }

                input.click(function (event) {
                    event.stopPropagation();
                });

                menu.click(function (event) {
                    event.stopPropagation();
                    hideMenu();
                });

                menu.keydown(function (event) {
                    if (event.keyCode === 27) {
                        hideMenu();
                        event.stopPropagation();
                    }
                });

                body.click(hideMenu);

                iElement.on('$destroy', function () {
                    scope.$destroy();
                });
                scope.$on('$destroy', function () {
                    body.off('click', hideMenu);
                });

                scope.$watch('typeaheadCtrl.searchString', function (newValue) {
                    if (newValue) {
                        var isVisible = menu.hasClass('show-menu');
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