describe('Dataset list sort service', function () {
    'use strict';

    var sortKey = 'dataprep.dataset.sort';
    var orderKey = 'dataprep.dataset.order';

    beforeEach(module('data-prep.dataset-list'));

    afterEach(inject(function($stateParams, $window) {
        $window.localStorage.removeItem(sortKey);
        $window.localStorage.removeItem(orderKey);
    }));

    describe('with empty localStorage', function() {
        beforeEach(inject(function($window) {
            $window.localStorage.removeItem(sortKey);
            $window.localStorage.removeItem(orderKey);
        }));

        it('should get default sort', inject(function (DatasetListSortService) {
            //when
            var result = DatasetListSortService.getSort();

            //then
            expect(result).toBe('date');
        }));

        it('should get default order', inject(function (DatasetListSortService) {
            //when
            var result = DatasetListSortService.getOrder();

            //then
            expect(result).toBe('desc');
        }));
    });

    describe('with parameters in localStorage', function() {
        beforeEach(inject(function($window) {
            $window.localStorage.setItem(sortKey, 'name');
            $window.localStorage.setItem(orderKey, 'asc');
        }));

        it('should get sort from localStorage', inject(function ($window, DatasetListSortService) {
            //when
            var sort = DatasetListSortService.getSort();

            //then
            expect(sort).toBe('name');
        }));

        it('should get order from localStorage', inject(function ($window, DatasetListSortService) {
            //when
            var order = DatasetListSortService.getOrder();

            //then
            expect(order).toBe('asc');
        }));
    });

    describe('parameters update', function() {

        it('should update sort in service ', inject(function (DatasetListSortService) {
            //given
            expect(DatasetListSortService.getSort()).not.toBe('name');

            //when
            DatasetListSortService.setSort('name');

            //then
            expect(DatasetListSortService.getSort()).toBe('name');
        }));

        it('should update sort in localStorage ', inject(function ($window, DatasetListSortService) {
            //given
            expect($window.localStorage.getItem(sortKey)).not.toBe('name');

            //when
            DatasetListSortService.setSort('name');

            //then
            expect($window.localStorage.getItem(sortKey)).toBe('name');
        }));

        it('should update order in service ', inject(function (DatasetListSortService) {
            //given
            expect(DatasetListSortService.getOrder()).not.toBe('asc');

            //when
            DatasetListSortService.setOrder('asc');

            //then
            expect(DatasetListSortService.getOrder()).toBe('asc');
        }));

        it('should update order in localStorage ', inject(function ($window, DatasetListSortService) {
            //given
            expect($window.localStorage.getItem(orderKey)).not.toBe('asc');

            //when
            DatasetListSortService.setOrder('asc');

            //then
            expect($window.localStorage.getItem(orderKey)).toBe('asc');
        }));
    });

});
