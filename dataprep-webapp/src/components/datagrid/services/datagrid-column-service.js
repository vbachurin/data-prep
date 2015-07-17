(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.datagrid.service:DatagridColumnService
     * @description Datagrid private service that manage the grid columns and columns headers
     * LEXICON :
     * <ul>
     *     <li>Column : the slick grid column</li>
     *     <li>Header : the datagrid header directive. The header created by SlickGrid will be called by 'SlickGird Header'</li>
     * </ul>
     * @requires data-prep.datagrid.service:DatagridStyleService
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function DatagridColumnService($rootScope, $compile, DatagridStyleService, DatagridService, ConverterService) {
        var grid;

        var gridHeaderPreviewTemplate =
            '<div class="grid-header <%= diffClass %>">' +
            '   <div class="grid-header-title dropdown-button ng-binding"><%= name %></div>' +
            '       <div class="grid-header-type ng-binding"><%= simpleType %></div>' +
            '   </div>' +
            '<div class="quality-bar"><div class="record-unknown"></div></div>';

        var gridHeaderTemplate = '<div id="datagrid-header-<%= index %>"></div>';

        var service = {
            colHeaderElements: [],
            init: init,
            updateColumns: updateColumns
        };
        return service;

        //------------------------------------------------------------------------------------------------------
        //-----------------------------------------------GRID COLUMNS-------------------------------------------
        //------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name createColumnItem
         * @methodOf data-prep.datagrid.service:DatagridColumnService
         * @param {object} col The column metadata to adapt
         * @param {number} index The column index
         * @param {boolean} preview Flag that indicates if we are in the preview mode
         * @description Adapt column metadata to slick column.
         * <ul>
         *     <li>Non preview mode : The div id depending on index is important. It is used as insertion point for the header directive.</li>
         *     <li>Preview mode : we inject directly a fake header</li>
         * </ul>
         * @returns {object} The adapted column item
         */
        function createColumnItem(col, index, preview) {
            var template;
            if(preview) {
                template =  _.template(gridHeaderPreviewTemplate)({
                    name: col.name,
                    diffClass: DatagridStyleService.getColumnPreviewStyle(col),
                    simpleType: col.domain ? col.domain : ConverterService.simplifyType(col.type)
                });
            }
            else {
                template =  _.template(gridHeaderTemplate)({index: index});
            }

            return {
                id: col.id,
                field: col.id,
                name: template,
                formatter: DatagridStyleService.columnFormatter(col),
                minWidth: 80,
                tdpColMetadata: col
            };
        }

        /**
         * @ngdoc method
         * @name updateColumns
         * @methodOf data-prep.datagrid.service:DatagridColumnService
         * @param {object[]} columnsMetadata Columns details
         * @param {boolean} preview Flag that indicates if we are in preview mode
         * @param {boolean} renewAllColumns Force recreation of every column
         * @description [PRIVATE] Two modes :
         * <ul>
         *     <li>Preview : we save actual headers and simulate fake headers with the new preview columns</li>
         *     <li>Classic : we map each column to a header. This header can be a reused header if the column was
         * the same as before, or a new created one otherwise.</li>
         * </ul>
         */
        function updateColumns(columnsMetadata, preview, renewAllColumns) {
            //detach current headers elements but they still exists internally
            _.forEach(service.colHeaderElements, function(header) {
                header.element.detach();
            });

            //create and set new SlickGrid columns
            var columns = _.map(columnsMetadata, function (col, index) {
                return createColumnItem(col, index, preview);
            });
            grid.setColumns(columns);

            //insert reused or created datagrid headers
            if(!preview) {
                //map every column to a header
                var finalColHeaderElements = _.map(columnsMetadata, function (col) {
                    //find saved header corresponding to column
                    var header = renewAllColumns ? null : _.find(service.colHeaderElements, function (colHeader) {
                        return colHeader.column.id === col.id;
                    });
                    if (header) {
                        header.scope.column = col;
                    }
                    //or create a new one if no corresponding one
                    else {
                        header = createHeader(col);
                    }

                    return header;
                });

                //destroy the unused headers
                var diff = _.difference(service.colHeaderElements, finalColHeaderElements);
                _.forEach(diff, function (header) {
                    header.scope.$destroy();
                    header.element.remove();
                });

                service.colHeaderElements = finalColHeaderElements;
            }
        }

        //------------------------------------------------------------------------------------------------------
        //-----------------------------------------------GRID HEADERS-------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name createHeader
         * @methodOf data-prep.datagrid.service:DatagridColumnService
         * @description [PRIVATE] Create a column header object containing
         * <ul>
         *     <li>the element directive</li>
         *     <li>The directive scope</li>
         *     <li>The column metadata</li>
         * </ul>
         */
        function createHeader(col) {
            var headerScope = $rootScope.$new(true);
            headerScope.column = col;
            var headerElement = angular.element('<datagrid-header column="column"></datagrid-header>');
            $compile(headerElement)(headerScope);

            return {
                element : headerElement,
                scope : headerScope,
                column: col
            };
        }

        /**
         * @ngdoc method
         * @name clearHeaders
         * @methodOf data-prep.datagrid.service:DatagridColumnService
         * @description [PRIVATE] Clear the actual headers directives. It destroy the existing scopes and elements.
         */
        function clearHeaders() {
            _.forEach(service.colHeaderElements, function (header) {
                header.scope.$destroy();
                header.element.remove();
            });
            service.colHeaderElements = [];
        }

        /**
         * @ngdoc method
         * @name renewDatagridHeaders
         * @methodOf data-prep.datagrid.service:DatagridColumnService
         * @description [PRIVATE] Clear and recreate the column headers directives (dropdown actions and quality bars).
         The columns are from {@link data-prep.services.playground.service:DatagridService DatagridService}
         */
        function renewDatagridHeaders() {
            clearHeaders();

            _.forEach(DatagridService.data.columns, function (col) {
                var header = createHeader(col);
                service.colHeaderElements.push(header);
            });
        }

        //------------------------------------------------------------------------------------------------------
        //--------------------------------------------------INIT------------------------------------------------
        //------------------------------------------------------------------------------------------------------
        /**
         * @ngdoc method
         * @name attachColumnReorderListeners
         * @methodOf data-prep.datagrid.service:DatagridColumnService
         * @description [PRIVATE] Attach listeners for header reorder. The handler destroy the headers and recreate them.
         * The behavior is necessary because SlickGrid remove the header and append them again. But by removing them, the
         * header directives
         */
        function attachColumnReorderListeners() {
            grid.onColumnsReordered.subscribe(renewDatagridHeaders);
        }

        /**
         * @ngdoc method
         * @name init
         * @methodOf data-prep.datagrid.service:DatagridColumnService
         * @param {object} newGrid The new grid
         * @description Initialize the grid and attach the column listeners
         */
        function init(newGrid) {
            grid = newGrid;
            attachColumnReorderListeners();
        }
    }

    angular.module('data-prep.datagrid')
        .service('DatagridColumnService', DatagridColumnService);
})();