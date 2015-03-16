describe('Dataset grid service', function() {
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

    beforeEach(module('data-prep.services.dataset'));

    it('should init visibility flag', inject(function(DatasetGridService) {
        //then
        expect(DatasetGridService.visible).toBe(false);
    }));

    it('should set visibility flag to true', inject(function(DatasetGridService) {
        //when
        DatasetGridService.show();

        //then
        expect(DatasetGridService.visible).toBe(true);
    }));

    it('should set visibility flag to false', inject(function(DatasetGridService) {
        //given
        DatasetGridService.visible = true;

        //when
        DatasetGridService.hide();

        //then
        expect(DatasetGridService.visible).toBe(false);
    }));

    it('should set metadata and data', inject(function(DatasetGridService) {
        //given
        var metadata = {name: 'my dataset'};
        var data = {columns: [], records: []};

        //when
        DatasetGridService.setDataset(metadata, data);

        //then
        expect(DatasetGridService.metadata).toBe(metadata);
        expect(DatasetGridService.data).toBe(data);
    }));

    it('should update data records', inject(function(DatasetGridService) {
        //given
        DatasetGridService.metadata = {name: 'my dataset'};
        DatasetGridService.data = {columns: [], records: []};

        var records = [{col: 'value'}];

        //when
        DatasetGridService.updateRecords(records);

        //then
        expect(DatasetGridService.data.records).toBe(records);
    }));

    it('should return every column id', inject(function(DatasetGridService) {
        //given
        DatasetGridService.data = {columns: [
            {id: 'col1', type: 'string'},
            {id: 'col2', type: 'numeric'},
            {id: 'col3', type: 'integer'},
            {id: 'col4', type: 'float'},
            {id: 'col5', type: 'double'},
            {id: 'col6', type: 'boolean'},
            {id: 'col7', type: 'string'}
        ], records: []};

        //when
        var allCols = DatasetGridService.getColumns(false);

        //then
        expect(allCols).toEqual(['col1', 'col2', 'col3', 'col4', 'col5', 'col6', 'col7']);
    }));

    it('should add string contains filter', inject(function(DatasetGridService) {
        //given
        expect(DatasetGridService.filters.length).toBe(0);

        //when
        DatasetGridService.addContainFilter('col1', 'toto');

        //then
        expect(DatasetGridService.filters.length).toBe(1);
        var predicate = DatasetGridService.filters[0];
        expect(predicate({col1: 'mon toto'})).toBe(true);
        expect(predicate({col1: 'ma tata'})).toBe(false);
    }));

    it('should set successive filters to DataView', inject(function(DatasetGridService) {
        //given
        var dataViewMock = new DataViewMock();
        DatasetGridService.dataView = dataViewMock;

        //when
        DatasetGridService.addContainFilter('col1', 'toto');
        DatasetGridService.addContainFilter('col2', 'toto');

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
});