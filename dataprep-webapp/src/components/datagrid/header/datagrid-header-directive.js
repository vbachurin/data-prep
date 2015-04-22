(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.datagrid-header.directive:DatagridHeader
     * @description Datagrid header. On creation, refresh quality bar
     <pre><datagrid-header
                 metadata="metadata"
                 column="column"></datagrid-header>
     </pre>

     <table>
         <tr>
             <th>Attributes</th>
             <th>Description</th>
         </tr>
         <tr>
             <td>metadata</td>
             <td>the loaded metadata</td>
         </tr>
         <tr>
             <td>column</td>
             <td>the column metadata</td>
         </tr>
     </table>

     Watchers:
     <ul>
        <li>Close transformation menu on retrieve error, base on {@link data-prep.datagrid-header.controller:DatagridHeaderCtrl controller}.transformationsRetrieveError flag</li>
     </ul>

     * @restrict E
     */
    function DatagridHeader() {
        return {
            restrict: 'E',
            templateUrl: 'components/datagrid/header/datagrid-header.html',
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

                    /**
                     * Close transformation menu on retrieve error
                     */
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
