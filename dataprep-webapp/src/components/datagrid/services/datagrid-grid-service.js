(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.datagrid.service:DatagridGridService
     * @description Datagrid private service that init the grid
     * @requires data-prep.datagrid.service:DatagridService
     * @requires data-prep.datagrid.service:DatagridStyleService
     * @requires data-prep.datagrid.service:DatagridColumnService
     * @requires data-prep.datagrid.service:DatagridSizeService
     * @requires data-prep.datagrid.service:DatagridExternalService
     * @requires data-prep.datagrid.service:DatagridTooltipService
     */
    function DatagridGridService(DatagridService, DatagridStyleService, DatagridColumnService,
                                 DatagridSizeService, DatagridExternalService, DatagridTooltipService) {
        var grid = null;
        var gridServices = [
            DatagridColumnService,
            DatagridStyleService,
            DatagridSizeService,
            DatagridExternalService,
            DatagridTooltipService
        ];

        return {
            initGrid : initGrid,
            navigateToFocusedColumn: navigateToFocusedColumn
        };

        //--------------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name attachLongTableListeners
         * @methodOf data-prep.datagrid.service:DatagridGridService
         * @description Attach listeners for big table row management
         */
        function attachLongTableListeners() {
            DatagridService.dataView.onRowCountChanged.subscribe(function () {
                grid.updateRowCount();
                grid.render();
            });
            DatagridService.dataView.onRowsChanged.subscribe(function (e, args) {
                grid.invalidateRows(args.rows);
                grid.render();
            });
        }


        /**
         * @ngdoc method
         * @name navigateToFocusedColumn
         * @methodOf data-prep.datagrid.service:DatagridGridService
         * @description navigates between columns
         */
        function navigateToFocusedColumn(){
            if(DatagridService.focusedColumn) {
                var columnIndex = _.findIndex(grid.getColumns(), {id: DatagridService.focusedColumn});
                var renderedRows = grid.getRenderedRange();
                var centerRow   = +((renderedRows.bottom - renderedRows.top) / 2).toFixed(0);
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
            _.forEach(gridServices, function(service) {
                service.init(grid);
            });
        }

        /**
         * @ngdoc method
         * @name initGrid
         * @methodOf data-prep.datagrid.service:DatagridGridService
         * @description Create Slick grid and initiate other datagrid services
         The dataview is initiated and held by {@link data-prep.services.playground.service:DatagridService DatagridService}
         * @param {string} elementId The element where the grid will be inserted in the DOM. The element must exists
         */
        function initGrid (elementId) {
            //create grid
            var options = {
                editable: true,
                enableAddRow: false,
                enableCellNavigation: true,
                enableTextSelectionOnCells: false,
                autoEdit: false
            };
            grid = new Slick.Grid(elementId, DatagridService.dataView, [], options);

            //listeners
            attachLongTableListeners();

            //init other services
            initGridServices();

            return grid;
        }
    }

    angular.module('data-prep.datagrid')
        .service('DatagridGridService', DatagridGridService);
})();