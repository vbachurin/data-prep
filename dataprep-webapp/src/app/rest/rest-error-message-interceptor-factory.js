(function() {
    'use strict';

    function RestErrorMessageHandler($q, $translate, toaster){
        return {
            responseError: function(rejection) {
                if(rejection.status === 0) {
                    $translate('SERVICE_UNAVAILABLE').then(function(value) {
                        toaster.pop('error', 'Error', value);
                    });
                    
                }

                else if(rejection.status === 500) {
                    $translate('GENERIC_ERROR').then(function(value) {
                        toaster.pop('error', 'Error', value);
                    });
                }

                return $q.reject(rejection);
            }
        };
    }

    angular.module('data-prep')
        .factory('RestErrorMessageHandler', RestErrorMessageHandler)
        .config(['$httpProvider', function($httpProvider) {
            $httpProvider.interceptors.push('RestErrorMessageHandler');
        }]);
})();