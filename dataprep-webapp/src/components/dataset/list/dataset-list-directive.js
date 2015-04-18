(function() {
    'use strict';

    function DatasetList() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/list/dataset-list.html',
            bindToController: true,
            controllerAs: 'datasetListCtrl',
            controller: 'DatasetListCtrl'
        };
    }

    angular.module('data-prep.dataset-list')
        .directive('datasetList', DatasetList);
})();