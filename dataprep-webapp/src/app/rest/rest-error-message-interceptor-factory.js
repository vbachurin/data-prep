(function() {
    'use strict';

    function RestErrorMessageHandler($q, $window){
        return {
            responseError: function(rejection) {
                if(rejection.status === 0) {
                    $window.alert('Service unavailable');
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