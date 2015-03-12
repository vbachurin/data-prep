(function () {
    'use strict';

    function Datagrid($compile) {
        return {
            restrict: 'E',
            templateUrl: 'components/datagrid/datagrid.html',
            bindToController: true,
            controllerAs: 'datagridCtrl',
            controller: 'DatagridCtrl',
            link: function (scope, iElement, iAttrs, ctrl) {
                var options, dataView, grid, colHeaderElements = [];

                //------------------------------------------------------------------------------------------------------
                //------------------------------------------------COL UTILES--------------------------------------------
                //------------------------------------------------------------------------------------------------------

                /**
                 * Reset columns class
                 */
                var resetColumnsClass = function() {
                    _.forEach(grid.getColumns(), function(column) {
                        column.cssClass = null;
                    });
                };

                /**
                 * Reset columns formatter
                 */
                var resetColumnsFormatter = function() {
                    _.forEach(grid.getColumns(), function(column) {
                        column.formatter = null;
                    });
                };

                /**
                 * Reset the cells css
                 */
                var resetCellStyles = function() {
                    grid.setCellCssStyles('highlight', {});
                };

                /**
                 * Adapt backend column to slick column. The name with div id depending on index is important. It is used to insert column header dropdown and quality bar
                 * @param col - the backend column to adapt
                 * @param index - column index
                 * @returns {{id: *, field: *, name: string}}
                 */
                var columnItem = function (col, index) {
                    var divId = 'datagrid-header-' + index;
                    var colItem = {
                        id: col.id,
                        field: col.id,
                        name: '<div id="' + divId + '"></div>'
                    };

                    return colItem;
                };

                /**
                 * Insert the dataset headers (dropdown actions and quality bars)
                 */
                var insertDatasetHeaders = function () {
                    _.forEach(ctrl.data.columns, function (col, index) {
                        var headerScope = scope.$new(true);
                        headerScope.columns = col;
                        headerScope.metadata = ctrl.metadata;
                        var headerElement = angular.element('<datagrid-header column="columns" metadata="metadata"></datagrid-header>');
                        $compile(headerElement)(headerScope);

                        colHeaderElements.push(headerElement);
                        angular.element('#datagrid-header-' + index).append(headerElement);
                    });
                };

                /**
                 * Remove header elements
                 */
                var clearHeaders = function () {
                    _.forEach(colHeaderElements, function (element) {
                        element.remove();
                    });
                    colHeaderElements = [];
                };

                //------------------------------------------------------------------------------------------------------
                //-------------------------------------------------LISTENERS--------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * Attach listeners for big table row management
                 */
                var attachLongTableListeners = function() {
                    dataView.onRowCountChanged.subscribe(function () {
                        grid.updateRowCount();
                        grid.render();
                    });
                    dataView.onRowsChanged.subscribe(function (e, args) {
                        grid.invalidateRows(args.rows);
                        grid.render();
                    });
                };

                /**
                 * Attach listeners for custom directives management in headers
                 */
                var attachColumnHeaderListeners = function() {
                    //destroy old elements and insert compiled column header directives
                    grid.onColumnsReordered.subscribe(function () {
                        clearHeaders();
                        insertDatasetHeaders();
                    });
                };

                var attachCellListeners = function() {
                    //get clicked word and highlight cells in clicked column containing the word
                    grid.onClick.subscribe(function (e,args) {
                        resetColumnsFormatter();

                        var config = {};
                        var column = grid.getColumns()[args.cell];
                        var word = dataView.getItem(args.row)[column.id];

                        column.formatter = function(row, cell, value) {
                            if((word === '' && value === '') || (word !== '' && value.indexOf(word) > -1)) {
                                config[row] = {};
                                config[row][column.id] = 'highlight';
                            }
                            return value;
                        };
                        grid.setCellCssStyles('highlight', config);
                        grid.invalidate();
                    });

                    //change selected cell column background
                    grid.onActiveCellChanged.subscribe(function(e,args) {
                        if(angular.isDefined(args.cell)) {
                            resetColumnsClass();
                            grid.getColumns()[args.cell].cssClass = 'selected';
                            grid.invalidate();
                        }
                    });
                };

                //------------------------------------------------------------------------------------------------------
                //---------------------------------------------------INIT-----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * Init Slick grid and attach listeners on dataview and grid
                 */
                var initGrid = function () {
                    //options, dataview and grid
                    options = {
                        editable: false,
                        enableAddRow: false,
                        enableCellNavigation: true,
                        enableTextSelectionOnCells: true
                    };
                    dataView = new Slick.Data.DataView({inlineFilters: true});
                    grid = new Slick.Grid('#datagrid', dataView, [], options);

                    //listeners
                    attachLongTableListeners();
                    attachColumnHeaderListeners();
                    attachCellListeners();
                };

                //------------------------------------------------------------------------------------------------------
                //--------------------------------------------------UPDATE----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * Clear and update columns
                 * @param dataCols
                 */
                var updateColumns = function (dataCols) {
                    clearHeaders();

                    var columns = _.map(dataCols, function (col, index) {
                        return columnItem(col, index);
                    });
                    grid.setColumns(columns);

                    insertDatasetHeaders();
                };

                /**
                 * Update dataView items and render grid
                 * @param data
                 */
                var updateData = function (data) {
                    _.forEach(data, function(item, index) {
                        item.tdpId = index;
                    });

                    dataView.beginUpdate();
                    dataView.setItems(data, 'tdpId');
                    dataView.endUpdate();

                    resetCellStyles();
                    grid.resetActiveCell();
                    grid.invalidate();
                };

                //------------------------------------------------------------------------------------------------------
                //-------------------------------------------------WATCHERS---------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * Update grid columns on backend column change
                 */
                scope.$watch(
                    function () {
                        return ctrl.data ? ctrl.data.columns : null;
                    },
                    function (newValue) {
                        if (newValue) {
                            if (!grid) {
                                initGrid();
                            }
                            updateColumns(newValue);
                            grid.autosizeColumns();
                        }
                    }
                );

                /**
                 * Update data on backend value change
                 */
                scope.$watch(
                    function () {
                        return ctrl.data ? ctrl.data.records : null;
                    },
                    function (newValue) {
                        if (newValue) {
                            updateData(newValue);
                        }
                    }
                );

                /**
                 * Destroy scope on element destroy
                 */
                iElement.on('$destroy', function () {
                    scope.$destroy();
                });
            }
        };
    }

    angular.module('data-prep.datagrid')
        .directive('datagrid', Datagrid);
})();

