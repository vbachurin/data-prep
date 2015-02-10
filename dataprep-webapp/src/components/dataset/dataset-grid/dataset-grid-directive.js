(function () {
    'use strict';

    function DatasetGrid() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/dataset-grid/dataset-grid-directive.html',
            scope: {
                metadata: '=',
                data: '='
            }
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetGrid', DatasetGrid);
})();

