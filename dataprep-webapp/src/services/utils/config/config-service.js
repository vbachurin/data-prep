(function() {
    'use strict';

    function ConfigService($http, RestURLs) {
        this.init = function init() {
            return $http.get('/assets/config/config.json')
                .then(function(config) {
                    RestURLs.setServerUrl(config.data.serverUrl);
                });
        };
    }

    angular.module('data-prep.services.utils')
        .service('ConfigService', ConfigService);
})();