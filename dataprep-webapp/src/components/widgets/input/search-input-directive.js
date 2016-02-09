/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function () {
    'use strict';
    function TalendSearchInput() {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, iElement, iAttrs, ngModel) {
                //var margin = 5;
                var wrapper = angular.element('<div style="position:relative;"></div>');
                iElement.wrap(wrapper);

                var clearButton = angular.element('<div class="search-input-icon clear-icon"><span class="icon" data-icon="d"></span></div>');
                var searchIcon = angular.element('<div class="search-input-icon"><span class="icon" data-icon="D"></span></div>');

                iElement.parent().append(clearButton);
                iElement.parent().append(searchIcon);

                scope.$watch(
                    function () {
                        return ngModel.$modelValue;
                    },
                    function (value) {
                        if (value) {
                            clearButton.css('display', 'block');
                            searchIcon.css('display', 'none');
                        } else {
                            searchIcon.css('display', 'block');
                            clearButton.css('display', 'none');
                        }
                    });

                clearButton.on('click', function () {
                    scope.$apply(function () {
                        ngModel.$setViewValue('');
                        ngModel.$render();
                    });
                });
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendSearchInput', TalendSearchInput);
})();
