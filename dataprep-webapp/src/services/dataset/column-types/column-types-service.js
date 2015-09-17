(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:ColumnTypesService
     * @description Column types service
     */
    function ColumnTypesService($q, $http, RestURLs) {
        var types;

        /**
         * @ngdoc method
         * @name getTypes
         * @methodOf data-prep.services.dataset.service:ColumnTypesService
         * @description Return all primitive types
         * @returns {Promise} The GET promise
         */
        this.getTypes = function getTypes() {
            if (types) {
                return $q.when(types);
            }
            return $http.get(RestURLs.serverUrl + '/api/types').then(function (response) {
                types = [];
                // TDP-399 we limit numeric types to integer and float (displayed as decimal)
                _.forEach( response.data,function(type){
                    switch (type.id.toLowerCase()) {

                        case 'integer':
                            types.push(type);
                            break;
                        case 'float':
                            types.push(type);
                            break;
                        case 'boolean':
                            types.push(type);
                            break;
                        case 'string':
                        case 'char':
                            types.push(type);
                            break;
                        case 'date':
                            types.push(type);
                            break;
                        case 'double':
                        case 'numeric':
                        case 'any':
                        default:

                    }
                } );
                return types;
            });
        };
    }

    angular.module('data-prep.services.dataset')
        .service('ColumnTypesService', ColumnTypesService);
})();