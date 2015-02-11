(function () {
    'use strict';

    function DatasetGrid() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/dataset-grid/dataset-grid-directive.html',
            scope: {
                metadata: '=',
                data: '='
            },
            bindToController: true,
            controllerAs: 'datagridCtrl',
            controller: function() {}
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetGrid', DatasetGrid);
})();

