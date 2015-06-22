describe('Datagrid service', function() {
    'use strict';

    function DataViewMock(){
        var filter, filterArgs;

        this.beginUpdate = function() {};
        this.endUpdate = function() {};

        this.setFilterArgs = function(args) {
            filterArgs = args;
        };

        this.setFilter = function(args) {
            filter = args;
        };

        this.filter = function(data) {
            return filter(data, filterArgs);
        };
    }

    beforeEach(module('data-prep.services.playground'));

    it('should set metadata and data', inject(function(DatagridService) {
        //given
        var metadata = {name: 'my dataset'};
        var data = {columns: [], records: []};

        //when
        DatagridService.setDataset(metadata, data);

        //then
        expect(DatagridService.metadata).toBe(metadata);
        expect(DatagridService.data).toBe(data);
    }));

    it('should update data records', inject(function(DatagridService) {
        //given
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {columns: [], records: []};

        var data = {'records': [{col: 'value'}]};

        //when
        DatagridService.updateData(data);

        //then
        expect(DatagridService.data.records).toBe(data.records);
    }));

    it('should return every column id', inject(function(DatagridService) {
        //given
        DatagridService.data = {columns: [
            {id: 'col1', name: 'column 1', type: 'string'},
            {id: 'col2', name: 'column 2', type: 'numeric'},
            {id: 'col3', name: 'column 3', type: 'integer'},
            {id: 'col4', name: 'column 4', type: 'float'},
            {id: 'col5', name: 'column 5', type: 'double'},
            {id: 'col6', name: 'column 6', type: 'boolean'},
            {id: 'col7', name: 'column 7', type: 'string'}
        ], records: []};

        //when
        var allCols = DatagridService.getColumns(false, false);

        //then
        expect(allCols).toEqual([{id:'col1', name: 'column 1'},
                                 {id:'col2', name: 'column 2'},
                                 {id:'col3', name: 'column 3'},
                                 {id:'col4', name: 'column 4'},
                                 {id:'col5', name: 'column 5'},
                                 {id:'col6', name: 'column 6'},
                                 {id:'col7', name: 'column 7'}]);
    }));

    it('should return non numeric col ids', inject(function(DatagridService) {
        //given
        DatagridService.data = {columns: [
            {id: 'col1', name: 'column 1', type: 'string'},
            {id: 'col2', name: 'column 2', type: 'numeric'},
            {id: 'col3', name: 'column 3', type: 'integer'},
            {id: 'col4', name: 'column 4', type: 'float'},
            {id: 'col5', name: 'column 5', type: 'double'},
            {id: 'col6', name: 'column 6', type: 'boolean'},
            {id: 'col7', name: 'column 7', type: 'string'}
        ], records: []};

        //when
        var allCols = DatagridService.getColumns(true, false);

        //then
        expect(allCols).toEqual([{id:'col1', name: 'column 1'},
                                 {id:'col6', name: 'column 6'},
                                 {id:'col7', name: 'column 7'}]);
    }));

    it('should return non boolean col ids', inject(function(DatagridService) {
        //given
        DatagridService.data = {columns: [
            {id: 'col1', name: 'column 1', type: 'string'},
            {id: 'col2', name: 'column 2', type: 'numeric'},
            {id: 'col3', name: 'column 3', type: 'integer'},
            {id: 'col4', name: 'column 4', type: 'float'},
            {id: 'col5', name: 'column 5', type: 'double'},
            {id: 'col6', name: 'column 6', type: 'boolean'},
            {id: 'col7', name: 'column 7', type: 'string'}
        ], records: []};

        //when
        var allCols = DatagridService.getColumns(false, true);

        //then
        expect(allCols).toEqual([{id:'col1', name: 'column 1'},
                                 {id:'col2', name: 'column 2'},
                                 {id:'col3', name: 'column 3'},
                                 {id:'col4', name: 'column 4'},
                                 {id:'col5', name: 'column 5'},
                                 {id:'col7', name: 'column 7'}]);
    }));

    it('should return non boolean and non numeric col ids', inject(function(DatagridService) {
        //given
        DatagridService.data = {columns: [
            {id: 'col1', name: 'column 1', type: 'string'},
            {id: 'col2', name: 'column 2', type: 'numeric'},
            {id: 'col3', name: 'column 3', type: 'integer'},
            {id: 'col4', name: 'column 4', type: 'float'},
            {id: 'col5', name: 'column 5', type: 'double'},
            {id: 'col6', name: 'column 6', type: 'boolean'},
            {id: 'col7', name: 'column 7', type: 'string'}
        ], records: []};

        //when
        var allCols = DatagridService.getColumns(true, true);

        //then
        expect(allCols).toEqual([{id:'col1', name: 'column 1'},
                                 {id:'col7', name: 'column 7'}]);
    }));

    it('should add filter', inject(function(DatagridService) {
        //given
        expect(DatagridService.filters.length).toBe(0);
        var filterFn = function(item) {
            return item.col1.indexOf('toto') > -1;
        };

        //when
        DatagridService.addFilter(filterFn);

        //then
        expect(DatagridService.filters.length).toBe(1);
        var predicate = DatagridService.filters[0];
        expect(predicate({col1: 'mon toto'})).toBe(true);
        expect(predicate({col1: 'ma tata'})).toBe(false);
    }));

    it('should set successive filters to DataView', inject(function(DatagridService) {
        //given
        var dataViewMock = new DataViewMock();
        DatagridService.dataView = dataViewMock;
        var filterFnCol1 = function(item) {
            return item.col1.indexOf('toto') > -1;
        };
        var filterFnCol2 = function(item) {
            return item.col2.indexOf('toto') > -1;
        };

        //when
        DatagridService.addFilter(filterFnCol1);
        DatagridService.addFilter(filterFnCol2);

        //then
        expect(dataViewMock.filter({
            col1: 'mon toto', col2: 'toto tata titi'
        })).toBe(true);
        expect(dataViewMock.filter({
            col1: 'mon tutu', col2: 'toto tata titi'
        })).toBe(false);
        expect(dataViewMock.filter({
            col1: 'mon toto', col2: 'tutu tata titi'
        })).toBe(false);
    }));

    it('should reset filters', inject(function(DatagridService) {
        //given
        DatagridService.filters = [{}, {}];

        //when
        DatagridService.resetFilters();

        //then
        expect(DatagridService.filters.length).toBe(0);
    }));

    it('should remove filter', inject(function(DatagridService) {
        //given
        var filterFnCol1 = function(item) {
            return item.col1.indexOf('toto') > -1;
        };
        var filterFnCol2 = function(item) {
            return item.col2.indexOf('toto') > -1;
        };
        DatagridService.addFilter(filterFnCol1);
        DatagridService.addFilter(filterFnCol2);
        expect(DatagridService.filters.length).toBe(2);

        //when
        DatagridService.removeFilter(filterFnCol1);

        //then
        expect(DatagridService.filters.length).toBe(1);
        expect(DatagridService.filters[0]).toBe(filterFnCol2);
    }));

    it('should do nothing on remove if filter is unknown', inject(function(DatagridService) {
        //given
        var filterFnCol1 = function(item) {
            return item.col1.indexOf('toto') > -1;
        };
        var filterFnCol2 = function(item) {
            return item.col2.indexOf('toto') > -1;
        };
        DatagridService.addFilter(filterFnCol1);

        expect(DatagridService.filters.length).toBe(1);

        //when
        DatagridService.removeFilter(filterFnCol2);

        //then
        expect(DatagridService.filters.length).toBe(1);
        expect(DatagridService.filters[0]).toBe(filterFnCol1);
    }));

    it('should return the rows containing searched value', inject(function(DatagridService) {
        //given
        DatagridService.setDataset({}, {columns: [], records: [
            {text: 'mon toto est ici'},
            {text: 'ma tata est la'},
            {text: 'la tata est ici'},
            {text: 'mon toto est la'},
            {text: 'mi titi est ici'},
            {text: 'mi titi est la'}
        ]});

        //when
        var rowsId = DatagridService.getRowsContaining('text', 'la');

        //then
        expect(rowsId).toEqual([1, 2, 3, 5]);
    }));

    it('should update filter', inject(function(DatagridService) {
        //given
        var filterFnCol1 = function(item) {
            return item.col1.indexOf('toto') > -1;
        };
        var filterFnCol2 = function(item) {
            return item.col2.indexOf('toto') > -1;
        };
        var newFilterFnCol2 = function(item) {
            return item.col2.indexOf('tata') > -1;
        };
        DatagridService.addFilter(filterFnCol1);
        DatagridService.addFilter(filterFnCol2);
        expect(DatagridService.filters.length).toBe(2);

        //when
        DatagridService.updateFilter(filterFnCol2, newFilterFnCol2);

        //then
        expect(DatagridService.filters.length).toBe(2);
        expect(DatagridService.filters[0]).toBe(filterFnCol1);
        expect(DatagridService.filters[1]).toBe(newFilterFnCol2);
    }));
});