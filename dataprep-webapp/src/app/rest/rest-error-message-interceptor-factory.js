(function() {
    'use strict';
    
    function RestErrorMessageHandler($q, toaster){
        return {
            responseError: function(rejection) {
                if(rejection.status === 0) {
                    toaster.pop('error', 'Error', 'Service unavailable');
                }

                else if(rejection.status === 500) {
                    toaster.pop('error', 'Error', 'An error occurred');
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