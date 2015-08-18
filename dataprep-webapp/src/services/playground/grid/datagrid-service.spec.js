describe('Datagrid service', function () {
    'use strict';

    var originalData = {
        records: [
            {tdpId: 0, firstname: 'Tata'},
            {tdpId: 1, firstname: 'Tete'},
            {tdpId: 2, firstname: 'Titi'},
            {tdpId: 3, firstname: 'Toto'},
            {tdpId: 4, firstname: 'Tutu'},
            {tdpId: 5, firstname: 'Tyty'},
            {tdpId: 6, firstname: 'Papa'},
            {tdpId: 7, firstname: 'Pepe'},
            {tdpId: 8, firstname: 'Pipi'},
            {tdpId: 9, firstname: 'Popo'},
            {tdpId: 10, firstname: 'Pupu'},
            {tdpId: 11, firstname: 'Pypy'}
        ],
        columns: [{id: '0000', name: 'lastname'}, {id: '0001', name: 'firstname'}]
    };

    //diff result corresponding to gridRangeIndex
    var diff = {
        records: [
            {tdpId: 1, firstname: 'Tete'},
            {tdpId: 2, firstname: 'Titi Bis', __tdpRowDiff: 'new'}, //insert new row
            {tdpId: 3, firstname: 'Toto', __tdpRowDiff: 'delete'}, //row is deleted in preview
            {tdpId: 6, firstname: 'Papa'},
            {tdpId: 7, firstname: 'Pepe 2', __tdpDiff: {firstname: 'update'}}, //firstname is updated in preview
            {tdpId: 8, firstname: 'Pipi'}
        ],
        columns: [{id: '0000', name: 'lastname'}, {id: '0001', name: 'firstname'}]
    };

    function DataViewMock() {
        var filter, filterArgs;

        this.beginUpdate = function () {
        };
        this.endUpdate = function () {
        };

        this.setFilterArgs = function (args) {
            filterArgs = args;
        };

        this.setFilter = function (args) {
            filter = args;
        };

        this.filter = function (data) {
            return filter(data, filterArgs);
        };

        this.getIdxById = function (tdpId) {
            var record = _.find(originalData.records, {tdpId: tdpId});
            return record ? originalData.records.indexOf(record) : null;
        };
        this.getItemById = function(tdpId) {
            return _.find(originalData.records, {tdpId: tdpId});
        };

        this.insertItem = function() {};
        this.deleteItem = function() {};
        this.updateItem = function() {};
    }

    beforeEach(module('data-prep.services.playground'));

    it('should set metadata and data', inject(function (DatagridService) {
        //given
        var metadata = {name: 'my dataset'};
        var data = {columns: [], records: []};

        //when
        DatagridService.setDataset(metadata, data);

        //then
        expect(DatagridService.metadata).toBe(metadata);
        expect(DatagridService.data).toBe(data);
    }));

    it('should reset focused column', inject(function (DatagridService) {
        //given
        var metadata = {name: 'my dataset'};
        var data = {columns: [], records: []};
        DatagridService.focusedColumn = '0001';

        //when
        DatagridService.setDataset(metadata, data);

        //then
        expect(DatagridService.focusedColumn).toBe(null);
    }));

    it('should update data records', inject(function (DatagridService) {
        //given
        DatagridService.metadata = {name: 'my dataset'};
        DatagridService.data = {columns: [], records: []};

        var data = {columns: [], 'records': [{tdpId: 1, col: 'value'}]};

        //when
        DatagridService.updateData(data);

        //then
        expect(DatagridService.data.records).toBe(data.records);
    }));

    it('should navigate to the column having the highest Id', inject(function (DatagridService) {
        //given
        DatagridService.data = {
            columns: [
                {id: '0000', name: 'column 1', type: 'string'},
                {id: '0001', name: 'column 2', type: 'numeric'},
                {id: '0002', name: 'column 2', type: 'numeric'},
                {id: '0003', name: 'column 3', type: 'integer'}], records: []
        };

        //the result of the 2nd column duplication
        var data = {
            columns: [
                {id: '0000', name: 'column 1', type: 'string'},
                {id: '0001', name: 'column 2', type: 'numeric'},
                {id: '0004', name: 'column 1', type: 'string'},
                {id: '0002', name: 'column 2', type: 'numeric'},
                {id: '0003', name: 'column 3', type: 'integer'}], records: []
        };

        //when
        DatagridService.updateData(data);

        //then
        expect(DatagridService.focusedColumn).toBe('0004');
    }));

    it('should return every column id', inject(function (DatagridService) {
        //given
        DatagridService.data = {
            columns: [
                {id: 'col1', name: 'column 1', type: 'string'},
                {id: 'col2', name: 'column 2', type: 'numeric'},
                {id: 'col3', name: 'column 3', type: 'integer'},
                {id: 'col4', name: 'column 4', type: 'float'},
                {id: 'col5', name: 'column 5', type: 'double'},
                {id: 'col6', name: 'column 6', type: 'boolean'},
                {id: 'col7', name: 'column 7', type: 'string'}
            ], records: []
        };

        //when
        var allCols = DatagridService.getColumns(false, false);

        //then
        expect(allCols).toEqual([{id: 'col1', name: 'column 1'},
            {id: 'col2', name: 'column 2'},
            {id: 'col3', name: 'column 3'},
            {id: 'col4', name: 'column 4'},
            {id: 'col5', name: 'column 5'},
            {id: 'col6', name: 'column 6'},
            {id: 'col7', name: 'column 7'}]);
    }));

    it('should return non numeric col ids', inject(function (DatagridService) {
        //given
        DatagridService.data = {
            columns: [
                {id: 'col1', name: 'column 1', type: 'string'},
                {id: 'col2', name: 'column 2', type: 'numeric'},
                {id: 'col3', name: 'column 3', type: 'integer'},
                {id: 'col4', name: 'column 4', type: 'float'},
                {id: 'col5', name: 'column 5', type: 'double'},
                {id: 'col6', name: 'column 6', type: 'boolean'},
                {id: 'col7', name: 'column 7', type: 'string'}
            ], records: []
        };

        //when
        var allCols = DatagridService.getColumns(true, false);

        //then
        expect(allCols).toEqual([{id: 'col1', name: 'column 1'},
            {id: 'col6', name: 'column 6'},
            {id: 'col7', name: 'column 7'}]);
    }));

    it('should return non boolean col ids', inject(function (DatagridService) {
        //given
        DatagridService.data = {
            columns: [
                {id: 'col1', name: 'column 1', type: 'string'},
                {id: 'col2', name: 'column 2', type: 'numeric'},
                {id: 'col3', name: 'column 3', type: 'integer'},
                {id: 'col4', name: 'column 4', type: 'float'},
                {id: 'col5', name: 'column 5', type: 'double'},
                {id: 'col6', name: 'column 6', type: 'boolean'},
                {id: 'col7', name: 'column 7', type: 'string'}
            ], records: []
        };

        //when
        var allCols = DatagridService.getColumns(false, true);

        //then
        expect(allCols).toEqual([{id: 'col1', name: 'column 1'},
            {id: 'col2', name: 'column 2'},
            {id: 'col3', name: 'column 3'},
            {id: 'col4', name: 'column 4'},
            {id: 'col5', name: 'column 5'},
            {id: 'col7', name: 'column 7'}]);
    }));

    it('should return non boolean and non numeric col ids', inject(function (DatagridService) {
        //given
        DatagridService.data = {
            columns: [
                {id: 'col1', name: 'column 1', type: 'string'},
                {id: 'col2', name: 'column 2', type: 'numeric'},
                {id: 'col3', name: 'column 3', type: 'integer'},
                {id: 'col4', name: 'column 4', type: 'float'},
                {id: 'col5', name: 'column 5', type: 'double'},
                {id: 'col6', name: 'column 6', type: 'boolean'},
                {id: 'col7', name: 'column 7', type: 'string'}
            ], records: []
        };

        //when
        var allCols = DatagridService.getColumns(true, true);

        //then
        expect(allCols).toEqual([{id: 'col1', name: 'column 1'},
            {id: 'col7', name: 'column 7'}]);
    }));

    it('should return numeric columns', inject(function(DatagridService) {
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
        var allCols = DatagridService.getNumberColumns();

        //then
        expect(allCols).toEqual([
            {id: 'col2', name: 'column 2', type: 'numeric'},
            {id: 'col3', name: 'column 3', type: 'integer'},
            {id: 'col4', name: 'column 4', type: 'float'},
            {id: 'col5', name: 'column 5', type: 'double'}]);
    }));

    it('should return numeric columns excluding a specific column', inject(function(DatagridService) {
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
        var allCols = DatagridService.getNumberColumns('col2');

        //then
        expect(allCols).toEqual([
            {id: 'col3', name: 'column 3', type: 'integer'},
            {id: 'col4', name: 'column 4', type: 'float'},
            {id: 'col5', name: 'column 5', type: 'double'}]);
    }));

    it('should add filter', inject(function (DatagridService) {
        //given
        expect(DatagridService.filters.length).toBe(0);
        var filterFn = function (item) {
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

    it('should set successive filters to DataView', inject(function (DatagridService) {
        //given
        var dataViewMock = new DataViewMock();
        DatagridService.dataView = dataViewMock;
        var filterFnCol1 = function (item) {
            return item.col1.indexOf('toto') > -1;
        };
        var filterFnCol2 = function (item) {
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

    it('should return filter that executes all filters', inject(function (DatagridService) {
        //given
        var dataViewMock = new DataViewMock();
        DatagridService.dataView = dataViewMock;
        var filterFnCol1 = function (item) {
            return item.col1.indexOf('toto') > -1;
        };
        var filterFnCol2 = function (item) {
            return item.col2.indexOf('toto') > -1;
        };

        DatagridService.addFilter(filterFnCol1);
        DatagridService.addFilter(filterFnCol2);

        //when
        var superFilter = DatagridService.getAllFiltersFn();

        //then
        expect(superFilter({
            col1: 'mon toto', col2: 'toto tata titi'
        })).toBe(true);
        expect(superFilter({
            col1: 'mon tutu', col2: 'toto tata titi'
        })).toBe(false);
        expect(superFilter({
            col1: 'mon toto', col2: 'tutu tata titi'
        })).toBe(false);
    }));

    it('should reset filters', inject(function (DatagridService) {
        //given
        DatagridService.filters = [{}, {}];

        //when
        DatagridService.resetFilters();

        //then
        expect(DatagridService.filters.length).toBe(0);
    }));

    it('should remove filter', inject(function (DatagridService) {
        //given
        var filterFnCol1 = function (item) {
            return item.col1.indexOf('toto') > -1;
        };
        var filterFnCol2 = function (item) {
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

    it('should do nothing on remove if filter is unknown', inject(function (DatagridService) {
        //given
        var filterFnCol1 = function (item) {
            return item.col1.indexOf('toto') > -1;
        };
        var filterFnCol2 = function (item) {
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

    it('should return the rows containing non empty searched value', inject(function (DatagridService) {
        //given
        DatagridService.setDataset({}, {
            columns: [], records: [
                {tdpId: 1, text: 'mon toto est ici'},
                {tdpId: 2, text: 'ma tata est la'},
                {tdpId: 3, text: 'la tata est ici'},
                {tdpId: 4, text: 'mon toto est la'},
                {tdpId: 5, text: 'mi titi est ici'},
                {tdpId: 6, text: 'mi titi est la'},
                {tdpId: 7, text: 'mi titi est ici'}
            ]
        });

        //when
        var rowsId = DatagridService.getSameContentConfig('text', 'mi titi est ici', 'myClass');

        //then
        expect(rowsId).toEqual({4: {text: 'myClass'}, 6: {text: 'myClass'}});
    }));

    it('should return the rows with empty value', inject(function (DatagridService) {
        //given
        DatagridService.setDataset({}, {
            columns: [], records: [
                {tdpId: 1, text: 'mon toto est ici'},
                {tdpId: 2, text: ''},
                {tdpId: 3, text: 'la tata est ici'},
                {tdpId: 4, text: 'mon toto est la'},
                {tdpId: 5, text: ''},
                {tdpId: 6, text: 'mi titi est la'}
            ]
        });

        //when
        var rowsId = DatagridService.getSameContentConfig('text', '', 'myClass');

        //then
        expect(rowsId).toEqual({1: {text: 'myClass'}, 4: {text: 'myClass'}});
    }));

    it('should update filter', inject(function (DatagridService) {
        //given
        var filterFnCol1 = function (item) {
            return item.col1.indexOf('toto') > -1;
        };
        var filterFnCol2 = function (item) {
            return item.col2.indexOf('toto') > -1;
        };
        var newFilterFnCol2 = function (item) {
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

    describe('preview operations', function () {
        beforeEach(inject(function (DatagridService) {
            var dataView = new DataViewMock();
            DatagridService.dataView = dataView;
            DatagridService.data = originalData;

            spyOn(dataView, 'beginUpdate').and.callThrough();
            spyOn(dataView, 'endUpdate').and.callThrough();
            spyOn(dataView, 'insertItem').and.callThrough();
            spyOn(dataView, 'deleteItem').and.callThrough();
            spyOn(dataView, 'updateItem').and.callThrough();
        }));

        it('should create executor that match the preview data', inject(function (DatagridService) {
            //when
            var executor = DatagridService.previewDataExecutor(diff);

            //then
            expect(executor.columns).toBe(diff.columns);
            expect(executor.preview).toBe(true);
            expect(executor.instructions).toEqual([
                { type: 'INSERT', row: { tdpId: 2, firstname: 'Titi Bis', __tdpRowDiff: 'new' }, index: 2 },
                { type: 'REPLACE', row: { tdpId: 3, firstname: 'Toto', __tdpRowDiff: 'delete' } },
                { type: 'REPLACE', row: { tdpId: 7, firstname: 'Pepe 2', __tdpDiff: { firstname: 'update' } } }
            ]);
        }));

        it('should apply nothing when executor is falsy', inject(function (DatagridService) {
            //given
            var executor = null;

            //when
            DatagridService.execute(executor);

            //then
            expect(DatagridService.dataView.insertItem).not.toHaveBeenCalled();
            expect(DatagridService.dataView.deleteItem).not.toHaveBeenCalled();
            expect(DatagridService.dataView.updateItem).not.toHaveBeenCalled();

            expect(DatagridService.data).toBe(originalData);
        }));

        it('should apply executor', inject(function (DatagridService) {
            //given
            var executor = {
                columns: diff.columns,
                preview: true,
                instructions: [
                    { type: 'INSERT', row: { tdpId: 2, firstname: 'Titi Bis', __tdpRowDiff: 'new' }, index: 2 },
                    { type: 'DELETE', row: { tdpId: 3, firstname: 'Toto', __tdpRowDiff: 'delete' } },
                    { type: 'REPLACE', row: { tdpId: 7, firstname: 'Pepe 2', __tdpDiff: { firstname: 'update' } } }
                ]
            };

            //when
            DatagridService.execute(executor);

            //then
            expect(DatagridService.dataView.insertItem).toHaveBeenCalledWith(2, { tdpId: 2, firstname: 'Titi Bis', __tdpRowDiff: 'new' });
            expect(DatagridService.dataView.deleteItem).toHaveBeenCalledWith(3);
            expect(DatagridService.dataView.updateItem).toHaveBeenCalledWith(7, { tdpId: 7, firstname: 'Pepe 2', __tdpDiff: { firstname: 'update' } });

        }));

        it('should return reverter on executor application', inject(function (DatagridService) {
            //given
            var executor = {
                columns: diff.columns,
                preview: true,
                instructions: [
                    { type: 'INSERT', row: { tdpId: 2, firstname: 'Titi Bis', __tdpRowDiff: 'new' }, index: 2 },
                    { type: 'DELETE', row: { tdpId: 3, firstname: 'Toto' } },
                    { type: 'REPLACE', row: { tdpId: 7, firstname: 'Pepe 2', __tdpDiff: { firstname: 'update' } } }
                ]
            };

            //when
            var reverter = DatagridService.execute(executor);

            //then
            expect(reverter.columns).toBe(originalData.columns);
            expect(reverter.preview).toBeFalsy();
            expect(reverter.instructions).toEqual([
                { type: 'DELETE', row: { tdpId: 2, firstname: 'Titi Bis', __tdpRowDiff: 'new' } },
                { type: 'INSERT', row: { tdpId: 3, firstname: 'Toto' }, index: 3 },
                { type: 'REPLACE', row: { tdpId: 7, firstname: 'Pepe' } }
            ]);
        }));

        it('should replace service data', inject(function (DatagridService) {
            //given
            var executor = {
                columns: diff.columns,
                preview: true,
                instructions: [
                    { type: 'INSERT', row: { tdpId: 2, firstname: 'Titi Bis', __tdpRowDiff: 'new' }, index: 2 },
                    { type: 'DELETE', row: { tdpId: 3, firstname: 'Toto' } },
                    { type: 'REPLACE', row: { tdpId: 7, firstname: 'Pepe 2', __tdpDiff: { firstname: 'update' } } }
                ]
            };

            //when
            DatagridService.execute(executor);

            //then
            expect(DatagridService.data).not.toBe(originalData);
            expect(DatagridService.data.preview).toBe(true);
            expect(DatagridService.data.columns).toBe(diff.columns);
            expect(DatagridService.data.records).toBe(originalData.records); // same array but modified by executor
        }));
    });
});