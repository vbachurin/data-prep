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
            controller: 'DatasetListCtrl'
        };
    }

    angular.module('data-prep.dataset-list')
        .directive('datasetList', DatasetList);
})();