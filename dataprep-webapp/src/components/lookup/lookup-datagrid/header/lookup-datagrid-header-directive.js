(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.lookup-datagrid-header.directive:DatagridHeader
     * @description This directive takes care of a lookup-datagrid header item. It creates the header dropdown menu.
     * On creation, it calculate the quality bar values.
     * On double-click, it allows to rename the column name.
     *
     * Watchers:
     * <ul>
     *    <li>Close transformation menu on retrieve error, base on {@link data-prep.lookup-datagrid-header.controller:DatagridHeaderCtrl controller}.transformationsRetrieveError flag</li>
     * </ul>
     *
     * @restrict E
     * @usage
     <lookup-datagrid-header
     column="column">
     </lookup-datagrid-header>
     * @param {object} column The column metadata
     */
    function LookupDatagridHeader($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/lookup/lookup-datagrid/header/lookup-datagrid-header.html',
            scope: {
                column: '='
            },
            bindToController: true,
            controllerAs: 'lookupDatagridHeaderCtrl',
            controller: 'LookupDatagridHeaderCtrl',
            link: {
                post: function (scope, iElement, iAttrs, ctrl) {
                    var gridHeader, gridHeaderTitle;

                    /**
                     * @ngdoc method
                     * @name attachClickListener
                     * @methodOf data-prep.lookup-datagrid-header.directive:DatagridHeader
                     * @description Attach a 'Click' event listener on grid header
                     */
                    function attachClickListener() {
                        gridHeader.mousedown(function(event) {
                            if (event.which === 3) { //Right click
                                //stop propagation not to hide dropdown and hide/show menu on right click
                                event.stopPropagation();
                                gridHeader.find('.dropdown-action').click();
                            }
                        });
                    }

                    /**
                     * Get the title and input elements, attach their listeners
                     */
                    $timeout(function () {
                        gridHeader= iElement.find('.grid-header').eq(0);
                        gridHeaderTitle = gridHeader.find('.grid-header-title').eq(0);

                        //attachKeyListener();
                        //attachDblClickListener();
                        //attachBlurListener();
                        //attachDisableInputClick();
                        attachClickListener();
                    });

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

                    iElement.on('$destroy', function () {
                        scope.$destroy();
                    });
                }
            }
        };
    }

    angular.module('data-prep.lookup-datagrid-header')
        .directive('lookupDatagridHeader', LookupDatagridHeader);
})();
