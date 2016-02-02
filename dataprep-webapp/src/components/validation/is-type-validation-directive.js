'use strict';
const INTEGER_REGEXP = /^\-?\d+$/;
const DOUBLE_REGEXP = /^\-?\d+(\.\d+)?$/;

/**
 * @ngdoc directive
 * @name data-prep.validation.directive:IsTypeValidation
 * @description This directive perform a type validation on input value modification.
 * @restrict E
 * @usage <input ... is-type="integer" />
 * @param {string} isType The wanted type (integer | numeric | double | float)
 */
export default function IsTypeValidation() {
    return {
        require: 'ngModel',
        link: function (scope, elm, attrs, ctrl) {
            var type = attrs.isType;
            ctrl.$validators.isTypeValidation = function (modelValue, viewValue) {
                if (ctrl.$isEmpty(modelValue)) {
                    return true;
                }

                switch (type) {
                    case 'integer':
                        if (!INTEGER_REGEXP.test(viewValue)) {
                            return false;
                        }
                        break;
                    case 'numeric':
                    case 'double':
                    case 'float':
                        if (!DOUBLE_REGEXP.test(viewValue)) {
                            return false;
                        }
                        break;
                    default:
                        break;
                }

                return true;
            };
        }
    };
}