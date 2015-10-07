(function() {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.transformation-params
     * @description This module contains the controller and directives to manage transformation parameters
     * @requires data-prep.services.state
     * @requires data-prep.services.utils.service
     * @requires data-prep.validation
     */
    angular.module('data-prep.transformation-params', [
        'data-prep.services.state',
        'data-prep.services.utils',
        'data-prep.validation',
        'talend.widget'
    ]);
})();