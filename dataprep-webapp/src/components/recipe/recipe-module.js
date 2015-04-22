(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.recipe
     * @description This module contains the controller and directives to manage the recipe
     * @requires data-prep.services.playground
     * @requires data-prep.services.recipe
     */
    angular.module('data-prep.recipe', [
        'pascalprecht.translate',
        'data-prep.services.playground',
        'data-prep.services.recipe'
    ]);
})();