(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.datagrid.service:DatagridTooltipService
     * @description Datagrid private service that manage the grid tooltip
     * @requires data-prep.datagrid.service:DatagridStyleService
     * @requires data-prep.services.playground.service:DatagridService
     */
    function DatagridTooltipService($timeout, DatagridStyleService, DatagridService) {
        var grid;
        var tooltipPromise, tooltipHidePromise;
        var tooltipDelay = 300;

        var service = {
            showTooltip: false,
            tooltip: {},
            tooltipRuler: null,
            init: init
        };
        return service;

        //--------------------------------------------------------------------------------------------------------------

        /**
         * @ngdoc method
         * @name cancelTooltip
         * @methodOf data-prep.datagrid.service:DatagridTooltipService
         * @description Cancel the current tooltip promise
         */
        function cancelTooltip() {
            if(tooltipPromise) {
                $timeout.cancel(tooltipPromise);
            }
        }

        /**
         * @ngdoc method
         * @name createTooltip
         * @methodOf data-prep.datagrid.service:DatagridTooltipService
         * @param {object} record The current record (only used if tooltip is used as an value editor, may be removed)
         * @param {string} colId The column id (only used if tooltip is used as an value editor, may be removed)
         * @param {object} position The position where to display it {x: number, y: number}
         * @param {String} htmlStr The html string to be displayed in the tooltip box
         * @description Update the tooltip component and display with a delay
         */
        function createTooltip(record, colId, position, htmlStr) {
            tooltipPromise = $timeout(function() {
                service.tooltip = {
                    record: record,
                    position: position,
                    colId: colId,
                    htmlStr: htmlStr
                };
                service.showTooltip = true;
            }, tooltipDelay);
        }

        /**
         * @ngdoc method
         * @name updateTooltip
         * @methodOf data-prep.datagrid.service:DatagridTooltipService
         * @param {object} record The current record (only used if tooltip is used as an value editor, may be removed)
         * @param {string} colId The column id (only used if tooltip is used as an value editor, may be removed)
         * @param {object} position The position where to display it {x: number, y: number}
         * @param {String} htmlStr The HTML string to be displayed in the tooltip.
         * @description Cancel the old tooltip promise if necessary and create a new one
         */
        function updateTooltip(record, colId, position, htmlStr) {
            cancelTooltip();
            createTooltip(record, colId, position, htmlStr);
        }

        /**
         * @ngdoc method
         * @name hideTooltip
         * @methodOf data-prep.datagrid.service:DatagridTooltipService
         * @description Cancel the old tooltip promise if necessary and hide the tooltip
         */
        function hideTooltip() {
            cancelTooltip();
            if(service.showTooltip) {
                tooltipHidePromise = $timeout(function() {
                    service.showTooltip = false;
                });
            }
        }

        //show tooltips only if not empty and width is bigger than cell
        /**
         * @ngdoc method
         * @name shouldShowTooltip
         * @methodOf data-prep.datagrid.service:DatagridTooltipService
         * @description Check if the text should be shown in a tooltip (content too long)
         * @param {string} text the text to display
         * @param {object} cell The cell containing the text
         */
        function shouldShowTooltip(text, cell) {
            if(text === '') {
                return false;
            }

            var ruler = service.tooltipRuler;

            ruler.text(text);
            var box = grid.getCellNodeBox(cell.row, cell.cell);

            // return if the content is bigger than the displayed box by computing the diff between the displayed box
            // and the hidden tooltip ruler size minus the cell padding
            return (box.right - box.left - 11 ) <= ruler.width() || (box.bottom - box.top) < ruler.height();
        }

        /**
         * @ngdoc method
         * @name attachTooltipListener
         * @methodOf data-prep.datagrid.service:DatagridTooltipService
         * @description Attach cell hover for tooltips listeners
         */
        function attachTooltipListener() {
            //show tooltip on hover
            grid.onMouseEnter.subscribe(function(e) {
                var cell = grid.getCellFromEvent(e);
                var row = cell.row;
                var column = grid.getColumns()[cell.cell];
                var item = DatagridService.dataView.getItem(row);

                if (!shouldShowTooltip(item[column.id], cell)) {
                    return;
                }

                var position = {
                    x: e.clientX,
                    y: e.clientY
                };

                updateTooltip(item, column.id, position, DatagridStyleService.computeHTMLForLeadingOrTrailingHiddenChars(item[column.id]));
            });

            //hide tooltip on leave
            grid.onMouseLeave.subscribe(function() {
                hideTooltip();
            });
        }

        /**
         * @ngdoc method
         * @name init
         * @methodOf data-prep.datagrid.service:DatagridTooltipService
         * @param {object} newGrid The new grid
         * @description Initialize the grid and attach the tooltips listeners
         */
        function init(newGrid) {
            grid = newGrid;
            attachTooltipListener();
        }
    }

    angular.module('data-prep.datagrid')
        .service('DatagridTooltipService', DatagridTooltipService);
})();