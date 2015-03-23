(function () {
    'use strict';

    function Datagrid($compile, DatasetGridService, FilterService) {
        return {
            restrict: 'E',
            template: '<div id="datagrid" class="datagrid"></div>',
            bindToController: true,
            controllerAs: 'datagridCtrl',
            controller: function() {},
            link: function (scope, iElement) {
                var options, grid, colHeaderElements = [];

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
                    _.forEach(DatasetGridService.data.columns, function (col, index) {
                        var headerScope = scope.$new(true);
                        headerScope.columns = col;
                        headerScope.metadata = DatasetGridService.metadata;
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
                    DatasetGridService.dataView.onRowCountChanged.subscribe(function () {
                        grid.updateRowCount();
                        grid.render();
                    });
                    DatasetGridService.dataView.onRowsChanged.subscribe(function (e, args) {
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

                /**
                 * Attach cell action listeners (click, active change, ...)
                 */
                var attachCellListeners = function() {
                    //get clicked content and highlight cells in clicked column containing the content
                    grid.onClick.subscribe(function (e,args) {
                        var config = {};
                        var column = grid.getColumns()[args.cell];
                        var content = DatasetGridService.dataView.getItem(args.row)[column.id];

                        var rowsContainingWord = DatasetGridService.getRowsContaining(column.id, content);
                        _.forEach(rowsContainingWord, function(rowId) {
                            config[rowId] = {};
                            config[rowId][column.id] = 'highlight';
                        });

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
                var initGridIfNeeded = function () {
                    if(grid) {
                        return;
                    }

                    options = {
                        editable: false,
                        enableAddRow: false,
                        enableCellNavigation: true,
                        enableTextSelectionOnCells: true
                    };
                    grid = new Slick.Grid('#datagrid', DatasetGridService.dataView, [], options);

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
                 * Render grid on dataView update
                 */
                var updateData = function () {
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
                        return DatasetGridService.data ? DatasetGridService.data.columns : null;
                    },
                    function (cols) {
                        if (cols) {
                            initGridIfNeeded();
                            updateColumns(cols);
                            grid.autosizeColumns();
                        }
                    }
                );

                /**
                 * Update data on backend value change
                 */
                scope.$watch(
                    function () {
                        return DatasetGridService.data ? DatasetGridService.data.records : null;
                    },
                    function (records) {
                        if(records) {
                            initGridIfNeeded();
                            updateData();
                        }
                    }
                );

                /**
                 * When filter change, displayed values change, so we reset active cell and cell styles
                 */
                scope.$watchCollection(
                    function () {
                        return FilterService.filters;
                    },
                    function () {
                        if(grid) {
                            resetCellStyles();
                            grid.resetActiveCell();
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

