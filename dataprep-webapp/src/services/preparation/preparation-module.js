(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.preparation
     * @description This module contains the services to manipulate preparations
     * @requires data-prep.services.dataset
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.services.preparation', [
        'data-prep.services.utils',
        'data-prep.services.dataset'
    ]);
})();