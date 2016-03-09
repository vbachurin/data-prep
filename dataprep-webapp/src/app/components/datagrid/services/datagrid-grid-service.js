/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

/**
 * @ngdoc service
 * @name data-prep.datagrid.service:DatagridGridService
 * @description Datagrid private service that init the grid
 * @requires data-prep.state.service:StateService
 * @requires data-prep.datagrid.service:DatagridService
 * @requires data-prep.datagrid.service:DatagridStyleService
 * @requires data-prep.datagrid.service:DatagridColumnService
 * @requires data-prep.datagrid.service:DatagridSizeService
 * @requires data-prep.datagrid.service:DatagridExternalService
 * @requires data-prep.datagrid.service:DatagridTooltipService
 */
export default function DatagridGridService($q, $timeout, StateService, state, DatagridService, DatagridStyleService, DatagridColumnService,
                                            DatagridSizeService, DatagridExternalService, DatagridTooltipService, DatagridModelService) {
    'ngInject';

    var grid = null;
    var gridServices = [
        DatagridColumnService,
        DatagridStyleService,
        DatagridSizeService,
        DatagridExternalService,
        DatagridTooltipService
    ];

    return {
        initGrid: initGrid,
        navigateToFocusedColumn: navigateToFocusedColumn
    };

    /**
     * @ngdoc method
     * @name attachLongTableListeners
     * @methodOf data-prep.datagrid.service:DatagridGridService
     * @description Attaches listeners for data update to reRender the grid
     */
    function attachLongTableListeners() {
        state.playground.grid.dataView.onRowCountChanged.subscribe(function () {
            grid.updateRowCount();
            grid.render();
        });
        state.playground.grid.dataView.onRowsChanged.subscribe(function (e, args) {
            grid.invalidateRows(args.rows);
            grid.render();
        });
    }

    /**
     * @ngdoc method
     * @name attachGridStateListeners
     * @methodOf data-prep.datagrid.service:DatagridGridService
     * @description Attach listeners for saving the state of column id and line selection number
     */
    function attachGridStateListeners() {
        grid.onActiveCellChanged.subscribe(function (e, args) {
            $timeout(() => {
                if (angular.isDefined(args.cell)) {
                    var column = grid.getColumns()[args.cell];
                    StateService.setGridSelection(column.tdpColMetadata, args.row);

                } else {
                    StateService.setGridSelection(state.playground.grid.selectedColumn, null);
                }
            });
        });

        grid.onHeaderContextMenu.subscribe(function (e, args) {
            $timeout(StateService.setGridSelection.bind(null, args.column.tdpColMetadata, null));
        });

        grid.onHeaderClick.subscribe(function (e, args) {
            $timeout(StateService.setGridSelection.bind(null, args.column.tdpColMetadata, null));
        });
    }

    /**
     * @ngdoc method
     * @name navigateToFocusedColumn
     * @methodOf data-prep.datagrid.service:DatagridGridService
     * @description navigates between columns
     */
    function navigateToFocusedColumn() {
        if (DatagridService.focusedColumn) {
            var columnIndex = _.findIndex(grid.getColumns(), {id: DatagridService.focusedColumn});
            var renderedRows = grid.getRenderedRange();
            var centerRow = +((renderedRows.bottom - renderedRows.top) / 2).toFixed(0);
            grid.scrollCellIntoView(renderedRows.top + centerRow, columnIndex, false);
        }
    }

    /**
     * @ngdoc method
     * @name initGridServices
     * @methodOf data-prep.datagrid.service:DatagridGridService
     * @description Init other grid services with the new created grid
     */
    function initGridServices() {
        _.forEach(gridServices, function (service) {
            service.init(grid);
        });
    }

    function attachRemoteModelFetch(remoteModel) {
        remoteModel.setFetchRecords((from, to) => {
            console.log('From: ' + from + ' - To: ' + to);
            const result = [];
            for(let i = 0; i <= (to - from); ++i) {
                result[i] = {
                    tdpId: (from + i),
                    '0000': ''+ Math.random(),
                    '0001': ''+ Math.random(),
                    '0002': ''+ Math.random(),
                    '0003': ''+ Math.random(),
                    '0004': ''+ Math.random(),
                    '0005': ''+ Math.random(),
                    '0006': ''+ Math.random(),
                    '0007': ''+ Math.random()
                };
            }
            return $q.resolve(result);
        });

        grid.onViewportChanged.subscribe(() => {
            const vp = grid.getViewport();
            remoteModel.ensureData(vp.top, vp.bottom);
        });

        remoteModel.onDataLoaded.subscribe((e, args) => {
            for (let i = args.from; i <= args.to; i++) {
                grid.invalidateRow(i);
            }
            grid.updateRowCount();
            grid.render();
        });

        grid.onViewportChanged.notify();
    }

    /**
     * @ngdoc method
     * @name initGrid
     * @methodOf data-prep.datagrid.service:DatagridGridService
     * @description Create Slick grid and initiate other datagrid services
     The dataview is initiated and held by {@link data-prep.services.playground.service:DatagridService DatagridService}
     * @param {string} elementId The element where the grid will be inserted in the DOM. The element must exists
     */
    function initGrid(elementId) {
        //create grid
        var options = {
            autoEdit: false,
            editable: true,
            enableAddRow: false,
            enableCellNavigation: true,
            enableTextSelectionOnCells: false,
            syncColumnCellResize: false,
            frozenColumn: 0,
            asyncEditorLoading: true,
            asyncEditorLoadDelay: 150
        };

        const remoteModel = DatagridModelService.createRemoteModel(1000);
        grid = new Slick.Grid(elementId, remoteModel.data, [{id: 'tdpId'}], options);

        //listeners
        attachLongTableListeners();
        attachGridStateListeners();
        attachRemoteModelFetch(remoteModel);

        //init other services
        initGridServices();
        return grid;
    }
}