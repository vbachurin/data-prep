(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.module:recipe
     * @description This module contains the services to manipulate the recipe
     * @requires data-prep.services.module:preparation
     * @requires data-prep.services.module:utils
     */
    angular.module('data-prep.services.recipe', [
        'data-prep.services.utils',
        'data-prep.services.preparation'
    ]);
})();