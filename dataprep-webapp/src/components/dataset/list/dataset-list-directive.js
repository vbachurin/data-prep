(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.dataset-list.directive:DatasetList
     * @description This directive display the dataset list from {@link data-prep.services.dataset.service:DatasetService DatasetService}
     * @requires data-prep.dataset-list.controller:DatasetListCtrl
     * @restrict E
     */
    function DatasetList() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/list/dataset-list.html',
            replace:true,
            bindToController: true,
            controllerAs: 'datasetListCtrl',
            controller: 'DatasetListCtrl',
            link: function() {
                //Resize datasets list : 100% viewport minus heights (headers + footer + sort header + uploading list + margin)
                var heightpanelNew = 'calc(100vh - 2 * 56px - 55px - 50px - ' + $('#flex-fixed-upload-list').height() + 'px)';
                $('#datasets-list').css('flex-basis', heightpanelNew);
            }
        };
    }

    angular.module('data-prep.dataset-list')
        .directive('datasetList', DatasetList);
})();