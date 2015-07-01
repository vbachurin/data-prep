(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.datagrid.service:DatagridStyleService
     * @description Datagrid private service that manage the grid style
     * @requires data-prep.services.playground.service:DatagridService
     */
    function DatagridStyleService(DatagridService, ConverterService) {
        var grid;

        return {
            init: init,
            resetCellStyles : resetCellStyles,
            manageColumnStyle: manageColumnStyle,
            computeHTMLForLeadingOrTrailingHiddenChars: computeHTMLForLeadingOrTrailingHiddenChars,
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
         * @param {object} selectedCol The grid selected column
         */
        function updateColumnClass(selectedCol) {
            _.forEach(grid.getColumns(), function(column) {
                column.cssClass = null;
                updateSelectionClass(column, selectedCol);
                updateNumbersClass(column);
            });
        }

        /**
         * @ngdoc method
         * @name manageColumnStyle
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @param {boolean} isPreview Flag that indicate if the data IS in preview mode
         * @description Update column style classes accordingly to the active cell.
         * This is usefull when data changes, the column style is reset but the active cell does not change.
         */
        function manageColumnStyle(isPreview) {
            var activeCell = grid.getActiveCell();
            if(!isPreview && activeCell) {
                var column = grid.getColumns()[activeCell.cell];
                updateColumnClass(column);
            }
            else {
                updateColumnClass(null);
            }
        }

        /**
         * @ngdoc method
         * @name computeHTMLForLeadingOrTrailingHiddenChars
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description split the string value into leading chars, text and trailing char and create html element using
         * the class hiddenChars to specify the hiddenChars.
         * @param {string} value The string value to adapt
         */
        function computeHTMLForLeadingOrTrailingHiddenChars(value){
            if(!value) {
                return value;
            }

            var returnStr = '';
            var hiddenCharsRegExpMatch = value.match(/(^\s*)?([\s\S]*?)(\s*$)/);

            //leading hidden chars found
            if (hiddenCharsRegExpMatch[1]){
                returnStr = '<span class="hiddenChars">' + hiddenCharsRegExpMatch[1] + '</span>';
            }

            returnStr += hiddenCharsRegExpMatch[2] ;

            //trailing hidden chars
            if (hiddenCharsRegExpMatch[3]){
                returnStr += '<span class="hiddenChars">' + hiddenCharsRegExpMatch[3] + '</span>';
            }

            return returnStr;
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
                var returnStr = computeHTMLForLeadingOrTrailingHiddenChars(value);

                //deleted row preview
                if(dataContext.__tdpRowDiff === 'delete') {
                    return '<div class="cellDeletedValue"><strike>' + (returnStr ? returnStr : ' ') + '</strike></div>';
                }
                //new row preview
                else if(dataContext.__tdpRowDiff === 'new') {
                    return '<div class="cellNewValue">' + (returnStr ? returnStr : ' ') + '</div>';
                }

                //updated cell preview
                if(dataContext.__tdpDiff){
                    // update
                    if (dataContext.__tdpDiff[columnDef.id] === 'update') {
                        return '<div class="cellUpdateValue">' + returnStr + '</div>';
                    }
                    // new
                    else if (dataContext.__tdpDiff[columnDef.id] === 'new') {
                        return '<div class="cellNewValue">' + returnStr + '</div>';
                    }
                    // new
                    else if (dataContext.__tdpDiff[columnDef.id] === 'delete') {
                        return '<div class="cellDeletedValue">' + (returnStr ? returnStr : ' ') + '</div>';
                    }
                }

                if(isInvalid(returnStr)){
                    return returnStr + '<div title="Invalid Value" class="red-rect"></div>';
                }
                else {
                    return returnStr + '<div class="invisible-rect"></div>';
                }

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
         * @name attachColumnHeaderListeners
         * @methodOf data-prep.datagrid.service:DatagridStyleService
         * @description Attach style listener on headers. On header selection we update the column cells style
         */
        function attachColumnHeaderListeners() {
            grid.onHeaderClick.subscribe(function(e, args) {
                resetCellStyles();
                updateColumnClass(args.column);
                grid.invalidate();
            });
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
                var config = {};
                var column = grid.getColumns()[args.cell];
                var content = DatagridService.dataView.getItem(args.row)[column.id];

                var rowsContainingWord = DatagridService.getRowsContaining(column.id, content);
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
                    var column = grid.getColumns()[args.cell];
                    updateColumnClass(column);
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
        .factory('DatagridStyleService', DatagridStyleService);
})();