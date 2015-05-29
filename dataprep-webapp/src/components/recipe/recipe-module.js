(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.recipe
     * @description This module contains the controller and directives to manage the recipe
     * @requires talend.widget
     * @requires data-prep.services.playground
     * @requires data-prep.services.recipe
     * @requires data-prep.services.recipeBullet
     * @requires data-prep.services.preparation
     * @requires data-prep.transformation-params
     */
    angular.module('data-prep.recipe', [
        'pascalprecht.translate',
        'talend.widget',
        'data-prep.services.playground',
        'data-prep.services.recipe',
        'data-prep.services.preparation',
        'data-prep.recipeBullet'
        'data-prep.transformation-params'
    ]);
})();