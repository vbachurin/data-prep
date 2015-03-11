(function () {
    'use strict';

    function DatasetColumnHeader() {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/grid/header/dataset-column-header-directive.html',
            scope: {
                metadata: '=',
                column: '='
            },
            bindToController: true,
            controllerAs: 'datasetHeaderCtrl',
            controller: 'DatasetColumnHeaderCtrl',
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

    angular.module('data-prep-dataset')
        .directive('datasetColumnHeader', DatasetColumnHeader);
})();
