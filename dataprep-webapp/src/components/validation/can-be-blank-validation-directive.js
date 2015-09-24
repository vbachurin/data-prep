(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.validation.directive:CanBeBlankValidation
     * @description This directive perform a validation on the input value if needed.
     * @restrict E
     * @usage
     <input
            ...
            can-be-blank="false" />
     * @param {string} canBeBlank 'false' = should check for non blank value. Any other value (including empty) is considered as 'true'
     */
    function CanBeBlankValidation() {
        return {
            require: 'ngModel',
            link: function(scope, elm, attrs, ctrl) {
                ctrl.$validators.canBeBlankValidation = function(modelValue) {
                    var mandatory = attrs.canBeBlank === 'false';

                    // not mandatory OR (value not null AND not blank)
                    return !mandatory || (modelValue !== null && !!(''+modelValue).trim());
                };
            }
        };
    }

    angular.module('data-prep.validation')
        .directive('canBeBlank', CanBeBlankValidation);
})();