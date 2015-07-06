(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.datagrid.service:DatagridExternalService
     * @description Datagrid private service that manage the selected column action to the outer world (non dratagrid)
     * @requires data-prep.services.statistics.service:StatisticsService
     * @requires data-prep.services.transformation.service:ColumnSuggestionService
     * @requires data-prep.services.playground.service:PreviewService
     */
    function DatagridExternalService(StatisticsService, ColumnSuggestionService, PreviewService) {
        var grid;

        return {
            init: init
        };

        //--------------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name updateSuggestionPanel
         * @methodOf data-prep.datagrid.service:DatagridExternalService
         * @param {string} column The selected column
         * @description Set the selected column into external services. This will trigger actions that use this property
         * Ex : StatisticsService for dataviz, ColumnSuggestionService for transformation list
         */
        function updateSuggestionPanel(column) {
            var columnMetadata = column.tdpColMetadata;
            StatisticsService.processVisuData(columnMetadata);
            ColumnSuggestionService.setColumn(columnMetadata); // this will trigger a digest after REST call
        }

        /**
         * @ngdoc method
         * @name attachCellListeners
         * @methodOf data-prep.datagrid.service:DatagridExternalService
         * @description Attach cell selection listeners
         */
        function attachCellListeners() {
            //change selected cell column background
            grid.onActiveCellChanged.subscribe(function(e,args) {
                if(angular.isDefined(args.cell)) {
                    var column = grid.getColumns()[args.cell];
                    updateSuggestionPanel(column);
                }
            });
        }

        /**
         * @ngdoc method
         * @name attachColumnListeners
         * @methodOf data-prep.datagrid.service:DatagridExternalService
         * @description Attach header selection listeners
         */
        function attachColumnListeners() {
            grid.onHeaderClick.subscribe(function(e, args) {
                var columnId = args.column.id;
                var column = _.find(grid.getColumns(), function(column) {
                    return column.id === columnId;
                });
                updateSuggestionPanel(column);
            });
        }

        /**
         * @ngdoc method
         * @name attachGridScroll
         * @methodOf data-prep.datagrid.service:DatagridExternalService
         * @description Attach grid scroll listener. It will update the displayed range for preview
         */
        function attachGridScrollListener() {
            grid.onScroll.subscribe(function() {
                PreviewService.gridRangeIndex = grid.getRenderedRange();
            });
        }

        /**
         * @ngdoc method
         * @name init
         * @methodOf data-prep.datagrid.service:DatagridExternalService
         * @param {object} newGrid The new grid
         * @description Initialize the grid
         */
        function init(newGrid) {
            grid = newGrid;
            attachCellListeners();
            attachColumnListeners();
            attachGridScrollListener();
        }
    }

    angular.module('data-prep.datagrid')
        .service('DatagridExternalService', DatagridExternalService);
})();