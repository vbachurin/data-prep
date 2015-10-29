describe('Column suggestion service', function () {
    'use strict';

    var firstSelectedColumn = {id: '0001', name: 'col1'};

    beforeEach(module('data-prep.services.transformation'));
    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'ACTION_SUGGESTION': 'Suggestion'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(inject(function ($q, TransformationCacheService) {
        spyOn(TransformationCacheService, 'getTransformations').and.returnValue($q.when(
            [
                {name: 'rename', category: 'column_metadata', label: 'z', description: 'test'},
                {name: 'cluster', category: 'quickfix', label: 'f', description: 'test'},
                {name: 'split', category: 'column_metadata', label: 'c', description: 'test'},
                {name: 'tolowercase', category: 'case', label: 'v', description: 'test'},
                {name: 'touppercase', category: 'case', label: 'u', description: 'test'},
                {name: 'removeempty', category: 'clear', label: 'a', description: 'test'},
                {name: 'totitlecase', category: 'case', label: 't', description: 'test'},
                {name: 'removetrailingspaces', category: 'quickfix', label: 'm', description: 'test'},
                {name: 'split', category: 'split', label: 'l', dynamic: true, description: 'test'}
            ]
        ));

        spyOn(TransformationCacheService, 'getSuggestions').and.returnValue($q.when(
            [
                {name: 'tolowercase', category: 'case', label: 'v', description: 'test'},
                {name: 'touppercase', category: 'case', label: 'u', description: 'test'}
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

    it('should group the transformations by category', inject(function ($rootScope, ColumnSuggestionService, TransformationCacheService) {
        //given
        ColumnSuggestionService.transformations = {};

        //when
        ColumnSuggestionService.initTransformations(firstSelectedColumn);
        expect(ColumnSuggestionService.transformations).toBeFalsy();
        $rootScope.$digest();

        //then
        expect(TransformationCacheService.getTransformations).toHaveBeenCalledWith(firstSelectedColumn);
        expect(TransformationCacheService.getSuggestions).toHaveBeenCalledWith(firstSelectedColumn);

        var suggestedTransformations = ColumnSuggestionService.transformations;
        expect(suggestedTransformations.SUGGESTION.length).toBe(2);
        expect(suggestedTransformations.CASE.length).toBe(3);
        expect(suggestedTransformations.CLEAR.length).toBe(1);
        expect(suggestedTransformations.QUICKFIX.length).toBe(2);
        expect(suggestedTransformations.SPLIT.length).toBe(1);
    }));

    it('should insert html label (with "..." with parameters) in each transformation/suggestions', inject(function ($rootScope, ColumnSuggestionService) {
        //given
        ColumnSuggestionService.transformations = {};

        //when
        ColumnSuggestionService.initTransformations(firstSelectedColumn);
        $rootScope.$digest();

        //then
        var suggestedTransformations = ColumnSuggestionService.transformations;

        expect(suggestedTransformations.SUGGESTION.length).toBe(2);
        expect(suggestedTransformations.CASE[0].labelHtml).toBe('t');
        expect(suggestedTransformations.CASE[1].labelHtml).toBe('u');
        expect(suggestedTransformations.CASE[2].labelHtml).toBe('v');
        expect(suggestedTransformations.CLEAR[0].labelHtml).toBe('a');
        expect(suggestedTransformations.QUICKFIX[0].labelHtml).toBe('f');
        expect(suggestedTransformations.QUICKFIX[1].labelHtml).toBe('m');
        expect(suggestedTransformations.SPLIT[0].labelHtml).toBe('l...');
    }));

    it('should filter "column metadata" category', inject(function ($rootScope, ColumnSuggestionService) {
        //given
        ColumnSuggestionService.transformations = {};

        //when
        ColumnSuggestionService.initTransformations(firstSelectedColumn);
        $rootScope.$digest();

        //then
        var suggestedTransformations = ColumnSuggestionService.transformations;
        var columnCategoryTransformation = _.find(suggestedTransformations, {category: 'column_metadata'});
        expect(columnCategoryTransformation).toBeFalsy();
    }));

    it('should not filter transformations when searchActionString is empty', inject(function ($rootScope, ColumnSuggestionService) {
        //given
        ColumnSuggestionService.searchActionString = '';

        //when
        ColumnSuggestionService.initTransformations(firstSelectedColumn);
        $rootScope.$digest();
        ColumnSuggestionService.filterTransformations();
        $rootScope.$digest();

        //then
        var filteredTransformations = ColumnSuggestionService.filteredTransformations;

        expect(filteredTransformations.SUGGESTION.length).toBe(2);
        expect(filteredTransformations.CASE[0].labelHtml).toBe('t');
        expect(filteredTransformations.CASE[1].labelHtml).toBe('u');
        expect(filteredTransformations.CASE[2].labelHtml).toBe('v');
        expect(filteredTransformations.CLEAR[0].labelHtml).toBe('a');
        expect(filteredTransformations.QUICKFIX[0].labelHtml).toBe('f');
        expect(filteredTransformations.QUICKFIX[1].labelHtml).toBe('m');
        expect(filteredTransformations.SPLIT[0].labelHtml).toBe('l...');
    }));

    it('should filter transformations when searchActionString is not empty', inject(function ($rootScope, ColumnSuggestionService) {
        //given
        ColumnSuggestionService.initTransformations(firstSelectedColumn);
        $rootScope.$digest();

        //when
        ColumnSuggestionService.searchActionString = 'l';

        ColumnSuggestionService.filterTransformations();
        $rootScope.$digest();

        //then
        var filteredTransformations = ColumnSuggestionService.filteredTransformations;
        expect(filteredTransformations['C<span class="highlighted">L</span>EAR'].length).toBe(1);
        expect(filteredTransformations['C<span class="highlighted">L</span>EAR'][0].labelHtml).toBe('a');
        expect(filteredTransformations['SP<span class="highlighted">L</span>IT'].length).toBe(1);
        expect(filteredTransformations['SP<span class="highlighted">L</span>IT'][0].labelHtml).toBe('<span class="highlighted">l</span>...');
    }));

    it('should filter transformations with case insensitive', inject(function ($rootScope, ColumnSuggestionService) {
        //given
        ColumnSuggestionService.initTransformations(firstSelectedColumn);
        $rootScope.$digest();

        //when
        ColumnSuggestionService.searchActionString = 'L';

        ColumnSuggestionService.filterTransformations();
        $rootScope.$digest();

        //then
        var filteredTransformations = ColumnSuggestionService.filteredTransformations;
        expect(_.values(filteredTransformations).length).toBe(2);
        expect(filteredTransformations['C<span class="highlighted">L</span>EAR'].length).toBe(1);
        expect(filteredTransformations['C<span class="highlighted">L</span>EAR'][0].labelHtml).toBe('a');
        expect(filteredTransformations['SP<span class="highlighted">L</span>IT'].length).toBe(1);
        expect(filteredTransformations['SP<span class="highlighted">L</span>IT'][0].labelHtml).toBe('<span class="highlighted">l</span>...');
    }));

    it('should filter transformations by escaping regex', inject(function ($rootScope, ColumnSuggestionService) {
        //given
        ColumnSuggestionService.initTransformations(firstSelectedColumn);
        $rootScope.$digest();

        //when
        ColumnSuggestionService.searchActionString = '...';

        ColumnSuggestionService.filterTransformations();
        $rootScope.$digest();

        //then
        var filteredTransformations = ColumnSuggestionService.filteredTransformations;
        expect(_.values(filteredTransformations).length).toBe(1);
        expect(filteredTransformations['SPLIT'].length).toBe(1);
        expect(filteredTransformations['SPLIT'][0].labelHtml).toBe('l<span class="highlighted">...</span>');
    }));
});