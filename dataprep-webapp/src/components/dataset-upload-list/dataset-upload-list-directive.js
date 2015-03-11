(function() {
    'use strict';

    function DatasetUploadList() {
        return {
            templateUrl: 'components/dataset-upload-list/dataset-upload-list.html',
            restrict: 'E',
            scope: {
                datasets: '='
            },
            bindToController: true,
            controllerAs: 'uploadListCtrl',
            controller: function() {}
        };
    }

    angular.module('data-prep.dataset-upload-list')
        .directive('datasetUploadList', DatasetUploadList);
})();