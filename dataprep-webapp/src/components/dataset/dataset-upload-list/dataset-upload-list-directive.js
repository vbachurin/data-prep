(function() {
    'use strict';

    function DatasetsUploadList() {
        return {
            templateUrl: 'components/dataset/dataset-upload-list/dataset-upload-list-directive.html',
            restrict: 'E',
            scope: {
                datasets: '='
            }
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetsUploadList', DatasetsUploadList);
})();