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
                    var gridHeaderTitle, gridHeaderTitleInput;

                    /**
                     * @ngdoc method
                     * @name setEditionMode
                     * @methodOf data-prep.datagrid-header.directive:DatagridHeader
                     * @param {boolean} value The new edition mode value
                     * @description Set edition mode to the provided value. This trigger a $digest.
                     */
                    function setEditionMode(value) {
                        ctrl.setEditMode(value);
                        scope.$digest();
                    }

                    /**
                     * @ngdoc method
                     * @name setEditionMode
                     * @methodOf data-prep.datagrid-header.directive:DatagridHeader
                     * @description Update column name if it has changed, just toggle edition mode otherwise
                     */
                    function executeRenameAction() {
                        if (ctrl.nameHasChanged()) {
                            ctrl.updateColumnName();
                        }
                        else {
                            ctrl.resetColumnName();
                            setEditionMode(false);
                        }
                    }

                    /**
                     * @ngdoc method
                     * @name attachBlurListener
                     * @methodOf data-prep.datagrid-header.directive:DatagridHeader
                     * @description Attach a 'Blur' event listener on input. It executes the column rename.
                     */
                    function attachBlurListener() {
                        gridHeaderTitleInput.on('blur', function () {
                            executeRenameAction();
                        });
                    }

                    /**
                     * @ngdoc method
                     * @name attachKeyListener
                     * @methodOf data-prep.datagrid-header.directive:DatagridHeader
                     * @description Attach a 'Keydown' event listener on input. It handles the ENTER and ESC button
                     */
                    function attachKeyListener() {
                        gridHeaderTitleInput
                            .keydown(function (event) {
                                event.stopPropagation();
                                switch (event.keyCode) {
                                    case 13 : //ENTER
                                        gridHeaderTitleInput.blur();
                                        break;
                                    case 27 : //ESC
                                        ctrl.resetColumnName();
                                        gridHeaderTitleInput.blur();
                                        break;
                                }
                            });
                    }

                    /**
                     * @ngdoc method
                     * @name attachDisableInputClick
                     * @methodOf data-prep.datagrid-header.directive:DatagridHeader
                     * @description Disable all click in the input to prevent header and dropdown actions
                     */
                    function attachDisableInputClick() {
                        gridHeaderTitleInput.on('click', function (e) {
                            e.preventDefault();
                            e.stopPropagation();
                        });
                    }

                    /**
                     * @ngdoc method
                     * @name attachDblClickListener
                     * @methodOf data-prep.datagrid-header.directive:DatagridHeader
                     * @description Attach a 'DblClick' event listener on title. It toggle edition mode and focus/select input text
                     */
                    function attachDblClickListener() {
                        gridHeaderTitle.on('dblclick', function () {
                            setEditionMode(true);

                            setTimeout(function () {
                                gridHeaderTitleInput.focus();
                                gridHeaderTitleInput.select();
                            }, 100);
                        });
                    }


                    /**
                     * Mousedown : stop propagation not to hide dropdown and hide/show menu on right click
                     */
                    iElement.find('.grid-header').mousedown(function(event) {
                        switch (event.which) {
                            case 1:
                                angular.element('.dropdown-menu').removeClass('show-menu');
                                break;
                            case 3:
                                event.stopPropagation();
                                iElement.find('.grid-header').find('.dropdown-action').click();
                                break;
                            default:
                        }
                    });

                    /**
                     * Get the title and input elements, attach their listeners
                     */
                    $timeout(function () {
                        gridHeaderTitle = iElement.find('.grid-header-title');
                        gridHeaderTitleInput = iElement.find('.grid-header-title-input').eq(0);

                        attachKeyListener();
                        attachDblClickListener();
                        attachBlurListener();
                        attachDisableInputClick();
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

    angular.module('data-prep.datagrid-header')
        .directive('datagridHeader', DatagridHeader);
})();
