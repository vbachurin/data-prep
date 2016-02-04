import CanBeBlankValidation from './can-be-blank-validation-directive';
import IsDateTimeValidation from './is-datetime-validation-directive';
import IsTypeValidation from './is-type-validation-directive';
import UniqueFolderValidation from './unique-folder-validation-directive';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.validation
     * @description This module contains the directive to perform type validation on forms
     */
    angular.module('data-prep.validation', [])
        .directive('canBeBlank', CanBeBlankValidation)
        .directive('isDateTime', IsDateTimeValidation)
        .directive('isType', IsTypeValidation)
        .directive('uniqueFolder', UniqueFolderValidation);
})();