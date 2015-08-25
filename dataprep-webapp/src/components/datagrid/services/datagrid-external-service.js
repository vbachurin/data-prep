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
        var suggestionTimeout;
        var scrollTimeout;
        var lastSelectedColumn;

        return {
            init: init,
            updateSuggestionPanel: updateSuggestionPanel
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
            if(column.tdpColMetadata === lastSelectedColumn) {
                return;
            }

            clearTimeout(suggestionTimeout);
            lastSelectedColumn = column.tdpColMetadata;

            suggestionTimeout = setTimeout(function() {
                var columnMetadata = column.tdpColMetadata;
                StatisticsService.processData(columnMetadata);
                ColumnSuggestionService.setColumn(columnMetadata); // this will trigger a digest after REST call
            }, 200);
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
                else {
                    lastSelectedColumn = null;
                }
            });
        }

        /**
         * @ngdoc method
         * @name attachColumnCallback
         * @methodOf data-prep.datagrid.service:DatagridExternalService
         * @description attachColumnListeners callback
         */
        function attachColumnCallback(args) {
            var columnId = args.column.id;
            var column = _.find(grid.getColumns(), {id: columnId});
            updateSuggestionPanel(column);
        }

        /**
         * @ngdoc method
         * @name attachColumnListeners
         * @methodOf data-prep.datagrid.service:DatagridExternalService
         * @description Attach header selection listeners on right click or left click
         */
        function attachColumnListeners() {
            grid.onHeaderContextMenu.subscribe(function(e, args) {
                attachColumnCallback(args);
            });

            grid.onHeaderClick.subscribe(function(e, args) {
                attachColumnCallback(args);
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
                clearTimeout(scrollTimeout);
                scrollTimeout = setTimeout(function() {
                    PreviewService.gridRangeIndex = grid.getRenderedRange();
                }, 200);
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