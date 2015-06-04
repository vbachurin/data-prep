(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.dataset-preview.directive:Preview
     * @description This directive display the sheet choice and previews on sheet select
     * @requires data-prep.dataset-preview.controller:DatasetXlsPreviewCtrl
     * @restrict E
     * @param {object} metadata The dataset metadata to preview
     * @param {boolean} state The popup state
     */
    function DatasetXlsPreview() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/preview/dataset-xls-preview.html',
            bindToController: true,
            controllerAs: 'previewCtrl',
            controller: 'DatasetXlsPreviewCtrl'
        };
    }

    angular.module('data-prep.dataset-xls-preview')
        .directive('datasetXlsPreview', DatasetXlsPreview);
})();