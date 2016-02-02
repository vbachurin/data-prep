import RestErrorMessageHandler from './rest-error-message-interceptor-factory';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.rest
     * @description This module contains the REST interceptor
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.services.rest', ['data-prep.services.utils'])
        .factory('RestErrorMessageHandler', RestErrorMessageHandler)
        .config(function ($httpProvider) {
            'ngInject';
            $httpProvider.interceptors.push('RestErrorMessageHandler');
        });
})();