(function () {
    'use strict';

    function DatasetGrid($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/dataset-grid/dataset-grid-directive.html',
            scope: {
                dataset: '=',
                data: '='
            },
            link: function (scope) {
                scope.$watch('data', function () {
                    if (scope.data) {
                        //$timeout(loadTableFeedbackStyles, 0, false);
                    }
                });
            }
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetGrid', DatasetGrid);
})();

