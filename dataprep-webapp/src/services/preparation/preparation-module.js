(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.module:preparation
     * @description This module contains the services to manipulate preparations
     * @requires data-prep.services.module:dataset
     * @requires data-prep.services.module:utils
     */
    angular.module('data-prep.services.preparation', [
        'data-prep.services.utils',
        'data-prep.services.dataset'
    ]);
})();