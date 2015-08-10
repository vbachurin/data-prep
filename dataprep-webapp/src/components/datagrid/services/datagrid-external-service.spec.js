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

    beforeEach(inject(function (StatisticsService, ColumnSuggestionService, SuggestionsStatsAggregationsService) {
        /*global SlickGridMock:false */
        gridMock = new SlickGridMock();
        gridMock.initColumnsMock(gridColumns);

        spyOn(gridMock.onActiveCellChanged, 'subscribe').and.returnValue();
        spyOn(gridMock.onHeaderClick, 'subscribe').and.returnValue();
        spyOn(gridMock.onScroll, 'subscribe').and.returnValue();

        spyOn(StatisticsService, 'processVisuData').and.returnValue();
        spyOn(ColumnSuggestionService, 'setColumn').and.returnValue();
        spyOn(SuggestionsStatsAggregationsService, 'updateAggregations').and.returnValue();

    }));

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
        it('should update playground right panel on active cell changed', inject(function (DatagridExternalService, StatisticsService, ColumnSuggestionService, SuggestionsStatsAggregationsService) {
            //given
            DatagridExternalService.init(gridMock);
            var args = {cell: 1};
            var columnMetadata = gridColumns[1].tdpColMetadata;

            //when
            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, args);

            //then
            expect(StatisticsService.processVisuData).toHaveBeenCalledWith(columnMetadata);
            expect(ColumnSuggestionService.setColumn).toHaveBeenCalledWith(columnMetadata);
            expect(SuggestionsStatsAggregationsService.updateAggregations).toHaveBeenCalledWith(columnMetadata);
        }));

        it('should do nothing when no cell is active', inject(function (DatagridExternalService, StatisticsService, ColumnSuggestionService, SuggestionsStatsAggregationsService) {
            //given
            DatagridExternalService.init(gridMock);

            //when
            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, {});

            //then
            expect(StatisticsService.processVisuData).not.toHaveBeenCalled();
            expect(ColumnSuggestionService.setColumn).not.toHaveBeenCalled();
            expect(SuggestionsStatsAggregationsService.updateAggregations).not.toHaveBeenCalledWith();
        }));

        it('should update playground right panel on header click', inject(function (DatagridExternalService, StatisticsService, ColumnSuggestionService) {
            //given
            DatagridExternalService.init(gridMock);
            var args = {
                column: {id: '0001'}
            };
            var columnMetadata = gridColumns[1].tdpColMetadata;

            //when
            var onHeaderClick = gridMock.onHeaderClick.subscribe.calls.argsFor(0)[0];
            onHeaderClick(null, args);

            //then
            expect(StatisticsService.processVisuData).toHaveBeenCalledWith(columnMetadata);
            expect(ColumnSuggestionService.setColumn).toHaveBeenCalledWith(columnMetadata);
        }));

        it('should update preview range on scroll', inject(function (DatagridExternalService, PreviewService) {
            //given
            DatagridExternalService.init(gridMock);

            var range = [1, 3, 5];
            gridMock.initRenderedRangeMock(range);

            expect(PreviewService.gridRangeIndex).toEqual([]);

            //when
            var onScroll = gridMock.onScroll.subscribe.calls.argsFor(0)[0];
            onScroll();

            //then
            expect(PreviewService.gridRangeIndex).toBe(range);
        }));
    });

});
