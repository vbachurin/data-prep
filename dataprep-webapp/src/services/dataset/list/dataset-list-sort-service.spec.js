describe('Dataset list sort service', function () {
    'use strict';

    beforeEach(module('data-prep.dataset-list'));

    afterEach(inject(function($stateParams, $window) {
        $window.localStorage.removeItem('dataprep.dataset.sort');
        $window.localStorage.removeItem('dataprep.dataset.sortOrder');
    }));

    it('should get sort in localStorage ', inject(function ($window, DatasetListSortService) {

        //when
        $window.localStorage.setItem('dataprep.dataset.sort', 'name');

        //then
        expect(DatasetListSortService.getDefaultSort().id).toBe('name');
    }));

    it('should get order in localStorage ', inject(function ($window, DatasetListSortService) {

        //when
        $window.localStorage.setItem('dataprep.dataset.sortOrder', 'asc');

        //then
        expect(DatasetListSortService.getDefaultOrder().id).toBe('asc');
    }));


    it('should get sort by default if not in localStorage ', inject(function (DatasetListSortService) {

        //then
        expect(DatasetListSortService.getDefaultSort().id).toBe('date');
    }));

    it('should get order by default if not in localStorage ', inject(function (DatasetListSortService) {

        //then
        expect(DatasetListSortService.getDefaultOrder().id).toBe('desc');
    }));


    it('should update sort in localStorage ', inject(function (DatasetListSortService) {

        //when
        DatasetListSortService.setDatasetsSort('name');

        //then
        expect(DatasetListSortService.getDefaultSort().id).toBe('name');
        expect(DatasetListSortService.datasetsSort).toBe('name');
    }));

    it('should update order in localStorage ', inject(function (DatasetListSortService) {

        //when
        DatasetListSortService.setDatasetsOrder('asc');

        //then
        expect(DatasetListSortService.getDefaultOrder().id).toBe('asc');
        expect(DatasetListSortService.datasetsOrder).toBe('asc');
    }));


});
