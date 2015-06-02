(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.playground
     * @description This module contains the services to load the playground
     * @requires data-prep.services.dataset
     * @requires data-prep.services.preparation
     * @requires data-prep.services.filter
     * @requires data-prep.services.recipe
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.services.playground', [
        'data-prep.services.dataset',
        'data-prep.services.preparation',
        'data-prep.services.filter',
        'data-prep.services.recipe',
        'data-prep.services.utils'
    ]);
})();