(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.recipeBullet
     * @description This module contains the controller and directives to manage the recipe Bullets
     * @requires data-prep.services.recipe.service:RecipeService
     * @requires data-prep.services.recipe.service:BulletService
     * @requires data-prep.services.playground.service:playground
     */
    angular.module('data-prep.recipeBullet', [
        'data-prep.services.recipe',
        'data-prep.services.playground'
    ]);
})();