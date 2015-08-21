describe('Filter service', function() {
    'use strict';

    beforeEach(module('data-prep.services.filter'));
    beforeEach(inject(function(DatagridService) {
        spyOn(DatagridService, 'resetFilters').and.callFake(function() {});
        spyOn(DatagridService, 'addFilter').and.callFake(function() {});
        spyOn(DatagridService, 'removeFilter').and.callFake(function() {});
        spyOn(DatagridService, 'updateFilter').and.callFake(function() {});
    }));

    it('should remove all filter and remove all datagrid filters', inject(function(FilterService, DatagridService) {
        //given
        FilterService.filters.push({});

        //when
        FilterService.removeAllFilters();

        //then
        expect(DatagridService.resetFilters).toHaveBeenCalled();
        expect(FilterService.filters.length).toBe(0);
    }));

    it('should add "contains" filter and add datagrid filter', inject(function(FilterService, DatagridService) {
        //given
        expect(FilterService.filters.length).toBe(0);

        //when
        FilterService.addFilter('contains', 'col1', 'column name', {phrase: 'toto'});

        //then
        expect(FilterService.filters.length).toBe(1);

        var filterInfo = FilterService.filters[0];
        expect(filterInfo.type).toBe('contains');
        expect(filterInfo.colId).toBe('col1');
        expect(filterInfo.colName).toBe('column name');
        expect(filterInfo.editable).toBe(true);
        expect(filterInfo.args).toEqual({phrase: 'toto'});
        expect(filterInfo.filterFn({col1: ' toto est ici'})).toBeTruthy();
        expect(filterInfo.filterFn({col1: ' tata est ici'})).toBeFalsy();

        expect(DatagridService.addFilter).toHaveBeenCalledWith(filterInfo.filterFn);
    }));

    it('"contains" filter should not throw exception on non existing column (that could be removed by a step)', inject(function(FilterService, DatagridService) {
        //given
        expect(FilterService.filters.length).toBe(0);

        //when
        FilterService.addFilter('contains', 'col_that_does_not_exist', 'column name', {phrase: 'toto'});

        //then
        expect(FilterService.filters.length).toBe(1);

        var filterInfo = FilterService.filters[0];
        expect(filterInfo.type).toBe('contains');
        expect(filterInfo.filterFn({col1: ' toto est ici'})).toBeFalsy();

        expect(DatagridService.addFilter).toHaveBeenCalledWith(filterInfo.filterFn);
    }));

    it('should add "contains" filter with wildcard', inject(function(FilterService) {
        //given
        expect(FilterService.filters.length).toBe(0);

        //when
        FilterService.addFilter('contains', 'col1', 'column name', {phrase: 'to*ici'});

        //then
        expect(FilterService.filters.length).toBe(1);

        var filterInfo = FilterService.filters[0];
        expect(filterInfo.filterFn({col1: ' toto est ici'})).toBeTruthy();
        expect(filterInfo.filterFn({col1: ' tata est ici'})).toBeFalsy();
    }));

    it('should remove filter and remove datagrid filter', inject(function(FilterService, DatagridService) {
        //given
        FilterService.addFilter('contains', 'col1', 'column 1', {phrase: 'Toto'});
        FilterService.addFilter('contains', 'col2', 'column 1', {phrase: 'Toto'});
        var filter1 = FilterService.filters[0];
        var filter2 = FilterService.filters[1];

        //when
        FilterService.removeFilter(filter1);

        //then
        expect(FilterService.filters.length).toBe(1);
        expect(FilterService.filters[0]).toBe(filter2);
        expect(DatagridService.removeFilter).toHaveBeenCalledWith(filter1.filterFn);
    }));

    it('should do nothing on remove if filter is unknown', inject(function(FilterService, DatagridService) {
        //given
        FilterService.addFilter('contains', 'col1', 'column 1', {phrase: 'Toto'});
        FilterService.addFilter('contains', 'col2', 'column 2', {phrase: 'Toto'});

        var filter1 = FilterService.filters[0];
        FilterService.removeFilter(filter1);
        expect(FilterService.filters.length).toBe(1);
        expect(DatagridService.removeFilter.calls.count()).toBe(1);

        //when
        FilterService.removeFilter(filter1);

        //then
        expect(FilterService.filters.length).toBe(1);
        expect(DatagridService.removeFilter.calls.count()).toBe(1);
    }));

    it('should return filter value info for "contains" filter', inject(function(FilterService) {
        //given
        FilterService.addFilter('contains', 'col1', 'column 1', {phrase: 'Toto'});
        var filter1 = FilterService.filters[0];

        //when
        var value = filter1.value;

        //then
        expect(value).toBe('Toto');
    }));

    it('should update "contains" filter and update datagrid filter', inject(function(FilterService, DatagridService) {
        //given
        FilterService.addFilter('contains', 'col1', 'column 1', {phrase: 'Toto'});
        FilterService.addFilter('contains', 'col2', 'column 2', {phrase: 'Toto'});
        var filter1 = FilterService.filters[0];
        var filter2 = FilterService.filters[1];

        //when
        FilterService.updateFilter(filter2, 'Tata');

        //then
        var newFilter2 = FilterService.filters[1];
        expect(FilterService.filters.length).toBe(2);
        expect(FilterService.filters[0]).toBe(filter1);
        expect(newFilter2).not.toBe(filter2);
        expect(newFilter2.type).toBe('contains');
        expect(newFilter2.colId).toBe('col2');
        expect(newFilter2.colName).toBe('column 2');
        expect(newFilter2.args.phrase).toBe('Tata');
        expect(newFilter2.value).toBe('Tata');
        expect(DatagridService.updateFilter).toHaveBeenCalledWith(filter2.filterFn, newFilter2.filterFn);
    }));

    it('should update "inside_range" filter after a brush and update datagrid filter', inject(function(FilterService, DatagridService) {
        //given
        FilterService.addFilter('inside_range', 'col1', 'column 1', {phrase: [5,10]});
        var filter = FilterService.filters[0];

        //when
        FilterService.updateFilter(filter, [0,22]);

        //then
        var newFilter = FilterService.filters[0];
        expect(FilterService.filters.length).toBe(1);
        expect(newFilter).not.toBe(filter);
        expect(newFilter.type).toBe('inside_range');
        expect(newFilter.colId).toBe('col1');
        expect(newFilter.colName).toBe('column 1');
        expect(newFilter.args.phrase).toEqual([0,22]);
        expect(newFilter.value).toBe('in [0 ... 22]');
        expect(DatagridService.updateFilter).toHaveBeenCalledWith(filter.filterFn, newFilter.filterFn);
    }));

    it('should add "invalid records" filter and add datagrid filter', inject(function(FilterService, DatagridService) {
        //given
        expect(FilterService.filters.length).toBe(0);

        //when
        FilterService.addFilter('invalid_records', 'col1', 'column name', {values: ['NA', 'N/A', 'N.A']});

        //then
        expect(FilterService.filters.length).toBe(1);

        var filterInfo = FilterService.filters[0];
        expect(filterInfo.type).toBe('invalid_records');
        expect(filterInfo.colId).toBe('col1');
        expect(filterInfo.colName).toBe('column name');
        expect(filterInfo.value).toBe('invalid records');
        expect(filterInfo.editable).toBe(false);
        expect(filterInfo.args).toEqual({values: ['NA', 'N/A', 'N.A']});
        expect(filterInfo.filterFn({col1: 'NA'})).toBeTruthy();
        expect(filterInfo.filterFn({col1: ' tata est ici'})).toBeFalsy();

        expect(DatagridService.addFilter).toHaveBeenCalledWith(filterInfo.filterFn);
    }));

    it('should add "empty records" filter and add datagrid filter', inject(function(FilterService, DatagridService) {
        //given
        expect(FilterService.filters.length).toBe(0);

        //when
        FilterService.addFilter('empty_records', 'col1', 'column name', {});

        //then
        expect(FilterService.filters.length).toBe(1);

        var filterInfo = FilterService.filters[0];
        expect(filterInfo.type).toBe('empty_records');
        expect(filterInfo.colId).toBe('col1');
        expect(filterInfo.colName).toBe('column name');
        expect(filterInfo.value).toBe('empty records');
        expect(filterInfo.editable).toBe(false);
        expect(filterInfo.args).toEqual({});
        expect(filterInfo.filterFn({col1: ''})).toBeTruthy();
        expect(filterInfo.filterFn({col1: ' tata est ici'})).toBeFalsy();

        expect(DatagridService.addFilter).toHaveBeenCalledWith(filterInfo.filterFn);
    }));

    it('should add "valid records" filter and add datagrid filter', inject(function(FilterService, DatagridService) {
        //when
        FilterService.addFilter('valid_records', 'col1', 'column name', {values:['m','p']});

        //then
        expect(FilterService.filters.length).toBe(1);

        var filterInfo = FilterService.filters[0];
        expect(filterInfo.type).toBe('valid_records');
        expect(filterInfo.colId).toBe('col1');
        expect(filterInfo.colName).toBe('column name');
        expect(filterInfo.value).toBe('valid records');
        expect(filterInfo.editable).toBe(false);
        expect(filterInfo.args).toEqual({values:['m','p']});
        expect(filterInfo.filterFn({col1: 'a'})).toBeTruthy();
        expect(filterInfo.filterFn({col1: 'm'})).toBeFalsy();
        expect(filterInfo.filterFn({col1: ''})).toBeFalsy();

        expect(DatagridService.addFilter).toHaveBeenCalledWith(filterInfo.filterFn);
    }));

    it('should add "inside range" filter and add datagrid filter', inject(function(FilterService, DatagridService) {
        //given
        expect(FilterService.filters.length).toBe(0);

        //when
        FilterService.addFilter('inside_range', 'col1', 'column name', {phrase: [0, 22]});
        FilterService.addFilter('inside_range', 'col2', 'column name2', {phrase: [0, 1000000]});

        //then
        expect(FilterService.filters.length).toBe(2);

        var filterInfo = FilterService.filters[0];
        expect(filterInfo.type).toBe('inside_range');
        expect(filterInfo.colId).toBe('col1');
        expect(filterInfo.colName).toBe('column name');
        expect(filterInfo.value).toBe('in [0 ... 22]');
        expect(filterInfo.editable).toBe(false);
        expect(filterInfo.args).toEqual({phrase: [0, 22]});
        expect(filterInfo.filterFn({col1:'5'})).toBeTruthy();
        expect(filterInfo.filterFn({col1:'-5'})).toBeFalsy();

        expect(DatagridService.addFilter).toHaveBeenCalledWith(filterInfo.filterFn);

        var filterInfo2 = FilterService.filters[1];
        expect(filterInfo2.type).toBe('inside_range');
        expect(filterInfo2.colId).toBe('col2');
        expect(filterInfo2.colName).toBe('column name2');
        expect(filterInfo2.value).toBe('in [0e+0 ... 1e+6]');
        expect(filterInfo2.editable).toBe(false);
        expect(filterInfo2.args).toEqual({phrase:  [0, 1000000]});
        expect(filterInfo2.filterFn({col2: '1000'})).toBeTruthy();
        expect(filterInfo2.filterFn({col2: '-5'})).toBeFalsy();

        expect(DatagridService.addFilter).toHaveBeenCalledWith(filterInfo2.filterFn);
    }));

    it('should update "inside range" filter and add datagrid filter', inject(function(FilterService) {
        //given
        expect(FilterService.filters.length).toBe(0);

        //when
        FilterService.addFilter('inside_range', 'col1', 'column name', {phrase: [0, 22]});

        var filterInfo1 = FilterService.filters[0];
        //before the brush 4 in [0 ... 22]
        expect(filterInfo1.filterFn({col1: '4'})).toBeTruthy();
        //on brush end the range of the slider was reduced from [0...22] to [5...10]
        FilterService.addFilter('inside_range', 'col1', 'column name', {phrase: [5, 10]});

        //then
        expect(FilterService.filters.length).toBe(1);

        var filterInfo2 = FilterService.filters[0];
        expect(filterInfo2.type).toBe('inside_range');
        expect(filterInfo2.colId).toBe('col1');
        expect(filterInfo2.colName).toBe('column name');
        expect(filterInfo2.value).toBe('in [5 ... 10]');
        expect(filterInfo2.editable).toBe(false);
        expect(filterInfo2.args).toEqual({phrase:  [5, 10]});
        expect(filterInfo2.filterFn({col1: '8'})).toBeTruthy();
        //the 4 is no more inside the brush range
        expect(filterInfo2.filterFn({col1: '4'})).toBeFalsy();
    }));
});