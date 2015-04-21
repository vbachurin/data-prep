(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.dataset-list.directive:DatasetList
     * @description Dataset list directive
     * @requires data-prep.dataset-list.controller:DatasetListCtrl
     * @restrict E
     */
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