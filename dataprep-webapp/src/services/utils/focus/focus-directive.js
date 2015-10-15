(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.services.utils:EnableFocus
     * @description This directive set focus on a element depending on a model value
     * @restrict A
     * @usage
     * <input type='text' enable-focus='beerTime' select-on-focus='true'/>
     * in case of beerTime === true the input text will have focus.
     * @param select-on-focus true to select the text or not when focus
     */
    function EnableFocus() {
        return {
            restrict: 'A',
            link: function(scope, iElement, attrs) {
                scope.$watch(attrs.enableFocus, function(newValue, oldValue) {
                    if (newValue) {
                        iElement[0].focus();
                        if (attrs.selectOnFocus && attrs.selectOnFocus==='true') {
                            iElement[0].select();
                        }
                    }
                });
            }
        };
    }

    angular.module('data-prep.services.utils')
        .directive('enableFocus', EnableFocus);
})();