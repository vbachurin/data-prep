(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.dataset-upload-list.directive:DatasetUploadList
     * @description This dataset display the upload list that had an upload error. Unlike the dataset list which is
     * based on a service property for the dataset list, this one is based on a given attribute value.
     * @restrict E
     * @usage
     <dataset-upload-list
            datasets="datasets">
     </dataset-upload-list>
     * @param {object[]} datasets The upload error datasets
     */
    function DatasetUploadList($timeout) {
        return {
            templateUrl: 'components/dataset/upload-list/dataset-upload-list.html',
            restrict: 'E',
            scope: {
                datasets: '='
            },
            bindToController: true,
            controllerAs: 'uploadListCtrl',
            controller: function() {},
            link: function(scope, iElement, iAttrs, ctrl) {
                scope.$watch(function() {
                    return ctrl.datasets;
                }, function() {
                    //Resize datasets list
                    $timeout(function(){
                        var heightpanelNew = 'calc(100vh - 2 * 56px - 50px - 50px - ' + $('#flex-fixed-upload-list').height() + 'px)';
                        $('#datasets-list').css('flex-basis', heightpanelNew);
                    },200);
                }, true);
            }
        };
    }

    angular.module('data-prep.dataset-upload-list')
        .directive('datasetUploadList', DatasetUploadList);
})();