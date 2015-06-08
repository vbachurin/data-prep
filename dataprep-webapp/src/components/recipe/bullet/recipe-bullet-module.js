(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.recipe-bullet
     * @description This module contains the controller and directives to manage the recipe bullets
     * @requires data-prep.services.recipe
     * @requires data-prep.services.playground
     */
    angular.module('data-prep.recipe-bullet', [
        'data-prep.services.recipe',
        'data-prep.services.playground'
    ]);
})();