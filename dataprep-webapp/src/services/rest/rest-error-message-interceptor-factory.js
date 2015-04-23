(function() {
    'use strict';

    function RestErrorMessageHandler($q, MessageService){

        return {
            responseError: function(rejection) {
                if(rejection.status === 0) {
                    MessageService.error('SERVER_ERROR_TITLE', 'SERVICE_UNAVAILABLE');
                }

                else if(rejection.status === 500) {
                    MessageService.error('SERVER_ERROR_TITLE', 'GENERIC_ERROR');
                }

                else if(rejection.data) {

                    if(rejection.data.code === 'TDP_API_UNABLE_TO_DELETE_DATASET') {
                        MessageService.error('SERVER_ERROR_TITLE', 'DELETE_DATASET_ERROR');
                    }

                }


                return $q.reject(rejection);
            }
        };
    }

    angular.module('data-prep.services.rest')
        .factory('RestErrorMessageHandler', RestErrorMessageHandler)
        .config(['$httpProvider', function($httpProvider) {
            $httpProvider.interceptors.push('RestErrorMessageHandler');
        }]);
})();