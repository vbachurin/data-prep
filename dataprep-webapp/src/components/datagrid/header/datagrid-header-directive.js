(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.datagrid-header.directive:DatagridHeader
     * @description This directive takes care of a datagrid header item. It creates the header dropdown menu.
     * On creation, it calculate the quality bar values.
     * On double-click, it allows to rename the column name.
     *
     * Watchers:
     * <ul>
     *    <li>Close transformation menu on retrieve error, base on {@link data-prep.datagrid-header.controller:DatagridHeaderCtrl controller}.transformationsRetrieveError flag</li>
     * </ul>
     *
     * @restrict E
     * @usage
     <datagrid-header
            column="column">
     </datagrid-header>
     * @param {object} column The column metadata
     */
    function DatagridHeader($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/datagrid/header/datagrid-header.html',
            scope: {
                column: '='
            },
            bindToController: true,

            controllerAs: 'datagridHeaderCtrl',
            controller: 'DatagridHeaderCtrl',
            link: {
                post: function (scope, iElement, iAttrs, ctrl) {

                    /**
                     * The double-click on the column header is managed in datagrid-header-directive.js
                     * whereas the click is managed in widget-dropdown-directive.js
                     */
                    $timeout(function() {

                        var gridHeaderTitle = iElement.find('.grid-header-title');
                        var gridHeaderTitleInput = null;

                        var updateVal = function () {

                            gridHeaderTitleInput = iElement.find('.grid-header-title-input').eq(0);

                            //Manage ENTER
                            gridHeaderTitleInput
                                .keyup(function (event) {
                                        if (event.keyCode === 13) {
                                            if(ctrl.updateEnabled) {
                                                ctrl.updateColumnName(ctrl.column);
                                            } else {
                                                $timeout(function() {
                                                    ctrl.setEditMode(false);
                                                });
                                                event.stopPropagation();
                                                gridHeaderTitleInput.off('blur');
                                            }
                                        }
                                });

                            //Manage ESC
                            gridHeaderTitleInput
                                .keydown(function (event) {
                                    if (event.keyCode === 27) {
                                        $timeout(function() {
                                            ctrl.setEditMode(false);
                                        });
                                        event.stopPropagation();
                                        gridHeaderTitleInput.off('blur');
                                    }
                                });

                            //Manage Click on input
                            gridHeaderTitleInput
                                .on('click', function(e){
                                    e.preventDefault();  //cancel system double-click event
                                    e.stopPropagation();
                                });

                            //Manage Unfocus Input
                            gridHeaderTitleInput
                                .on('blur', function () {
                                    if(ctrl.updateEnabled) {
                                        ctrl.updateColumnName(ctrl.column);
                                    } else {
                                        $timeout(function() {
                                            ctrl.setEditMode(false);
                                        });
                                    }
                                });
                        };


                        //Detect the double click
                        var detectClickAction = function () {

                            $timeout(function() {
                                ctrl.setEditMode(true);
                            });

                            $timeout(function() {
                                updateVal();
                            });

                            $timeout(function() {
                                gridHeaderTitleInput.focus();
                                gridHeaderTitleInput.select();
                            }, 100);
                        };


                        //Bind dblclick event to 'gridHeaderTitle'
                        gridHeaderTitle.on('dblclick',detectClickAction);

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
