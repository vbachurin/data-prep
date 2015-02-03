(function() {
    'use strict';

    function DatasetsUploadList() {
        return {
            templateUrl: 'components/dataset-upload-list/dataset-upload-list-directive.html',
            restrict: 'E',
            scope: {
                datasets: '='
            }
        }
    }

    angular.module('data-prep')
        .directive('datasetsUploadList', DatasetsUploadList);
})();