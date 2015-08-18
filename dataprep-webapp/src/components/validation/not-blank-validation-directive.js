(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.type-validation.directive:NotBlankValidation
     * @description This directive perform a validation on the input value if needed.
     * @restrict E
     * @usage
     <input
            ...
            not-blank-validation />
     */
    function NotBlankValidation() {
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$validators.notBlankValidation = function(modelValue) {
                    if(attrs.notBlankValidation === 'false'){
                        return !!modelValue.trim();
                    }
                    return true;
                };
            }
        };
    }

    angular.module('data-prep.validation')
        .directive('notBlankValidation', NotBlankValidation);
})();