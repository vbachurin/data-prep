(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.rest.service:RestErrorMessageHandler
     * @description Error message interceptor
     * @requires data-prep.services.utils.service:MessageService
     */
    function RestErrorMessageHandler($q, MessageService){

        return {
            /**
             * @ngdoc method
             * @name responseError
             * @methodOf data-prep.services.rest.service:RestErrorMessageHandler
             * @param {object} rejection - the rejected promise
             * @description Display the error message depending on the error status and error code
             */
            responseError: function(rejection) {
                //user cancel the request : we do not show message
                if(rejection.config.timeout && rejection.config.timeout.$$state.value === 'user cancel') {
                    return $q.reject(rejection);
                }

                if(rejection.status === 0) {
                    MessageService.error('SERVER_ERROR_TITLE', 'SERVICE_UNAVAILABLE');
                }

                else if(rejection.status === 500) {
                    MessageService.error('SERVER_ERROR_TITLE', 'GENERIC_ERROR');
                }

                else if(rejection.data && rejection.data.code) {

                    if(rejection.data.code === 'TDP_API_DATASET_STILL_IN_USE') {
                        MessageService.error('SERVER_ERROR_TITLE', 'DELETE_DATASET_ERROR');
                    }
                    else if(rejection.data.code === 'TDP_PS_PREPARATION_STEP_CANNOT_BE_DELETED_IN_SINGLE_MODE') {
                        MessageService.error('PREPARATION_STEP_DELETE_MODE_NOT_AUTHORIZED_TITLE', 'PREPARATION_STEP_DELETE_MODE_NOT_AUTHORIZED');
                    }
                    else if(rejection.data.code === 'TDP_DSS_UNSUPPORTED_CONTENT') {
                        MessageService.error('UNSUPPORTED_CONTENT_TITLE', 'UNSUPPORTED_CONTENT');
                    }
                    else if(rejection.data.code === 'TDP_API_UNABLE_TO_CREATE_DATASET') {
                        MessageService.error('IMPORT_ERROR_TITLE', 'IMPORT_ERROR');
                    }
                    else if(rejection.data.code === 'TDP_API_UNABLE_TO_CREATE_OR_UPDATE_DATASET') {
                        MessageService.error('UPDATE_ERROR_TITLE', 'UPDATE_ERROR');
                    }
                    // TODO remove the or if changed in GenericCommand is not approved
                    else if(rejection.data.code === 'TDP_API_UNABLE_TO_COPY_DATASET_CONTENT' || rejection.data.code === 'TDP_DSS_DATASET_NAME_ALREADY_USED'){
                        MessageService.error('DATASET_NAME_ALREADY_USED_TITLE', 'DATASET_NAME_ALREADY_USED');
                    }
                    else {
                        MessageService.error('SERVER_ERROR_TITLE', 'GENERIC_ERROR');
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