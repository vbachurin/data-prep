(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.datagrid.service:DatagridStyleService
     * @description Datagrid private service that manage the grid style
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.utils.service:ConverterService
     * @requires data-prep.services.utils.service:TextFormatService
     */
    function DatagridStyleService(DatagridService, ConverterService, TextFormatService) {
        var grid;
        var lastSelectedColumnId;

        return {
            init: init,
            resetCellStyles : resetCellStyles,
            resetColumnStyles : resetColumnStyles,
            selectedColumn : selectedColumn,
            manageColumnStyle: manageColumnStyle,
            columnFormatter: columnFormatter,
            getColumnPreviewStyle: getColumnPreviewStyle
        };

        //--------------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name resetCellStyles
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Reset the cells css
         */
        function resetCellStyles() {
            grid.resetActiveCell();
            grid.setCellCssStyles('highlight', {});
        }

        /**
         * @ngdoc method
         * @name resetColumnStyles
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Reset the columns css.
         * Currently there is only one visual style class : selected. We reset the lastSelected
         */
        function resetColumnStyles() {
            lastSelectedColumnId = null;
        }

        /**
         * @ngdoc method
         * @name selectedColumn
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description returns the selected column object (the one from provided array with the selected id)
         * @param {array} columns The grid columns
         * @return {object}
         */
        function selectedColumn(columns) {
            if(lastSelectedColumnId) {
                return _.find(columns, function(column) {
                    return column.id === lastSelectedColumnId;
                });
            }
        }

        /**
         * @ngdoc method
         * @name addClass
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Add a css class to a column
         * @param {object} column The target column
         * @param {string} newClass The class to add
         */
        function addClass(column, newClass) {
            column.cssClass = (column.cssClass || '') + ' ' + newClass;
        }

        /**
         * @ngdoc method
         * @name updateSelectionClass
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Add 'selected' class if the column is the selected one
         * @param {object} column The target column
         * @param {object} selectedCol The selected column
         */
        function updateSelectionClass(column, selectedCol) {
            if(column === selectedCol) {
                addClass(column, 'selected');
            }
        }

        /**
         * @ngdoc method
         * @name updateNumbersClass
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Add the 'number' class to the column if its type is a number type
         * @param {object} column the target column
         */
        function updateNumbersClass(column){
            var simplifiedType = ConverterService.simplifyType(column.tdpColMetadata.type);
            if (simplifiedType === 'number'){
                addClass(column, 'numbers');
            }
        }

        /**
         * @ngdoc method
         * @name updateColumnClass
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Set style classes on columns depending on its state (type, selection, ...)
         * @param {object} columns The columns array
         * @param {object} selectedCol The grid selected column
         */
        function updateColumnClass(columns, selectedCol) {
            _.forEach(columns, function(column) {
                if(column.field === 'tdpId'){
                    column.cssClass = 'index-column';
                } else {
                    column.cssClass = null;
                    updateSelectionClass(column, selectedCol);
                    updateNumbersClass(column);
                }

            });

            if(selectedCol) {
                lastSelectedColumnId = selectedCol.id;
            }
        }

        /**
         * @ngdoc method
         * @name manageColumnStyle
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * * @param {object} columns The columns array
         * @param {boolean} isPreview Flag that indicate if the data IS in preview mode
         * @description Update column style classes accordingly to the active cell.
         * This is usefull when data changes, the column style is reset but the active cell does not change.
         */
        function manageColumnStyle(columns, isPreview) {
            var selectedColumn;

            if(!isPreview) {
                var activeCell = grid.getActiveCell();
                if(activeCell) {
                    selectedColumn = columns[activeCell.cell];
                }
                else if(lastSelectedColumnId) {
                    selectedColumn = _.find(columns, function(col) {
                        return col.id === lastSelectedColumnId;
                    });
                }
            }

            updateColumnClass(columns, selectedColumn);
        }

        /**
         * @ngdoc method
         * @name columnFormatter
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Value formatter used in SlickGrid column definition. This is called to get a cell formatted value
         * @param {object} col The column to format
         */
        function columnFormatter(col) {

            var invalidValues = col.quality.invalidValues;
            var isInvalid = function isInvalid(value) {
                return invalidValues.indexOf(value) >= 0;
            };

            return function formatter(row, cell, value, columnDef, dataContext) {
                //hidden characters need to be shown
                var returnStr = TextFormatService.computeHTMLForLeadingOrTrailingHiddenChars(value);

                //entire row modification preview
                switch(dataContext.__tdpRowDiff) {
                    case 'delete': return '<div class="cellDeletedValue">' + (returnStr ? returnStr : ' ') + '</div>';
                    case 'new': return '<div class="cellNewValue">' + (returnStr ? returnStr : ' ') + '</div>';
                }

                //cell modification preview
                if(dataContext.__tdpDiff && dataContext.__tdpDiff[columnDef.id]){
                    switch(dataContext.__tdpDiff[columnDef.id]) {
                        case 'update': return '<div class="cellUpdateValue">' + returnStr + '</div>';
                        case 'new': return '<div class="cellNewValue">' + returnStr + '</div>';
                        case 'delete': return '<div class="cellDeletedValue">' + (returnStr ? returnStr : ' ') + '</div>';
                    }
                }

                return returnStr + (isInvalid(returnStr) ? '<div title="Invalid Value" class="red-rect"></div>' : '<div class="invisible-rect"></div>');
            };
        }

        /**
         * @ngdoc method
         * @name getColumnPreviewStyle
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Get the column header preview style
         * @param {object} col The column metadata
         */
        function getColumnPreviewStyle(col) {
            switch (col.__tdpColumnDiff) {
                case 'new':     return 'newColumn';
                case 'delete':  return 'deletedColumn';
                case 'update':  return 'updatedColumn';
                default:        return '';
            }
        }

        /**
         * @ngdoc method
         * @name attachColumnHeaderCallback
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description attachColumnHeaderListeners callback
         */
        function attachColumnHeaderCallback(args) {
            resetCellStyles();
            updateColumnClass(grid.getColumns(), args.column);
            grid.invalidate();
        }


        /**
         * @ngdoc method
         * @name attachColumnHeaderListeners
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Attach style listener on headers. On header selection (on right click or left click) we update the column cells style
         */
        function attachColumnHeaderListeners() {
            grid.onHeaderContextMenu.subscribe(function(e, args) {
                attachColumnHeaderCallback (args);
            });

            grid.onHeaderClick.subscribe(function(e, args) {
                attachColumnHeaderCallback (args);
            });
        }

        function highlightCellsContaining(rowIndex, colIndex) {
            var column = grid.getColumns()[colIndex];
            var content = DatagridService.dataView.getItem(rowIndex)[column.id];

            var sameContentConfig = DatagridService.getSameContentConfig(column.id, content, 'highlight');
            grid.setCellCssStyles('highlight', sameContentConfig);
        }

        /**
         * @ngdoc method
         * @name attachCellListeners
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Attach cell action listeners (click, active change, ...)
         */
        function attachCellListeners() {
            //get clicked content and highlight cells in clicked column containing the content
            grid.onClick.subscribe(function (e,args) {
                setTimeout(highlightCellsContaining.bind(null, args.row, args.cell), 0);
            });

            //change selected cell column background
            grid.onActiveCellChanged.subscribe(function(e,args) {
                if(angular.isDefined(args.cell)) {
                    var columns = grid.getColumns();
                    var column = columns[args.cell];
                    updateColumnClass(columns, column);
                    grid.invalidate();
                }
            });
        }

        /**
         * @ngdoc method
         * @name init
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @param {object} newGrid The new grid
         * @description Initialize the grid and attach the style listeners
         */
        function init(newGrid) {
            grid = newGrid;
            attachColumnHeaderListeners();
            attachCellListeners();
        }
    }

    angular.module('data-prep.datagrid')
        .service('DatagridStyleService', DatagridStyleService);
})();