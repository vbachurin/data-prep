(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.utils.service:TypesService
     * @description Informations on default types
     */
    function TypesService($http, RestURLs) {

        var self = this;
        self.types = {};

        /**
         * @ngdoc method
         * @name getTypes
         * @methodOf data-prep.services.transformation.service:TransformationRestService
         * @description return all primitive types managed by dataprep
         * @returns {HttpPromise} The GET promise
         */
        self.getTypes = function(){
            return $http.get(RestURLs.serverUrl + '/api/types');
        };


        self.types = self.getTypes();

    }

    angular.module('data-prep.services.utils')
        .service('TypesService', TypesService);
})();