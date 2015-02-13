(function () {
    'use strict';

    function DatasetGrid($rootScope) {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/dataset-grid/dataset-grid-directive.html',
            scope: {
                metadata: '=',
                data: '='
            },
            bindToController: true,
            controllerAs: 'datagridCtrl',
            controller: function() {
                var vm = this;

                $rootScope.$on('talend.dataset.transform', function(event, args) {
                    vm.data.records = args.data.records;
                });
            }
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetGrid', DatasetGrid);
})();

