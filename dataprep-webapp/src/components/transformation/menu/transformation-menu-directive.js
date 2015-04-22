(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-menu.directive:TransformMenu
     * @description This directive display a transformation menu item, and create the parameters form if needed
     * @restrict E
     * @usage
     <transform-menu
         metadata="metadata"
         column="column"
         menu="menu">
     </transform-menu>
     * @param {object} metadata The loaded metadata
     * @param {object} column The target column of this transformation menu item
     * @param {object} menu The menu item to display
     */
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