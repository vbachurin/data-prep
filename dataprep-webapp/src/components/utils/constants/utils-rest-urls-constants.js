(function() {
    'use strict';

    angular.module('data-prep-utils')
        .service('RestURLs', ['apiUrl', function(apiUrl) {
            return {
                serverUrl:				apiUrl,
                datasetUrl:             apiUrl + '/api/datasets',
                transformUrl:           apiUrl + '/api/transform'
            };
        }]);
})();