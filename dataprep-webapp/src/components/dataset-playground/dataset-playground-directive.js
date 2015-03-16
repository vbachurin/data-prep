(function () {
    'use strict';

    function DatasetPlayground() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset-playground/dataset-playground.html',
            bindToController: true,
            controllerAs: 'datasetCtrl',
            controller: 'DatasetPlaygroundCtrl'
        };
    }

    angular.module('data-prep.dataset-playground')
        .directive('datasetPlayground', DatasetPlayground);
})();