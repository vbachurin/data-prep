(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.services.utils:EnableFocus
     * @description This directive set focus on a element depending on a model value
     * @restrict A
     * @usage
     * <input type='text' enable-focus='ctrl.beerTime'/>
     * in case of beerTime === true the input text will have focus.
     */
    function EnableFocus() {
        return {
            restrict: 'A',
            link: function(scope, iElement, attrs) {
                scope.$watch(attrs.enableFocus, function(newValue, oldValue) {
                    if (newValue) {
                        iElement[0].focus();
                    }
                });
            }
        };
    }

    angular.module('data-prep.services.utils')
        .directive('enableFocus', EnableFocus);
})();