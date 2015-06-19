describe('Column suggestion service', function() {
    'use strict';

    var firstSelectedColumn = {id: '0001', name: 'col1'};
    var secondSelectedColumn = {id: '0002', name: 'col2'};

    beforeEach(module('data-prep.services.transformation'));
    beforeEach(inject(function($q, TransformationCacheService) {
        spyOn(TransformationCacheService, 'getTransformations').and.returnValue($q.when(
            [
                {name: 'rename', category: 'columns'},
                {name: 'cluster', category: 'quickfix'},
                {name: 'split', category: 'columns'},
                {name: 'tolowercase', category: 'case'},
                {name: 'touppercase', category: 'case'},
                {name: 'removeempty', category: 'clear'},
                {name: 'totitlecase', category: 'case'},
                {name: 'removetrailingspaces', category: 'quickfix'}
            ]
        ));
    }));

    it('should reset the current selected column and suggested transformations', inject(function(ColumnSuggestionService) {
        //given
        ColumnSuggestionService.currentColumn = {};
        ColumnSuggestionService.transformations = {};

        //when
        ColumnSuggestionService.reset();

        //then
        expect(ColumnSuggestionService.currentColumn).toBeFalsy();
        expect(ColumnSuggestionService.transformations).toBeFalsy();
    }));

    it('should set selected column', inject(function(ColumnSuggestionService) {
        //given
        ColumnSuggestionService.currentColumn = {};

        //when
        ColumnSuggestionService.setColumn(firstSelectedColumn);

        //then
        expect(ColumnSuggestionService.currentColumn).toBe(firstSelectedColumn);
    }));

    it('should filter "column" category and group the transformations by category', inject(function($rootScope, ColumnSuggestionService, TransformationCacheService) {
        //given
        ColumnSuggestionService.currentColumn = {};

        //when
        ColumnSuggestionService.setColumn(firstSelectedColumn);
        $rootScope.$digest();

        //then
        expect(TransformationCacheService.getTransformations).toHaveBeenCalledWith(firstSelectedColumn);

        var suggestedTransformations = ColumnSuggestionService.transformations;
        expect(suggestedTransformations).toBeDefined();
        expect('columns' in suggestedTransformations).toBe(false);
        expect('quickfix' in suggestedTransformations).toBe(true);
        expect('case' in suggestedTransformations).toBe(true);
        expect('clear' in suggestedTransformations).toBe(true);

        expect(suggestedTransformations.quickfix)
            .toEqual([{name: 'cluster', category: 'quickfix'}, {name: 'removetrailingspaces', category: 'quickfix'}]);
        expect(suggestedTransformations.case)
            .toEqual([{name: 'tolowercase', category: 'case'}, {name: 'touppercase', category: 'case'}, {name: 'totitlecase', category: 'case'}]);
        expect(suggestedTransformations.clear)
            .toEqual([{name: 'removeempty', category: 'clear'}]);
    }));

    it('should do nothing when we set the actual selected column', inject(function($rootScope, ColumnSuggestionService, TransformationCacheService) {
        //given
        ColumnSuggestionService.currentColumn = firstSelectedColumn;

        //when
        ColumnSuggestionService.setColumn(firstSelectedColumn);

        //then
        expect(TransformationCacheService.getTransformations).not.toHaveBeenCalled();
    }));

    it('should do nothing when we set the actual selected column', inject(function($rootScope, ColumnSuggestionService, TransformationCacheService) {
        //given
        ColumnSuggestionService.currentColumn = firstSelectedColumn;

        //when
        ColumnSuggestionService.setColumn(firstSelectedColumn);

        //then
        expect(TransformationCacheService.getTransformations).not.toHaveBeenCalled();
    }));

    it('should set the suggested transformations only if the corresponding column has not changed ', inject(function($rootScope, ColumnSuggestionService) {
        //given
        ColumnSuggestionService.currentColumn = null;
        ColumnSuggestionService.transformations = null;
        ColumnSuggestionService.setColumn(firstSelectedColumn);

        //when
        ColumnSuggestionService.currentColumn = secondSelectedColumn;
        $rootScope.$digest();

        //then
        expect(ColumnSuggestionService.transformations).toBe(null);
    }));
});