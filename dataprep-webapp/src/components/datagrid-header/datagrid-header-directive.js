(function () {
    'use strict';

    function DatagridHeader() {
        return {
            restrict: 'E',
            templateUrl: 'components/datagrid-header/datagrid-header.html',
            scope: {
                metadata: '=',
                column: '='
            },
            bindToController: true,
            controllerAs: 'datagridHeaderCtrl',
            controller: 'DatagridHeaderCtrl',
            link: {
                post: function (scope, iElement, iAttrs, ctrl) {
                    ctrl.refreshQualityBar();

                    scope.$watch(
                        function () {
                            return ctrl.transformationsRetrieveError;
                        },
                        function (newValue) {
                            if (newValue) {
                                var headerDropdownAction = iElement.find('.dropdown-action').eq(0);
                                headerDropdownAction.click();
                            }
                        });

                    iElement.on('$destroy', function() {
                        scope.$destroy();
                    });
                }
            }
        };
    }

    angular.module('data-prep.datagrid-header')
        .directive('datagridHeader', DatagridHeader);
})();
