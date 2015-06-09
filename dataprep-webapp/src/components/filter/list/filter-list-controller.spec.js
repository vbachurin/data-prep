describe('filter search controller', function() {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.filter-list'));

    beforeEach(inject(function($rootScope, $controller, FilterService) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('FilterListCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn(FilterService, 'removeFilter').and.callThrough();
    }));

    it('should bind filters getter to FilterService', inject(function(FilterService) {
        //given
        var ctrl = createController();
        expect(ctrl.filters.length).toBe(0);

        //when
        FilterService.addFilter('contains', '0000', 'col', {phrase: 'toto'});

        //then
        expect(ctrl.filters.length).toBe(1);

        var filter = ctrl.filters[0];
        expect(filter.type).toBe('contains');
        expect(filter.colId).toBe('0000');
        expect(filter.colName).toBe('col');
        expect(filter.args).toEqual({phrase: 'toto'});
        expect(filter.value).toBe('toto');
    }));

    it('should call filter service remove filter function', inject(function(FilterService) {
        //given
        FilterService.addFilter('contains', '0000', 'col', {phrase: 'toto'});
        var ctrl = createController();
        var filter = ctrl.filters[0];

        //when
        ctrl.delete(filter);

        //then
        expect(FilterService.removeFilter).toHaveBeenCalledWith(filter);
    }));
});
