describe('Datagrid external service', function () {
    'use strict';

    var gridMock;

    var gridColumns = [
        {id: '0000', field: 'col0', tdpColMetadata: {id: '0000', name: 'col0'}},
        {id: '0001', field: 'col1', tdpColMetadata: {id: '0001', name: 'col1'}},
        {id: '0002', field: 'col2', tdpColMetadata: {id: '0002', name: 'col2'}},
        {id: '0003', field: 'col3', tdpColMetadata: {id: '0003', name: 'col3'}},
        {id: '0004', field: 'col4', tdpColMetadata: {id: '0004', name: 'col4'}}
    ];

    beforeEach(module('data-prep.datagrid'));
    beforeEach(module('data-prep.suggestions-stats'));

    beforeEach(inject(function (StatisticsService, ColumnSuggestionService) {
        /*global SlickGridMock:false */
        gridMock = new SlickGridMock();
        gridMock.initColumnsMock(gridColumns);

        spyOn(gridMock.onActiveCellChanged, 'subscribe').and.returnValue();
        spyOn(gridMock.onHeaderClick, 'subscribe').and.returnValue();
        spyOn(gridMock.onScroll, 'subscribe').and.returnValue();

        spyOn(StatisticsService, 'processData').and.returnValue();
        spyOn(ColumnSuggestionService, 'setColumn').and.returnValue();
    }));

    beforeEach(function () {
        jasmine.clock().install();
    });
    afterEach(function () {
        jasmine.clock().uninstall();
    });

    describe('on creation', function () {
        it('should add grid active cell change listener', inject(function (DatagridExternalService) {
            //when
            DatagridExternalService.init(gridMock);

            //then
            expect(gridMock.onActiveCellChanged.subscribe).toHaveBeenCalled();
        }));

        it('should add header click listener', inject(function (DatagridExternalService) {
            //when
            DatagridExternalService.init(gridMock);

            //then
            expect(gridMock.onHeaderClick.subscribe).toHaveBeenCalled();
        }));

        it('should add scroll listener', inject(function (DatagridExternalService) {
            //when
            DatagridExternalService.init(gridMock);

            //then
            expect(gridMock.onScroll.subscribe).toHaveBeenCalled();
        }));
    });

    describe('on event', function () {
        it('should update playground right panel on active cell changed', inject(function (DatagridExternalService, StatisticsService, ColumnSuggestionService) {
            //given
            DatagridExternalService.init(gridMock);
            var args = {cell: 1};
            var columnMetadata = gridColumns[1].tdpColMetadata;

            //when
            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, args);

            expect(StatisticsService.processData).not.toHaveBeenCalled();
            expect(ColumnSuggestionService.setColumn).not.toHaveBeenCalled();
            jasmine.clock().tick(200);

            //then
            expect(StatisticsService.processData).toHaveBeenCalledWith(columnMetadata);
            expect(ColumnSuggestionService.setColumn).toHaveBeenCalledWith(columnMetadata);
        }));

        it('should NOT update playground right panel on active cell changed if column is the same', inject(function (DatagridExternalService, StatisticsService, ColumnSuggestionService) {
            //given
            DatagridExternalService.init(gridMock);
            var args = {cell: 1};

            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, args);
            jasmine.clock().tick(200);

            expect(StatisticsService.processData.calls.count()).toBe(1);
            expect(ColumnSuggestionService.setColumn.calls.count()).toBe(1);

            //when
            onActiveCellChanged(null, args);
            jasmine.clock().tick(200);

            //then
            expect(StatisticsService.processData.calls.count()).toBe(1);
            expect(ColumnSuggestionService.setColumn.calls.count()).toBe(1);
        }));

        it('should cancel the pending right panel update and schedule a new one', inject(function (DatagridExternalService, StatisticsService, ColumnSuggestionService) {
            //given
            DatagridExternalService.init(gridMock);
            var firstCallArgs = {cell: 1};
            var secondCallArgs = {cell: 2};
            var secondCallColumnMetadata = gridColumns[2].tdpColMetadata;

            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, firstCallArgs);

            jasmine.clock().tick(199);

            expect(StatisticsService.processData).not.toHaveBeenCalled();
            expect(ColumnSuggestionService.setColumn).not.toHaveBeenCalled();

            //when
            onActiveCellChanged(null, secondCallArgs);

            jasmine.clock().tick(1);
            expect(StatisticsService.processData).not.toHaveBeenCalled();
            expect(ColumnSuggestionService.setColumn).not.toHaveBeenCalled();
            jasmine.clock().tick(200);

            //then
            expect(StatisticsService.processData).toHaveBeenCalledWith(secondCallColumnMetadata);
            expect(ColumnSuggestionService.setColumn).toHaveBeenCalledWith(secondCallColumnMetadata);
        }));
        
        it('should do nothing when no cell is active', inject(function (DatagridExternalService, StatisticsService, ColumnSuggestionService) {
            //given
            DatagridExternalService.init(gridMock);

            //when
            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, {});

            //then
            expect(StatisticsService.processData).not.toHaveBeenCalled();
            expect(ColumnSuggestionService.setColumn).not.toHaveBeenCalled();
        }));

        it('should update playground right panel on header click after a 200ms delay', inject(function (DatagridExternalService, StatisticsService, ColumnSuggestionService) {
            //given
            DatagridExternalService.init(gridMock);
            var args = {
                column: {id: '0001'}
            };
            var columnMetadata = gridColumns[1].tdpColMetadata;

            //when
            var onHeaderClick = gridMock.onHeaderClick.subscribe.calls.argsFor(0)[0];
            onHeaderClick(null, args);

            expect(StatisticsService.processData).not.toHaveBeenCalled();
            expect(ColumnSuggestionService.setColumn).not.toHaveBeenCalled();
            jasmine.clock().tick(200);

            //then
            expect(StatisticsService.processData).toHaveBeenCalledWith(columnMetadata);
            expect(ColumnSuggestionService.setColumn).toHaveBeenCalledWith(columnMetadata);
        }));

        it('should NOT update playground right panel on header click when column is the same', inject(function (DatagridExternalService, StatisticsService, ColumnSuggestionService) {
            //given
            DatagridExternalService.init(gridMock);
            var args = {
                column: {id: '0001'}
            };

            var onHeaderClick = gridMock.onHeaderClick.subscribe.calls.argsFor(0)[0];
            onHeaderClick(null, args);
            jasmine.clock().tick(200);

            expect(StatisticsService.processData.calls.count()).toBe(1);
            expect(ColumnSuggestionService.setColumn.calls.count()).toBe(1);

            //when
            onHeaderClick(null, args);
            jasmine.clock().tick(200);

            //then
            expect(StatisticsService.processData.calls.count()).toBe(1);
            expect(ColumnSuggestionService.setColumn.calls.count()).toBe(1);
        }));

        it('should update preview range on scroll after a 200ms delay', inject(function (DatagridExternalService, PreviewService) {
            //given
            DatagridExternalService.init(gridMock);

            var range = [1, 3, 5];
            gridMock.initRenderedRangeMock(range);

            expect(PreviewService.gridRangeIndex).toBeFalsy();

            //when
            var onScroll = gridMock.onScroll.subscribe.calls.argsFor(0)[0];
            onScroll();
            jasmine.clock().tick(200);

            //then
            expect(PreviewService.gridRangeIndex).toBe(range);
        }));
    });

});
