(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.datagrid.directive:Datagrid
     * @description This directive create the SlickGrid datagrid<br/>
     * Watchers :
     * <ul>
     *         <li>Update grid columns on backend column change</li>
     *         <li>Update data on backend value change</li>
     *         <li>Scroll to top when loaded dataset change</li>
     *         <li>When filter change, displayed values change, so we reset active cell and cell styles</li>
     * </ul>
     *
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.filter.service:FilterService
     * @requires data-prep.services.playground.service:PreviewService
     * @restrict E
     */
    function Datagrid($timeout, $compile, $window, DatagridService, FilterService, PreviewService) {
        return {
            restrict: 'E',
            templateUrl: 'components/datagrid/datagrid.html',
            bindToController: true,
            controllerAs: 'datagridCtrl',
            controller: 'DatagridCtrl',
            link: function (scope, iElement, iAttrs, ctrl) {
                var options, grid, colHeaderElements = [];

                // the tooltip ruler is used compute a cell text regardless of the font and zoom used.
                // To do so, the text is put into an invisible span so that the span can be measured.
                var tooltipRuler = angular.element('<span id="tooltip-ruler" style="display:none"></span>');
                iElement.append(tooltipRuler);

                //------------------------------------------------------------------------------------------------------
                //------------------------------------------------COL UTILES--------------------------------------------
                //------------------------------------------------------------------------------------------------------

                /**
                 * @ngdoc method
                 * @name resetColumnsClass
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Reset columns class
                 */
                var resetColumnsClass = function() {
                    _.forEach(grid.getColumns(), function(column) {
                        column.cssClass = null;
                    });
                };

                /**
                 * @ngdoc method
                 * @name resetCellStyles
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Reset the cells css
                 */
                var resetCellStyles = function() {
                    grid.setCellCssStyles('highlight', {});
                };

                /**
                 * @ngdoc method
                 * @name formatter
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Value formatter used in SlickGrid column definition. This is called to get a cell formatted value
                 */
                var formatter = function(row, cell, value, columnDef, dataContext) {

                    var returnStr = value;

                    //hidden characters need to be shown
                    if(value && (/\s/.test(value.charAt(0)) || /\s/.test(value.charAt(value.length-1))))  {
                        var hiddenCharsRegExpMatch = value.match(/(^\s\s*)?(\S*)(\s\s*$)?/);
                        if (hiddenCharsRegExpMatch[1]){
                            returnStr = '<span class="hiddenChars">' + hiddenCharsRegExpMatch[1] + '</span>' + hiddenCharsRegExpMatch[2];
                        }else{
                            returnStr = hiddenCharsRegExpMatch[2] ;
                        }
                        if (hiddenCharsRegExpMatch[3]){
                            returnStr += '<span class="hiddenChars">' + hiddenCharsRegExpMatch[3] + '</span>';
                        }
                    }
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

                    //no preview
                    return returnStr;
                };

                /**
                 * @ngdoc method
                 * @name columnItem
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @param {object} col The backend column to adapt
                 * @param {number} index Column index
                 * @param {boolean} preview Preview flag
                 * @description [PRIVATE] Adapt backend column to slick column. The name with div id depending on index
                 * is important. It is used to insert column header dropdown and quality bar.
                 * For preview, we inject directly a fake header
                 * @returns {object} - the adapted column item
                 */
                var columnItem = function (col, index, preview) {
                    var template;
                    if(preview) {

                        var diffClass = '';
                        if (col.__tdpColumnDiff === 'new') {
                            diffClass = 'newColumn';
                        }
                        else if (col.__tdpColumnDiff === 'delete') {
                            diffClass = 'deletedColumn';
                        }

                        template = '<div class="grid-header '+ diffClass +'">' +
                            '<div class="grid-header-title dropdown-button ng-binding">' + col.name + '</div>' +
                            '<div class="grid-header-type ng-binding">' + col.type + '</div>' +
                            '</div>' +
                            '<div class="quality-bar"><div  class="record-ok" style="width: 100%;"></div></div>';
                    }
                    else {
                        var divId = 'datagrid-header-' + index;
                        template = '<div id="' + divId + '"></div>';
                    }

                    var colItem = {
                        id: col.id,
                        field: col.id,
                        name: template,
                        formatter: formatter
                    };
                    return colItem;
                };

                /**
                 * @ngdoc method
                 * @name insertDatasetHeaders
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Create and insert the dataset column headers (dropdown actions and quality bars).
                 The columns are from {@link data-prep.services.playground.service:DatagridService DatagridService}
                 */
                var insertDatagridHeaders = function () {
                    _.forEach(DatagridService.data.columns, function (col, index) {
                        var header = createHeader(col);
                        iElement.find('#datagrid-header-' + index).eq(0).append(header.element);

                        colHeaderElements.push(header);
                    });
                };

                /**
                 * @ngdoc method
                 * @name createHeader
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Create a column header object containing element, scope and column
                 */
                var createHeader = function(col) {
                    var headerScope = scope.$new(true);
                    headerScope.columns = col;
                    var headerElement = angular.element('<datagrid-header column="columns"></datagrid-header>');
                    $compile(headerElement)(headerScope);

                    return {
                        element : headerElement,
                        scope : headerScope,
                        column: col
                    };
                };

                /**
                 * @ngdoc method
                 * @name clearHeaders
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Remove header elements.
                 */
                var clearHeaders = function () {
                    _.forEach(colHeaderElements, function (header) {
                        header.scope.$destroy();
                        header.element.remove();
                    });
                    colHeaderElements = [];
                };

                /**
                 * @ngdoc method
                 * @name updateColSelection
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @param {string} column - the selected column
                 * @description [PRIVATE] Set the selected column into service. This will trigger actions that use this property
                 */
                var updateColSelection = function (column) {
                    $timeout(function() {
                        DatagridService.setSelectedColumn(column.id);
                    });
                };

                //------------------------------------------------------------------------------------------------------
                //-------------------------------------------------LISTENERS--------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * @ngdoc method
                 * @name attachLongTableListeners
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Attach listeners for big table row management
                 */
                var attachLongTableListeners = function() {
                    DatagridService.dataView.onRowCountChanged.subscribe(function () {
                        grid.updateRowCount();
                        grid.render();
                    });
                    DatagridService.dataView.onRowsChanged.subscribe(function (e, args) {
                        grid.invalidateRows(args.rows);
                        grid.render();
                    });
                };

                /**
                 * @ngdoc method
                 * @name attachColumnHeaderListeners
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Attach listeners for custom directives management in headers
                 */
                var attachColumnHeaderListeners = function() {
                    //destroy old elements and insert compiled column header directives
                    grid.onColumnsReordered.subscribe(function () {
                        clearHeaders();
                        insertDatagridHeaders();
                    });

                    //change column background and update column profil on click
                    grid.onHeaderClick.subscribe(function(e, args) {
                        var columnId = args.column.id;
                        var column = _.find(grid.getColumns(), function(column) {
                            return column.id === columnId;
                        });

                        if(column.cssClass !== 'selected') {
                            resetCellStyles();
                            resetColumnsClass();
                            column.cssClass = 'selected';
                            grid.invalidate();

                            updateColSelection(column);
                        }
                    });
                };

                /**
                 * @ngdoc method
                 * @name attachTooltipListener
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Attach cell hover for tooltips listeners
                 */
                var attachTooltipListener = function() {
                    //show tooltips only if not empty and width is bigger than cell
                    function shouldShowTooltip(item, column) {
                        var toolTipText = item[column.id];
                        if(toolTipText === '') {
                            return false;
                        }

                        tooltipRuler.text(toolTipText);
                        return column.width <= tooltipRuler.width();
                    }

                    //show tooltip on hover
                    grid.onMouseEnter.subscribe(function(e) {
                        var cell = grid.getCellFromEvent(e);
                        var row = cell.row;
                        var column = grid.getColumns()[cell.cell];
                        var item = DatagridService.dataView.getItem(row);

                        if (!shouldShowTooltip(item, column)) {
                            return;
                        }
                        
                        var position = {
                            x: e.clientX,
                            y: e.clientY
                        };

                        ctrl.updateTooltip(item, column.id, position);
                    });
                    //hide tooltip on leave
                    grid.onMouseLeave.subscribe(function() {
                        ctrl.hideTooltip();
                    });
                };

                /**
                 * @ngdoc method
                 * @name attachCellListeners
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Attach cell action listeners (click, active change, ...)
                 */
                var attachCellListeners = function() {
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

                        updateColSelection(column);
                    });

                    //change selected cell column background
                    grid.onActiveCellChanged.subscribe(function(e,args) {
                        if(angular.isDefined(args.cell)) {
                            var column = grid.getColumns()[args.cell];

                            if(column.cssClass !== 'selected') {
                                resetColumnsClass();
                                column.cssClass = 'selected';
                                grid.invalidate();
                            }

                        }
                    });

                };

                var attachGridMove = function() {
                    grid.onScroll.subscribe(function() {
                        PreviewService.gridRangeIndex = grid.getRenderedRange();
                    });

                    $window.addEventListener('resize', function(){
                        grid.resizeCanvas();
                    }, true);
                };

                //------------------------------------------------------------------------------------------------------
                //---------------------------------------------------INIT-----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * @ngdoc method
                 * @name initGridIfNeeded
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Init Slick grid and attach listeners on dataview and grid.
                 The dataview is initiated and held by {@link data-prep.services.playground.service:DatagridService DatagridService}
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
                    grid = new Slick.Grid('#datagrid', DatagridService.dataView, [], options);

                    //listeners
                    attachLongTableListeners();
                    attachColumnHeaderListeners();
                    attachCellListeners();
                    attachTooltipListener();
                    attachGridMove();
                };

                //------------------------------------------------------------------------------------------------------
                //--------------------------------------------------UPDATE----------------------------------------------
                //------------------------------------------------------------------------------------------------------
                /**
                 * @ngdoc method
                 * @name updateColumns
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @param {object[]} dataCols Columns details
                 * @param {boolean} preview Preview flag
                 * @description [PRIVATE] Two modes :
                 * - Preview : we save actual headers and simulate fake headers with the new preview columns
                 * - Classic : we map each column to a header. This header can be a reused header if the column was
                 * the same as before, or a new created one otherwise.
                 */
                var updateColumns = function (dataCols, preview) {
                    //save current headers elements
                    _.forEach(colHeaderElements, function(header) {
                        header.element.detach();
                    });

                    //create and set new SlickGrid columns
                    var columns = _.map(dataCols, function (col, index) {
                        return columnItem(col, index, preview);
                    });
                    grid.setColumns(columns);

                    //insert reused or created datagrid headers
                    if(!preview) {
                        //map every column to a header
                        var finalColHeaderElements = _.map(dataCols, function (col, index) {
                            //find saved header corresponding to column
                            var header = _.find(colHeaderElements, function (colHeader) {
                                return colHeader.column === col;
                            });
                            //or create a new one if no corresponding one
                            if (!header) {
                                header = createHeader(col);
                            }

                            //attach header element to SlickGrid column
                            iElement.find('#datagrid-header-' + index).eq(0).append(header.element);
                            return header;
                        });

                        //destroy the unused headers
                        var diff = _.difference(colHeaderElements, finalColHeaderElements);
                        _.forEach(diff, function (header) {
                            header.scope.$destroy();
                            header.element.remove();
                        });
                        colHeaderElements = finalColHeaderElements;
                    }
                };

                /**
                 * @ngdoc method
                 * @name updateData
                 * @methodOf data-prep.datagrid.directive:Datagrid
                 * @description [PRIVATE] Reset the cell styles and re render the grid
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
                        return DatagridService.data ? DatagridService.data.columns : null;
                    },
                    function (cols) {
                        if (cols) {
                            initGridIfNeeded();
                            updateColumns(cols, DatagridService.data.preview);
                            grid.autosizeColumns();
                        }
                    }
                );

                /**
                 * Update data on backend value change
                 */
                scope.$watch(
                    function () {
                        return DatagridService.data ? DatagridService.data.records : null;
                    },
                    function (records) {
                        if(records) {
                            initGridIfNeeded();
                            updateData();
                        }
                    }
                );

                /**
                 * Scroll to top when loaded dataset change
                 */
                scope.$watch(
                    function () {
                        return DatagridService.metadata;
                    },
                    function (metadata) {
                        if(metadata) {
                            grid.scrollRowToTop(0);
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
                            grid.scrollRowToTop(0);
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

