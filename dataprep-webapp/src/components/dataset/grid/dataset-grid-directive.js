(function () {
    'use strict';

    function DatasetGrid() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/grid/dataset-grid-directive.html',
            bindToController: true,
            controllerAs: 'datagridCtrl',
            controller: 'DatasetGridCtrl'
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetGrid', DatasetGrid);
})();

