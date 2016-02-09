/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

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