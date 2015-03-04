(function () {
    'use strict';

    function DatasetColumnHeader($timeout) {
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

                    $timeout(function() {
                        var headerDropdownAction = iElement.find('#header-dropdown-action').eq(0);
                        scope.$watch(
                            function () {
                                return ctrl.transformationsRetrieveError;
                            },
                            function (newValue) {
                                if (newValue) {
                                    headerDropdownAction.click();
                                }
                            });
                    });
                }
            }
        };
    }

    angular.module('data-prep-dataset')
        .directive('datasetColumnHeader', DatasetColumnHeader);
})();
