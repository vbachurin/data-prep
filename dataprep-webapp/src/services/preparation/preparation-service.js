(function() {
    'use strict';

    function PreparationService($http, RestURLs) {
        /**
         * Get All the user preparations
         * @returns Promise
         */
        this.getPreparations = function() {
            return $http.get(RestURLs.preparationUrl);
        };
    }

    angular.module('data-prep.services.preparation')
        .service('PreparationService', PreparationService);
})();