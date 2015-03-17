describe('Filter service', function() {
    'use strict';

    beforeEach(module('data-prep.services.filter'));
    beforeEach(inject(function(DatasetGridService) {
        spyOn(DatasetGridService, 'resetFilters').and.callFake(function() {});
        spyOn(DatasetGridService, 'addFilter').and.callFake(function() {});
        spyOn(DatasetGridService, 'removeFilter').and.callFake(function() {});
    }));

    it('should remove all filter and remove all datagrid filters', inject(function(FilterService, DatasetGridService) {
        //given
        FilterService.filters.push({});

        //when
        FilterService.removeAllFilters();

        //then
        expect(DatasetGridService.resetFilters).toHaveBeenCalled();
        expect(FilterService.filters.length).toBe(0);
    }));

    it('should add "contains" filter and add datagrid filter', inject(function(FilterService, DatasetGridService) {
        //given
        expect(FilterService.filters.length).toBe(0);

        //when
        FilterService.addFilter('contains', 'col1', {phrase: 'toto'});

        //then
        expect(FilterService.filters.length).toBe(1);

        var filterInfos = FilterService.filters[0];
        expect(filterInfos.type).toBe('contains');
        expect(filterInfos.colId).toBe('col1');
        expect(filterInfos.args).toEqual({phrase: 'toto'});
        expect(filterInfos.filterFn({col1: ' toto est ici'})).toBeTruthy();
        expect(filterInfos.filterFn({col1: ' tata est ici'})).toBeFalsy();

        expect(DatasetGridService.addFilter).toHaveBeenCalledWith(filterInfos.filterFn);
    }));

    it('should add "contains" filter with wildcard', inject(function(FilterService, DatasetGridService) {
        //given
        expect(FilterService.filters.length).toBe(0);

        //when
        FilterService.addFilter('contains', 'col1', {phrase: 'to*ici'});

        //then
        expect(FilterService.filters.length).toBe(1);

        var filterInfos = FilterService.filters[0];
        expect(filterInfos.filterFn({col1: ' toto est ici'})).toBeTruthy();
        expect(filterInfos.filterFn({col1: ' tata est ici'})).toBeFalsy();
    }));

    it('should remove filter and remove datagrid filter', inject(function(FilterService, DatasetGridService) {
        //given
        FilterService.addFilter('contains', 'col1', {phrase: 'Toto'});
        FilterService.addFilter('contains', 'col2', {phrase: 'Toto'});
        var filter1 = FilterService.filters[0];
        var filter2 = FilterService.filters[1];

        //when
        FilterService.removeFilter(filter1);

        //then
        expect(FilterService.filters.length).toBe(1);
        expect(FilterService.filters[0]).toBe(filter2);
        expect(DatasetGridService.removeFilter).toHaveBeenCalledWith(filter1.filterFn);
    }));

    it('should do nothing on remove if filter is unknown', inject(function(FilterService, DatasetGridService) {
        //given
        FilterService.addFilter('contains', 'col1', {phrase: 'Toto'});
        FilterService.addFilter('contains', 'col2', {phrase: 'Toto'});

        var filter1 = FilterService.filters[0];
        FilterService.removeFilter(filter1);
        expect(FilterService.filters.length).toBe(1);
        expect(DatasetGridService.removeFilter.calls.count()).toBe(1);

        //when
        FilterService.removeFilter(filter1);

        //then
        expect(FilterService.filters.length).toBe(1);
        expect(DatasetGridService.removeFilter.calls.count()).toBe(1);
    }));

    it('should create filter info description string', inject(function(FilterService) {
        //given
        FilterService.addFilter('contains', 'col1', {phrase: 'Toto'});
        var filter1 = FilterService.filters[0];

        //when
        var description = filter1.toString();

        //then
        expect(description).toBe('COL1: Toto');
    }));
});