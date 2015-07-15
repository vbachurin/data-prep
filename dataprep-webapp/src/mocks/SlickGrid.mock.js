/* jshint ignore:start */
function SlickGridMock () {
    return {
        //mock functions
        initCellMock: function(cell, box) {
            //cell : the SlickGrid cell infos. Ex: {row: 1, cell: 2}
            //box : the SlickGrid cell box. Ex: {left: 300, right: 500, top: 10, bottom: 40}
            this.cell = cell;
            this.box = box;
        },
        initColumnsMock: function(columns) {
            this.columns = columns;
        },
        initRenderedRangeMock: function(range) {
            this.range = range;
        },
        initActiveCellMock: function(activeCell) {
            this.activeCell = activeCell;
        },
        cssStyleConfig: {},

        //slickGrid events
        onActiveCellChanged: {subscribe: function() {}},
        onClick: {subscribe: function() {}},
        onColumnsReordered: {subscribe: function() {}},
        onColumnsResized: {subscribe: function() {}},
        onHeaderClick: {subscribe: function() {}},
        onMouseEnter: {subscribe: function() {}},
        onMouseLeave: {subscribe: function() {}},
        onScroll: {subscribe: function() {}},

        //slickGrid functions
        autosizeColumns: function() {},
        invalidate: function() {},
        invalidateRows: function() {},
        getActiveCell: function() {return this.activeCell;},
        getColumns: function() {return this.columns;},
        getCellFromEvent: function() {return this.cell;},
        getCellNodeBox: function() {return this.box;},
        getRenderedRange: function() {return this.range;},
        gotoCell: function() {},
        render: function() {},
        resetActiveCell: function() {},
        resizeCanvas: function() {},
        setColumns: function(columns) {this.columns = columns;},
        setCellCssStyles: function(cssStyle, config) {this.cssStyleConfig[cssStyle] = config;},
        scrollRowToTop: function() {},
        updateRowCount: function() {}
    };
}
/* jshint ignore:end */