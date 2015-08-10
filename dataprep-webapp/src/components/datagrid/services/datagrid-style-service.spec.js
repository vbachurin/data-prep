describe('Datagrid style service', function () {
    'use strict';

    var gridMock, gridColumns;

    function assertColumnsHasNoStyles() {
        gridColumns.forEach(function(column) {
            expect(column.cssClass).toBeFalsy();
        });
    }

    beforeEach(module('data-prep.datagrid'));

    beforeEach(inject(function () {
        gridColumns = [
            {id: '0000', field: 'col0', tdpColMetadata: {id: '0000', name: 'col0', type: 'string'}},
            {id: '0001', field: 'col1', tdpColMetadata: {id: '0001', name: 'col1', type: 'integer'}},
            {id: '0002', field: 'col2', tdpColMetadata: {id: '0002', name: 'col2', type: 'string'}},
            {id: '0003', field: 'col3', tdpColMetadata: {id: '0003', name: 'col3', type: 'string'}},
            {id: '0004', field: 'col4', tdpColMetadata: {id: '0004', name: 'col4', type: 'string'}}
        ];

        /*global SlickGridMock:false */
        gridMock = new SlickGridMock();
        gridMock.initColumnsMock(gridColumns);

        spyOn(gridMock.onClick, 'subscribe').and.returnValue();
        spyOn(gridMock.onHeaderClick, 'subscribe').and.returnValue();
        spyOn(gridMock.onActiveCellChanged, 'subscribe').and.returnValue();
        spyOn(gridMock, 'resetActiveCell').and.returnValue();
        spyOn(gridMock, 'invalidate').and.returnValue();
    }));

    describe('on creation', function () {
        it('should add header click listener', inject(function (DatagridStyleService) {
            //when
            DatagridStyleService.init(gridMock);

            //then
            expect(gridMock.onHeaderClick.subscribe).toHaveBeenCalled();
        }));

        it('should add cell click listener', inject(function (DatagridStyleService) {
            //when
            DatagridStyleService.init(gridMock);

            //then
            expect(gridMock.onClick.subscribe).toHaveBeenCalled();
        }));

        it('should add active cell changed listener', inject(function (DatagridStyleService) {
            //when
            DatagridStyleService.init(gridMock);

            //then
            expect(gridMock.onActiveCellChanged.subscribe).toHaveBeenCalled();
        }));
    });

    describe('on header click event', function () {
        it('should set reset cell styles', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            gridMock.setCellCssStyles('highlight', {'2': {'0000': 'highlight'}});

            var args = {column: gridColumns[1]};

            //when
            var onHeaderClick = gridMock.onHeaderClick.subscribe.calls.argsFor(0)[0];
            onHeaderClick(null, args);

            //then
            expect(gridMock.resetActiveCell).toHaveBeenCalled();
            expect(gridMock.cssStyleConfig.highlight).toEqual({});
        }));

        it('should set selected column class', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var args = {column: gridColumns[1]};

            assertColumnsHasNoStyles();

            //when
            var onHeaderClick = gridMock.onHeaderClick.subscribe.calls.argsFor(0)[0];
            onHeaderClick(null, args);

            //then
            expect(gridColumns[0].cssClass).toBeFalsy();
            expect(gridColumns[1].cssClass.indexOf('selected') > -1).toBe(true);
            expect(gridColumns[2].cssClass).toBeFalsy();
            expect(gridColumns[3].cssClass).toBeFalsy();
            expect(gridColumns[4].cssClass).toBeFalsy();
        }));

        it('should invalidate grid', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var args = {column: gridColumns[1]};

            //when
            var onHeaderClick = gridMock.onHeaderClick.subscribe.calls.argsFor(0)[0];
            onHeaderClick(null, args);

            //then
            expect(gridMock.invalidate).toHaveBeenCalled();
        }));
    });

    describe('on header click event', function () {
        beforeEach(inject(function(DatagridService) {
            spyOn(DatagridService.dataView, 'getItem').and.returnValue({'0001': 'cell 1 content'});
            spyOn(DatagridService, 'getRowsContaining').and.returnValue([5, 18, 28, 42, 43]);
        }));

        it('should configure cells highlight class on cell click', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var cell = 1;
            var row = 28;
            var args = {cell: cell, row: row};

            //when
            var onClick = gridMock.onClick.subscribe.calls.argsFor(0)[0];
            onClick(null, args);

            //then
            expect(gridMock.cssStyleConfig.highlight).toEqual({
                5: { '0001': 'highlight' },
                18: { '0001': 'highlight' },
                28: { '0001': 'highlight' },
                42: { '0001': 'highlight' },
                43: { '0001': 'highlight' }
            });
        }));
    });

    describe('on active cell changed event', function () {
        it('should set "selected" column class', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var args = {cell: 1};

            assertColumnsHasNoStyles();

            //when
            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, args);

            //then
            expect(gridColumns[0].cssClass).toBeFalsy();
            expect(gridColumns[1].cssClass.indexOf('selected') > -1).toBe(true);
            expect(gridColumns[2].cssClass).toBeFalsy();
            expect(gridColumns[3].cssClass).toBeFalsy();
            expect(gridColumns[4].cssClass).toBeFalsy();
        }));

        it('should invalidate grid', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var args = {cell: 1};

            //when
            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, args);

            //then
            expect(gridMock.invalidate).toHaveBeenCalled();
        }));

        it('should do nothing when there is no active cell', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var args = {cell: undefined};

            //when
            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, args);

            //then
            expect(gridMock.invalidate).not.toHaveBeenCalled();
            assertColumnsHasNoStyles();
        }));
    });

    describe('reset cell styles', function() {
        it('should reset cell styles configuration', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            gridMock.setCellCssStyles('highlight', {'2': {'0000': 'highlight'}});

            //when
            DatagridStyleService.resetCellStyles();

            //then
            expect(gridMock.cssStyleConfig.highlight).toEqual({});
        }));

        it('should reset grid active cell', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);

            //when
            DatagridStyleService.resetCellStyles();

            //then
            expect(gridMock.resetActiveCell).toHaveBeenCalled();
        }));
    });

    describe('update column styles', function() {
        it('should set "selected" class on active cell column when this is NOT a preview', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            assertColumnsHasNoStyles();

            var isPreview = false;
            var activeCell = {cell: 1};
            gridMock.initActiveCellMock(activeCell);

            //when
            DatagridStyleService.manageColumnStyle(isPreview);

            //then
            expect(gridColumns[0].cssClass).toBeFalsy();
            expect(gridColumns[1].cssClass.indexOf('selected') > -1).toBe(true);
            expect(gridColumns[2].cssClass).toBeFalsy();
            expect(gridColumns[3].cssClass).toBeFalsy();
            expect(gridColumns[4].cssClass).toBeFalsy();
        }));

        it('should NOT set "selected" class on active cell column when this is a preview', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            assertColumnsHasNoStyles();

            var isPreview = true;
            var activeCell = {cell: 1};
            gridMock.initActiveCellMock(activeCell);

            //when
            DatagridStyleService.manageColumnStyle(isPreview);

            //then
            expect(gridColumns[0].cssClass).toBeFalsy();
            expect(gridColumns[1].cssClass.indexOf('selected') > -1).toBe(false);
            expect(gridColumns[2].cssClass).toBeFalsy();
            expect(gridColumns[3].cssClass).toBeFalsy();
            expect(gridColumns[4].cssClass).toBeFalsy();
        }));

        it('should NOT set "selected" class without active cell', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            DatagridStyleService.resetColumnStyles();
            assertColumnsHasNoStyles();

            var isPreview = false;
            gridMock.initActiveCellMock();

            //when
            DatagridStyleService.manageColumnStyle(isPreview);

            //then
            expect(gridColumns[0].cssClass).toBeFalsy();
            expect(gridColumns[1].cssClass.indexOf('selected') > -1).toBe(false);
            expect(gridColumns[2].cssClass).toBeFalsy();
            expect(gridColumns[3].cssClass).toBeFalsy();
            expect(gridColumns[4].cssClass).toBeFalsy();
        }));

        it('should set "number" class on number column on preview mode', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            assertColumnsHasNoStyles();

            var isPreview = true;

            //when
            DatagridStyleService.manageColumnStyle(isPreview);

            //then
            expect(gridColumns[0].cssClass).toBeFalsy();
            expect(gridColumns[1].cssClass.indexOf('number') > -1).toBe(true);
            expect(gridColumns[2].cssClass).toBeFalsy();
            expect(gridColumns[3].cssClass).toBeFalsy();
            expect(gridColumns[4].cssClass).toBeFalsy();
        }));

        it('should set "number" class on number column on NON preview mode with active cell', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            assertColumnsHasNoStyles();

            var isPreview = false;
            var activeCell = {cell: 1};
            gridMock.initActiveCellMock(activeCell);

            //when
            DatagridStyleService.manageColumnStyle(isPreview);

            //then
            expect(gridColumns[0].cssClass).toBeFalsy();
            expect(gridColumns[1].cssClass.indexOf('number') > -1).toBe(true);
            expect(gridColumns[2].cssClass).toBeFalsy();
            expect(gridColumns[3].cssClass).toBeFalsy();
            expect(gridColumns[4].cssClass).toBeFalsy();
        }));

        it('should apply "selected" class to last selected column before preview', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);

            gridMock.initActiveCellMock({cell: 1}); // a cell from column 1
            DatagridStyleService.manageColumnStyle(false); // will select column 1
            expect(gridColumns[1].cssClass.indexOf('selected') > -1).toBe(true);

            DatagridStyleService.manageColumnStyle(true); // will unselect column 1
            expect(gridColumns[1].cssClass.indexOf('selected') > -1).toBe(false);

            gridMock.initActiveCellMock(); // no active cell anymore, it should take last selected column id

            //when
            DatagridStyleService.manageColumnStyle(false);

            //then
            expect(gridColumns[0].cssClass).toBeFalsy();
            expect(gridColumns[1].cssClass.indexOf('selected') > -1).toBe(true);
            expect(gridColumns[2].cssClass).toBeFalsy();
            expect(gridColumns[3].cssClass).toBeFalsy();
            expect(gridColumns[4].cssClass).toBeFalsy();
        }));
    });

    describe('column formatter', function() {
        it('should adapt value into html with leading/trailing spaces management', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = '  my value     ';
            var columnDef = gridColumns[1];
            var dataContext = {};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result.indexOf('<span class="hiddenChars">  </span>my value<span class="hiddenChars">     </span>')).toBe(0);
        }));

        it('should add invisible rectangle on valid value', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = 'my value';
            var columnDef = gridColumns[1];
            var dataContext = {};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result.indexOf('<div class="invisible-rect"></div>') > 0).toBe(true);
        }));

        it('should add red rectangle on invalid value', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: ['my value']}};
            var value = 'my value';
            var columnDef = gridColumns[1];
            var dataContext = {};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result.indexOf('<div title="Invalid Value" class="red-rect"></div>') > 0).toBe(true);
        }));

        it('should add "deleted" class on deleted row', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = 'my value';
            var columnDef = gridColumns[1];
            var dataContext = {__tdpRowDiff : 'delete'};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result).toBe('<div class="cellDeletedValue"><strike>my value</strike></div>');
        }));

        it('should add a space " " as value on empty cell in a deleted row', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = '';
            var columnDef = gridColumns[1];
            var dataContext = {__tdpRowDiff : 'delete'};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result).toBe('<div class="cellDeletedValue"><strike> </strike></div>');
        }));

        it('should add "new" class on a new row', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = 'my value';
            var columnDef = gridColumns[1];
            var dataContext = {__tdpRowDiff : 'new'};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result).toBe('<div class="cellNewValue">my value</div>');
        }));

        it('should add a space " " as value on empty cell in a new row', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = '';
            var columnDef = gridColumns[1];
            var dataContext = {__tdpRowDiff : 'new'};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result).toBe('<div class="cellNewValue"> </div>');
        }));

        it('should add "update" class on an updated cell', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = 'my value';
            var columnDef = gridColumns[1];
            var dataContext = {__tdpDiff : {'0001': 'update'}};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result).toBe('<div class="cellUpdateValue">my value</div>');
        }));

        it('should add "new" class on a new cell', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = 'my value';
            var columnDef = gridColumns[1];
            var dataContext = {__tdpDiff : {'0001': 'new'}};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result).toBe('<div class="cellNewValue">my value</div>');
        }));

        it('should add "delete" class on a deleted cell', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = 'my value';
            var columnDef = gridColumns[1];
            var dataContext = {__tdpDiff : {'0001': 'delete'}};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result).toBe('<div class="cellDeletedValue">my value</div>');
        }));

        it('should add a space " " as value on empty deleted cell', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {quality: {invalidValues: []}};
            var value = '';
            var columnDef = gridColumns[1];
            var dataContext = {__tdpDiff : {'0001': 'delete'}};

            //when
            var formatter = DatagridStyleService.columnFormatter(col);
            var result = formatter(null, null, value, columnDef, dataContext);

            //then
            expect(result).toBe('<div class="cellDeletedValue"> </div>');
        }));
    });

    describe('column preview style', function() {
        it('should return "new" column style', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {__tdpColumnDiff: 'new'};

            //when
            var diffClass = DatagridStyleService.getColumnPreviewStyle(col);

            //then
            expect(diffClass).toBe('newColumn');
        }));

        it('should return "deleted" column style', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {__tdpColumnDiff: 'delete'};

            //when
            var diffClass = DatagridStyleService.getColumnPreviewStyle(col);

            //then
            expect(diffClass).toBe('deletedColumn');
        }));

        it('should return "updated" column style', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {__tdpColumnDiff: 'update'};

            //when
            var diffClass = DatagridStyleService.getColumnPreviewStyle(col);

            //then
            expect(diffClass).toBe('updatedColumn');
        }));

        it('should return empty string on no change diff', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var col = {};

            //when
            var diffClass = DatagridStyleService.getColumnPreviewStyle(col);

            //then
            expect(diffClass).toBe('');
        }));
    });

    describe('column selection management', function() {
        it('should return selected column', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);

            //given : set selected column
            var activeCell = {cell: 1};
            gridMock.initActiveCellMock(activeCell);
            DatagridStyleService.manageColumnStyle(false);

            //when
            var selectedColumn = DatagridStyleService.selectedColumn();

            //then
            expect(selectedColumn).toBe(gridColumns[1]);
        }));

        it('should undefined when there is no selected column', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);

            //when
            var selectedColumn = DatagridStyleService.selectedColumn();

            //then
            expect(selectedColumn).not.toBeDefined();
        }));
    });
});
