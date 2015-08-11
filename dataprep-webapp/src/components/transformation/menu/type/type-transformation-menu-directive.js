(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.transformation-menu.directive:TypeTransformMenu
     * @description This directive display the type transformation menu
     * @restrict E
     * @usage
     <type-transform-menu
     column="column">
     </type-transform-menu>
     * @param {object} column The target column of this transformation menu item
     */
    function TypeTransformMenu() {
        return {
            restrict: 'E',
            templateUrl: 'components/transformation/menu/type/type-transformation-menu.html',
            scope: {
                column: '='
            },
            bindToController: true,
            controllerAs: 'typeMenuCtrl',
            controller: 'TypeTransformMenuCtrl',
            link: function (scope, iElement, iAttrs, ctrl) {
                scope.$watch(
                    function () {
                        return ctrl.column;
                    },
                    ctrl.adaptDomains
                );
            }
        };
    }

    angular.module('data-prep.type-transformation-menu')
        .directive('typeTransformMenu', TypeTransformMenu);
})();