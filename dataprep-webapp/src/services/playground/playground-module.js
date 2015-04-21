(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.module:playground
     * @description This module contains the services to load the playground
     * @requires data-prep.services.module:dataset
     * @requires data-prep.services.module:filter
     * @requires data-prep.services.module:recipe
     * @requires data-prep.services.module:utils
     */
    angular.module('data-prep.services.playground', [
        'data-prep.services.dataset',
        'data-prep.services.filter',
        'data-prep.services.recipe',
        'data-prep.services.utils'
    ]);
})();