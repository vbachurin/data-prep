(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.recipe
     * @description This module contains the services to manipulate the recipe
     * @requires data-prep.services.preparation
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.services.recipe', [
        'data-prep.services.utils',
        'data-prep.services.preparation'
    ]);
})();