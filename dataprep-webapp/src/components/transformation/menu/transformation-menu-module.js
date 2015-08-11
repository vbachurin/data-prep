(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.transformation-menu
     * @description This module contains the controller and directives to manage the transformation menu items
     * @requires talend.widget
     * @requires data-prep.transformation-params
     * @requires data-prep.type-transformation-menu
     * @requires data-prep.services.preparation
     * @requires data-prep.services.playground
     * @requires data-prep.services.transformation
     */
    angular.module('data-prep.transformation-menu', [
        'talend.widget',
        'data-prep.transformation-params',
        'data-prep.type-transformation-menu',
        'data-prep.services.preparation',
        'data-prep.services.playground',
        'data-prep.services.transformation'
    ]);
})();