(function () {
    'use strict';

    function TransformMenu() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/menu/transformation-menu.html',
            replace: true,
            scope: {
                metadata: '=',
                column: '=',
                menu: '='
            },
            bindToController: true,
            controllerAs: 'menuCtrl',
            controller: 'TransformMenuCtrl'
        };
    }

    angular.module('data-prep.transformation-menu')
        .directive('transformMenu', TransformMenu);
})();