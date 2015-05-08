(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.dataset-preview.directive:DatasetPreview
     * @description This directive display the dataset preview from {@link data-prep.services.dataset.service:DatasetService DatasetService}
     * @requires data-prep.dataset-preview.controller:DatasetPreviewCtrl
     * @restrict E
     */
    function DatasetPreview() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/preview/dataset-preview-xls.html',
            bindToController: true,
            controllerAs: 'datasetPreviewCtrl',
            controller: 'DatasetPreviewCtrl'
        };
    }

    angular.module('data-prep.dataset-preview')
        .directive('datasetPreview', DatasetPreview);
})();