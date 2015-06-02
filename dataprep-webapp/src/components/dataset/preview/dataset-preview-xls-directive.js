(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.dataset-preview.directive:DatasetPreviewXls
     * @description This directive display the dataset preview from {@link data-prep.services.dataset.service:DatasetService DatasetService}
     * @requires data-prep.dataset-preview.controller:DatasetXlsPreviewCtrl
     * @restrict E
     */
    function DatasetPreviewXls() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/preview/dataset-preview-xls.html',
            bindToController: true,
            controllerAs: 'datasetPreviewXlsCtrl',
            controller: 'DatasetPreviewXlsCtrl'
        };
    }

    angular.module('data-prep.dataset-preview-xls')
        .directive('datasetPreviewXls', DatasetPreviewXls);
})();