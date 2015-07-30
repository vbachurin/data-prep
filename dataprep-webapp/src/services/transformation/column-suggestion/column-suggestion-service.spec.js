describe('Column suggestion service', function() {
    'use strict';

    var firstSelectedColumn = {id: '0001', name: 'col1'};
    var secondSelectedColumn = {id: '0002', name: 'col2'};

    beforeEach(module('data-prep.services.transformation'));
    beforeEach(inject(function($q, TransformationCacheService,TransformationRestService) {
        spyOn(TransformationCacheService, 'getTransformations').and.returnValue($q.when(
            [
                {name: 'rename', category: 'columns', label:'z'},
                {name: 'cluster', category: 'quickfix', label:'f'},
                {name: 'split', category: 'columns', label:'c'},
                {name: 'tolowercase', category: 'case', label:'v'},
                {name: 'touppercase', category: 'case', label:'u'},
                {name: 'removeempty', category: 'clear', label:'a'},
                {name: 'totitlecase', category: 'case', label:'t'},
                {name: 'removetrailingspaces', category: 'quickfix', label:'m'}
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

    it('should filter "column" category, sort and group the transformations by category', inject(function($rootScope, ColumnSuggestionService, TransformationCacheService) {
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

        //Assert sorted result
        expect(suggestedTransformations[0].label).toEqual('a');
        expect(suggestedTransformations[suggestedTransformations.length - 1].label).toEqual('v');
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