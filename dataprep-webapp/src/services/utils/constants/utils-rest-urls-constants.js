(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.utils.service:RestURLs
     * @description The REST api services url
     */
    angular.module('data-prep.services.utils')
        .service('RestURLs', ['apiUrl', function(apiUrl) {
            return {
                serverUrl:				apiUrl,
                datasetUrl:             apiUrl + '/api/datasets',
                transformUrl:           apiUrl + '/api/transform',
                preparationUrl:         apiUrl + '/api/preparations',
                previewUrl:             apiUrl + '/api/transform/preview'
            };
        }]);
})();