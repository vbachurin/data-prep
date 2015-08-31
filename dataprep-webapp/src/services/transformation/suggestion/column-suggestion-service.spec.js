describe('Column suggestion service', function () {
    'use strict';

    var firstSelectedColumn = {id: '0001', name: 'col1'};
    var secondSelectedColumn = {id: '0002', name: 'col2'};

    beforeEach(module('data-prep.services.transformation'));
    beforeEach(inject(function ($q, TransformationCacheService) {
        spyOn(TransformationCacheService, 'getTransformations').and.returnValue($q.when(
            [
                {name: 'rename', category: 'columns', label: 'z'},
                {name: 'cluster', category: 'quickfix', label: 'f'},
                {name: 'split', category: 'columns', label: 'c'},
                {name: 'tolowercase', category: 'case', label: 'v'},
                {name: 'touppercase', category: 'case', label: 'u'},
                {name: 'removeempty', category: 'clear', label: 'a'},
                {name: 'totitlecase', category: 'case', label: 't'},
                {name: 'removetrailingspaces', category: 'quickfix', label: 'm'}
            ]
        ));
    }));

    it('should reset the suggested transformations', inject(function (ColumnSuggestionService) {
        //given
        ColumnSuggestionService.transformations = {};

        //when
        ColumnSuggestionService.reset();

        //then
        expect(ColumnSuggestionService.transformations).toBeFalsy();
    }));

    it('should filter "column" category, sort and group the transformations by category', inject(function ($rootScope, ColumnSuggestionService, TransformationCacheService) {
        //given
        ColumnSuggestionService.transformations = {};

        //when
        ColumnSuggestionService.initTransformations(firstSelectedColumn);
        expect(ColumnSuggestionService.transformations).toBeFalsy();
        $rootScope.$digest();

        //then : transformations initialized
        expect(TransformationCacheService.getTransformations).toHaveBeenCalledWith(firstSelectedColumn);

        //then : column category filtered
        var suggestedTransformations = ColumnSuggestionService.transformations;
        expect(suggestedTransformations).toBeDefined();
        var columnCategoryTransformation = _.find(suggestedTransformations, {category: 'columns'});
        expect(columnCategoryTransformation).toBeFalsy();

        //then : result alphabetically sorted
        expect(suggestedTransformations[0].label).toEqual('a');
        expect(suggestedTransformations[suggestedTransformations.length - 1].label).toEqual('v');
    }));
});