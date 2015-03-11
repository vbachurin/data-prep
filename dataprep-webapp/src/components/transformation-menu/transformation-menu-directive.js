(function () {
    'use strict';

    function DatasetTransformMenu() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation-menu/transformation-menu.html',
            replace: true,
            scope: {
                metadata: '=',
                column: '=',
                menu: '='
            },
            bindToController: true,
            controllerAs: 'menuCtrl',
            controller: 'DatasetTransformMenuCtrl'
        };
    }

    angular.module('data-prep.transformation-menu')
        .directive('datasetTransformMenu', DatasetTransformMenu);
})();