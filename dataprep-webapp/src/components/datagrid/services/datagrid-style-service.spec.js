describe('Datagrid style service', function () {
    'use strict';

    var gridMock, gridColumns;

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

    describe('on event', function () {
        it('should set reset cell styles on header click', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            gridMock.setCellCssStyles('highlight', {'2': {'0000': 'highlight'}});

            var args = {column: gridColumns[1]};

            //when
            var onHeaderClick = gridMock.onHeaderClick.subscribe.calls.argsFor(0)[0];
            onHeaderClick(null, args);

            //then
            expect(gridMock.resetActiveCell).toHaveBeenCalled();
            expect(gridMock.cssStyleConfig['highlight']).toEqual({});
        }));

        it('should set selected column class on header click', inject(function (DatagridStyleService) {
            //given
            DatagridStyleService.init(gridMock);
            var args = {column: gridColumns[1]};

            gridColumns.forEach(function(column) {
                expect(column.cssClass).toBeFalsy();
            });

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
    });

});
