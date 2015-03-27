(function () {
    'use strict';

    function TransformationService($http, RestURLs) {

        this.getTransformations = function(datasetId, column) {
            var cleanDatasetId = encodeURIComponent(datasetId);
            var cleanColumn = encodeURIComponent(column);
            return $http.get(RestURLs.datasetUrl + '/' + cleanDatasetId + '/' + cleanColumn + '/actions');
        };
    }

    angular.module('data-prep.services.transformation')
        .service('TransformationService', TransformationService);
})();