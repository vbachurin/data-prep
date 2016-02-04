/**
 * @ngdoc directive
 * @name data-prep.transformation-menu.directive:TypeTransformMenu
 * @description This directive display the type transformation menu
 * @restrict E
 * @usage <type-transform-menu column="column"></type-transform-menu>
 * @param {object} column The target column of this transformation menu item
 */
export default function TypeTransformMenu() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/transformation/menu/type/type-transformation-menu.html',
        scope: {
            column: '='
        },
        bindToController: true,
        controllerAs: 'typeMenuCtrl',
        controller: 'TypeTransformMenuCtrl'
    };
}