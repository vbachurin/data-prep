/**
 * @ngdoc directive
 * @name data-prep.dataset-preview.directive:Preview
 * @description This directive display the sheet choice and previews on sheet select
 * @requires data-prep.dataset-preview.controller:DatasetXlsPreviewCtrl
 * @restrict E
 */
export default function DatasetXlsPreview() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/dataset/preview/dataset-xls-preview.html',
        bindToController: true,
        controllerAs: 'previewCtrl',
        controller: 'DatasetXlsPreviewCtrl'
    };
}