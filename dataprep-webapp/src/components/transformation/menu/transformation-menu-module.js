(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.transformation-menu
     * @description This module contains the controller and directives to manage the transformation menu items
     * @requires talend.widget
     * @requires data-prep.type-validation
     * @requires data-prep.transformation-params
     * @requires data-prep.services.dataset
     * @requires data-prep.services.recipe
     * @requires data-prep.services.preparation
     * @requires data-prep.services.playground
     */
    angular.module('data-prep.transformation-menu', [
        'talend.widget',
        'data-prep.type-validation',
        'data-prep.transformation-params',
        'data-prep.services.dataset',
        'data-prep.services.recipe',
        'data-prep.services.preparation',
        'data-prep.services.playground'
    ]);
})();