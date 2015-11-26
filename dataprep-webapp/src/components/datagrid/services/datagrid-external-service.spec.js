describe('Datagrid external service', function () {
    'use strict';

    var gridMock, stateMock;

    var gridColumns = [
        {id: '0000', field: 'col0', tdpColMetadata: {id: '0000', name: 'col0'}},
        {id: '0001', field: 'col1', tdpColMetadata: {id: '0001', name: 'col1'}},
        {id: '0002', field: 'col2', tdpColMetadata: {id: '0002', name: 'col2'}},
        {id: '0003', field: 'col3', tdpColMetadata: {id: '0003', name: 'col3'}},
        {id: '0004', field: 'col4', tdpColMetadata: {id: '0004', name: 'col4'}},
        {id: 'tdpId', field: 'tdpId', tdpColMetadata: {id: 'tdpId', name: 'tdpId'}}
    ];

    beforeEach(module('data-prep.datagrid', function($provide) {
        stateMock = {
            playground: {
                grid: {}
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(function (StatisticsService, SuggestionService) {
        /*global SlickGridMock:false */
        gridMock = new SlickGridMock();
        gridMock.initColumnsMock(gridColumns);

        spyOn(gridMock.onActiveCellChanged, 'subscribe').and.returnValue();
        spyOn(gridMock.onHeaderClick, 'subscribe').and.returnValue();
        spyOn(gridMock.onHeaderContextMenu, 'subscribe').and.returnValue();
        spyOn(gridMock.onScroll, 'subscribe').and.returnValue();

        spyOn(StatisticsService, 'updateStatistics').and.returnValue();
        spyOn(StatisticsService, 'reset').and.returnValue();
        spyOn(SuggestionService, 'setColumn').and.returnValue();
        spyOn(SuggestionService, 'setLine').and.returnValue();
        spyOn(SuggestionService, 'selectTab').and.returnValue();
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

        it('should add header right click listener', inject(function (DatagridExternalService) {
            //when
            DatagridExternalService.init(gridMock);

            //then
            expect(gridMock.onHeaderContextMenu.subscribe).toHaveBeenCalled();
        }));

        it('should add scroll listener', inject(function (DatagridExternalService) {
            //when
            DatagridExternalService.init(gridMock);

            //then
            expect(gridMock.onScroll.subscribe).toHaveBeenCalled();
        }));
    });

    describe('update right panel', function() {
        describe('debounce', function() {
            it('should update after 300ms', inject(function($timeout, DatagridExternalService, SuggestionService) {
                //given
                stateMock.playground.grid.selectedColumn = {id: '0001'};

                //when
                DatagridExternalService.updateSuggestionPanel('TABLE');
                $timeout.flush(0);
                expect(SuggestionService.selectTab).not.toHaveBeenCalled();
                $timeout.flush(300);

                //then
                expect(SuggestionService.selectTab).toHaveBeenCalledWith('TABLE');
            }));

            it('should update immediately', inject(function($timeout, DatagridExternalService, SuggestionService) {
                //given
                stateMock.playground.grid.selectedColumn = {id: '0001'};

                //when
                DatagridExternalService.updateSuggestionPanel('TABLE', true);
                $timeout.flush(0);

                //then
                expect(SuggestionService.selectTab).toHaveBeenCalledWith('TABLE');
            }));

            it('should cancel pending update and trigger a new one', inject(function($timeout, DatagridExternalService, SuggestionService) {
                //given
                stateMock.playground.grid.selectedColumn = {id: '0001'};

                DatagridExternalService.updateSuggestionPanel();                // trigger an update but flush time < debounce time : there is a pending update
                $timeout.flush(200);

                expect(SuggestionService.selectTab).not.toHaveBeenCalledWith();

                //when
                DatagridExternalService.updateSuggestionPanel();                // trigger another update while there is a pending update
                $timeout.flush(100);                                            // flush the pending request remaining time
                expect(SuggestionService.selectTab).not.toHaveBeenCalledWith(); // first pending request should not have triggered
                $timeout.flush(200);                                            // flush the second update remaining time

                //then
                expect(SuggestionService.selectTab).toHaveBeenCalled();
            }));
        });

        describe('tab selection', function() {
            it('should select provided tab', inject(function($timeout, DatagridExternalService, SuggestionService) {
                //given
                stateMock.playground.grid.selectedColumn = {id: '0001'};
                expect(SuggestionService.selectTab).not.toHaveBeenCalled();

                //when
                DatagridExternalService.updateSuggestionPanel('TABLE');
                $timeout.flush(300);

                //then
                expect(SuggestionService.selectTab).toHaveBeenCalledWith('TABLE');
            }));

            it('should select "COLUMN" tab if no tab is provided', inject(function($timeout, DatagridExternalService, SuggestionService) {
                //given
                stateMock.playground.grid.selectedColumn = {id: '0001'};
                expect(SuggestionService.selectTab).not.toHaveBeenCalled();

                //when
                DatagridExternalService.updateSuggestionPanel();
                $timeout.flush(300);

                //then
                expect(SuggestionService.selectTab).toHaveBeenCalledWith('COLUMN');
            }));

            it('should select "LINE" tab when there is no selected column', inject(function($timeout, DatagridExternalService, SuggestionService) {
                //given
                stateMock.playground.grid.selectedLine = {tdpId: 125};
                stateMock.playground.grid.selectedColumn = null;
                expect(SuggestionService.selectTab).not.toHaveBeenCalled();

                //when
                DatagridExternalService.updateSuggestionPanel();
                $timeout.flush(300);

                //then
                expect(SuggestionService.selectTab).toHaveBeenCalledWith('LINE');
            }));
        });

        describe('charts', function() {
            it('should reset charts when there is no selected column', inject(function($timeout, DatagridExternalService, StatisticsService) {
                //given
                stateMock.playground.grid.selectedLine = {tdpId: 125};
                stateMock.playground.grid.selectedColumn = null;
                expect(StatisticsService.reset).not.toHaveBeenCalled();

                //when
                DatagridExternalService.updateSuggestionPanel();
                $timeout.flush(300);

                //then
                expect(StatisticsService.reset).toHaveBeenCalledWith(true, true, true);
            }));

            it('should update charts when there is a selected column', inject(function($timeout, DatagridExternalService, StatisticsService) {
                //given
                stateMock.playground.grid.selectedColumn = {id: '0001'};
                expect(StatisticsService.updateStatistics).not.toHaveBeenCalled();

                //when
                DatagridExternalService.updateSuggestionPanel();
                $timeout.flush(300);

                //then
                expect(StatisticsService.updateStatistics).toHaveBeenCalled();
            }));
        });

        describe('transformations', function() {
            it('should do nothing if selected column/line have not changed', inject(function($timeout, DatagridExternalService, SuggestionService) {
                //when
                DatagridExternalService.updateSuggestionPanel();
                $timeout.flush(300);

                //then
                expect(SuggestionService.setLine).not.toHaveBeenCalled();
                expect(SuggestionService.setColumn).not.toHaveBeenCalled();
            }));

            describe('line scope', function() {
                it('should update when there is a selected line', inject(function($timeout, DatagridExternalService, SuggestionService) {
                    //given
                    stateMock.playground.grid.selectedLine = {tdpId: 125};
                    expect(SuggestionService.setLine).not.toHaveBeenCalled();

                    //when
                    DatagridExternalService.updateSuggestionPanel();
                    $timeout.flush(300);

                    //then
                    expect(SuggestionService.setLine).toHaveBeenCalledWith(stateMock.playground.grid.selectedLine);
                }));

                it('should NOT update when there is no selected line', inject(function($timeout, DatagridExternalService, SuggestionService) {
                    //given
                    stateMock.playground.grid.selectedLine = null;
                    stateMock.playground.grid.selectedColumn = {id: '0001'};
                    expect(SuggestionService.setLine).not.toHaveBeenCalled();

                    //when
                    DatagridExternalService.updateSuggestionPanel();
                    $timeout.flush(300);

                    //then
                    expect(SuggestionService.setLine).not.toHaveBeenCalled();
                }));

                it('should NOT update when selected line has not changed', inject(function($timeout, DatagridExternalService, SuggestionService) {
                    //given
                    stateMock.playground.grid.selectedLine = {tdpId: 125};
                    DatagridExternalService.updateSuggestionPanel();
                    $timeout.flush(300);
                    expect(SuggestionService.setLine.calls.count()).toBe(1);

                    stateMock.playground.grid.selectedColumn = {id: '0001'};

                    //when
                    DatagridExternalService.updateSuggestionPanel();
                    $timeout.flush(300);

                    //then
                    expect(SuggestionService.setLine.calls.count()).toBe(1);
                }));
            });

            describe('column scope', function() {
                it('should update when there is a selected column', inject(function($timeout, DatagridExternalService, SuggestionService) {
                    //given
                    stateMock.playground.grid.selectedColumn = {id: '0001'};
                    expect(SuggestionService.setColumn).not.toHaveBeenCalled();

                    //when
                    DatagridExternalService.updateSuggestionPanel();
                    $timeout.flush(300);

                    //then
                    expect(SuggestionService.setColumn).toHaveBeenCalled();
                }));

                it('should NOT update when there is no selected column', inject(function($timeout, DatagridExternalService, SuggestionService) {
                    //given
                    stateMock.playground.grid.selectedLine = {tdpId: 125};
                    stateMock.playground.grid.selectedColumn = null;
                    expect(SuggestionService.setColumn).not.toHaveBeenCalled();

                    //when
                    DatagridExternalService.updateSuggestionPanel();
                    $timeout.flush(300);

                    //then
                    expect(SuggestionService.setColumn).not.toHaveBeenCalled();
                }));

                it('should NOT update when selected column has not changed', inject(function($timeout, DatagridExternalService, SuggestionService) {
                    //given
                    stateMock.playground.grid.selectedColumn = {id: '0001'};
                    DatagridExternalService.updateSuggestionPanel();
                    $timeout.flush(300);
                    expect(SuggestionService.setColumn.calls.count()).toBe(1);

                    stateMock.playground.grid.selectedLine = {tdpId: 125};

                    //when
                    DatagridExternalService.updateSuggestionPanel();
                    $timeout.flush(300);

                    //then
                    expect(SuggestionService.setColumn.calls.count()).toBe(1);
                }));
            });
        });
    });

    describe('on active cell event', function () {
        it('should update playground right panel on active cell changed after a 300ms delay', inject(function ($timeout, DatagridExternalService, StatisticsService, SuggestionService) {
            //given
            DatagridExternalService.init(gridMock);
            var columnMetadata = gridColumns[1].tdpColMetadata;
            stateMock.playground.grid.selectedColumn = columnMetadata;
            var args = {cell: 1};

            //when
            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, args);

            expect(StatisticsService.updateStatistics).not.toHaveBeenCalled();
            expect(SuggestionService.setColumn).not.toHaveBeenCalled();
            expect(SuggestionService.selectTab).not.toHaveBeenCalled();

            $timeout.flush(300);

            //then
            expect(StatisticsService.updateStatistics).toHaveBeenCalled();
            expect(SuggestionService.setColumn).toHaveBeenCalledWith(columnMetadata);
            expect(SuggestionService.selectTab).toHaveBeenCalledWith('COLUMN');
        }));

        it('should do nothing when no cell is active', inject(function ($timeout, DatagridExternalService, StatisticsService, SuggestionService) {
            //given
            try{
                $timeout.flush();
            }
            catch(error) {}
            DatagridExternalService.init(gridMock);

            //when
            var onActiveCellChanged = gridMock.onActiveCellChanged.subscribe.calls.argsFor(0)[0];
            onActiveCellChanged(null, {});
            try{
                $timeout.flush();
            }

            //then
            catch(error) {
                expect(StatisticsService.updateStatistics).not.toHaveBeenCalled();
                expect(SuggestionService.setColumn).not.toHaveBeenCalled();
                expect(SuggestionService.selectTab).not.toHaveBeenCalled();
                return;
            }
            throw new Error('should have thrown exception because there is nothing to flush');
        }));
    });

    describe('on header click event', function () {
        it('should update playground right panel on header click after a 300ms delay', inject(function ($timeout, DatagridExternalService, StatisticsService, SuggestionService) {
            //given
            DatagridExternalService.init(gridMock);
            var columnMetadata = gridColumns[1].tdpColMetadata;
            stateMock.playground.grid.selectedColumn = columnMetadata;

            //when
            var onHeaderClick = gridMock.onHeaderClick.subscribe.calls.argsFor(0)[0];
            onHeaderClick();

            expect(StatisticsService.updateStatistics).not.toHaveBeenCalled();
            expect(SuggestionService.setColumn).not.toHaveBeenCalled();

            $timeout.flush(300);

            //then
            expect(StatisticsService.updateStatistics).toHaveBeenCalled();
            expect(SuggestionService.setColumn).toHaveBeenCalledWith(columnMetadata);
            expect(SuggestionService.selectTab).toHaveBeenCalledWith('COLUMN');
        }));

        it('should update playground right panel on header right click after a 300ms delay', inject(function ($timeout, DatagridExternalService, StatisticsService, SuggestionService) {
            //given
            DatagridExternalService.init(gridMock);
            var columnMetadata = gridColumns[1].tdpColMetadata;
            stateMock.playground.grid.selectedColumn = columnMetadata;

            //when
            var onHeaderContextMenu = gridMock.onHeaderContextMenu.subscribe.calls.argsFor(0)[0];
            onHeaderContextMenu();

            expect(StatisticsService.updateStatistics).not.toHaveBeenCalled();
            expect(SuggestionService.setColumn).not.toHaveBeenCalled();
            expect(SuggestionService.selectTab).not.toHaveBeenCalled();

            $timeout.flush(300);

            //then
            expect(StatisticsService.updateStatistics).toHaveBeenCalled();
            expect(SuggestionService.setColumn).toHaveBeenCalledWith(columnMetadata);
            expect(SuggestionService.selectTab).toHaveBeenCalledWith('COLUMN');
        }));
    });

    describe('on scroll event', function () {
        it('should update preview range on scroll after a 300ms delay', inject(function (DatagridExternalService, PreviewService) {
            //given
            DatagridExternalService.init(gridMock);

            var range = [1, 3, 5];
            gridMock.initRenderedRangeMock(range);

            expect(PreviewService.gridRangeIndex).toBeFalsy();

            //when
            var onScroll = gridMock.onScroll.subscribe.calls.argsFor(0)[0];
            onScroll();
            jasmine.clock().tick(300);

            //then
            expect(PreviewService.gridRangeIndex).toBe(range);
        }));
    });
});
