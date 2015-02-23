(function() {
    'use strict';

    function DatasetUploadList() {
        return {
            templateUrl: 'components/dataset/upload-list/dataset-upload-list-directive.html',
            restrict: 'E',
            scope: {
                datasets: '='
            },
            bindToController: true,
            controllerAs: 'uploadListCtrl',
            controller: function() {}
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetUploadList', DatasetUploadList);
})();