(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.utils.service:ConfigService
     * @description App Configuration service. It allows to init the service api configuration
     */
    function ConfigService($http, RestURLs) {
        /**
         * @ngdoc method
         * @name init
         * @methodOf data-prep.services.utils.service:ConfigService
         * @description Fetch the current front server configuration file and trigger the actual configuration
         */
        this.init = function init(config) {
            //return $http.get('/assets/config/config.json')
            //    .then(function(config) {
                    RestURLs.setServerUrl(config.serverUrl);
             //   });
        };
    }

    angular.module('data-prep.services.utils')
        .service('ConfigService', ConfigService);
})();