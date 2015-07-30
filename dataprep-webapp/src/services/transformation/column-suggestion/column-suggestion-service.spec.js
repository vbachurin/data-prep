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

    it('should reset the current selected column and suggested transformations', inject(function (ColumnSuggestionService) {
        //given
        ColumnSuggestionService.currentColumn = {};
        ColumnSuggestionService.transformations = {};

        //when
        ColumnSuggestionService.reset();

        //then
        expect(ColumnSuggestionService.currentColumn).toBeFalsy();
        expect(ColumnSuggestionService.transformations).toBeFalsy();
    }));

    it('should set selected column', inject(function (ColumnSuggestionService) {
        //given
        ColumnSuggestionService.currentColumn = {};

        //when
        ColumnSuggestionService.setColumn(firstSelectedColumn);

        //then
        expect(ColumnSuggestionService.currentColumn).toBe(firstSelectedColumn);
    }));

    it('should filter "column" category, sort and group the transformations by category', inject(function ($rootScope, ColumnSuggestionService, TransformationCacheService) {
        //given
        ColumnSuggestionService.currentColumn = {};

        //when
        ColumnSuggestionService.setColumn(firstSelectedColumn);
        $rootScope.$digest();

        //then : transformations initialized
        expect(TransformationCacheService.getTransformations).toHaveBeenCalledWith(firstSelectedColumn);

        //then : column category filtered
        var suggestedTransformations = ColumnSuggestionService.transformations;
        expect(suggestedTransformations).toBeDefined();
        expect('columns' in suggestedTransformations).toBe(false);

        //then : result alphabetically sorted
        expect(suggestedTransformations[0].label).toEqual('a');
        expect(suggestedTransformations[suggestedTransformations.length - 1].label).toEqual('v');
    }));

    it('should do nothing when we set the actual selected column', inject(function ($rootScope, ColumnSuggestionService, TransformationCacheService) {
        //given
        ColumnSuggestionService.currentColumn = firstSelectedColumn;

        //when
        ColumnSuggestionService.setColumn(firstSelectedColumn);

        //then
        expect(TransformationCacheService.getTransformations).not.toHaveBeenCalled();
    }));

    it('should do nothing when we set the actual selected column', inject(function ($rootScope, ColumnSuggestionService, TransformationCacheService) {
        //given
        ColumnSuggestionService.currentColumn = firstSelectedColumn;

        //when
        ColumnSuggestionService.setColumn(firstSelectedColumn);

        //then
        expect(TransformationCacheService.getTransformations).not.toHaveBeenCalled();
    }));

    it('should set the suggested transformations only if the corresponding column has not changed ', inject(function ($rootScope, ColumnSuggestionService) {
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

    it('should init common statistics when we set the selected column', inject(function (ColumnSuggestionService) {
        //given
        var col = {
            'id': '0001',
            type: 'boolean',
            statistics: {
                count: 4,
                distinctCount: 5,
                duplicateCount: 6,
                empty: 7,
                invalid: 8,
                valid: 9
            }
        };
        expect(ColumnSuggestionService.statistics).toBeFalsy();

        //when
        ColumnSuggestionService.setColumn(col);

        //then
        expect(ColumnSuggestionService.statistics).toBeTruthy();
        expect(ColumnSuggestionService.statistics.common).toEqual({
            COUNT: 4,
            DISTINCT_COUNT: 5,
            DUPLICATE_COUNT: 6,
            VALID: 9,
            EMPTY: 7,
            INVALID: 8
        });
        expect(ColumnSuggestionService.statistics.specific).toEqual({});
    }));

    it('should init number statistics when we set the selected column', inject(function (ColumnSuggestionService) {
        //given
        var col = {
            'id': '0001',
            type: 'integer',
            statistics: {
                count: 4,
                distinctCount: 5,
                duplicateCount: 6,
                empty: 7,
                invalid: 8,
                valid: 9,
                min: 10,
                max: 11,
                mean: 12,
                variance: 13,
                quantiles: {
                    lowerQuantile: 'NaN'
                }
            }
        };
        expect(ColumnSuggestionService.statistics).toBeFalsy();

        //when
        ColumnSuggestionService.setColumn(col);

        //then
        expect(ColumnSuggestionService.statistics).toBeTruthy();
        expect(ColumnSuggestionService.statistics.common).toEqual({
            COUNT: 4,
            DISTINCT_COUNT: 5,
            DUPLICATE_COUNT: 6,
            VALID: 9,
            EMPTY: 7,
            INVALID: 8
        });
        expect(ColumnSuggestionService.statistics.specific).toEqual({
            MIN: 10,
            MAX: 11,
            MEAN: 12,
            VARIANCE: 13
        });
    }));

    it('should init number statistics with quantile when we set the selected column', inject(function (ColumnSuggestionService) {
        //given
        var col = {
            'id': '0001',
            type: 'integer',
            statistics: {
                count: 4,
                distinctCount: 5,
                duplicateCount: 6,
                empty: 7,
                invalid: 8,
                valid: 9,
                min: 10,
                max: 11,
                mean: 12,
                variance: 13,
                quantiles: {
                    median: 14,
                    lowerQuantile: 15,
                    upperQuantile: 16
                }
            }
        };
        expect(ColumnSuggestionService.statistics).toBeFalsy();

        //when
        ColumnSuggestionService.setColumn(col);

        //then
        expect(ColumnSuggestionService.statistics).toBeTruthy();
        expect(ColumnSuggestionService.statistics.common).toEqual({
            COUNT: 4,
            DISTINCT_COUNT: 5,
            DUPLICATE_COUNT: 6,
            VALID: 9,
            EMPTY: 7,
            INVALID: 8
        });
        expect(ColumnSuggestionService.statistics.specific).toEqual({
            MIN: 10,
            MAX: 11,
            MEAN: 12,
            VARIANCE: 13,
            MEDIAN: 14,
            LOWER_QUANTILE: 15,
            UPPER_QUANTILE: 16
        });
    }));

    it('should init text statistics when we set the selected column', inject(function (ColumnSuggestionService) {
        //given
        var col = {
            'id': '0001',
            type: 'string',
            statistics: {
                count: 4,
                distinctCount: 5,
                duplicateCount: 6,
                empty: 7,
                invalid: 8,
                valid: 9,
                textLengthSummary: {
                    averageLength: 10.13248646854654,
                    averageLengthWithBlank: 11.783242375675245,
                    minimalLength: 12,
                    minimalLengthWithBlank: 13,
                    maximalLength: 14
                }
            }
        };
        expect(ColumnSuggestionService.statistics).toBeFalsy();

        //when
        ColumnSuggestionService.setColumn(col);

        //then
        expect(ColumnSuggestionService.statistics).toBeTruthy();
        expect(ColumnSuggestionService.statistics.common).toEqual({
            COUNT: 4,
            DISTINCT_COUNT: 5,
            DUPLICATE_COUNT: 6,
            VALID: 9,
            EMPTY: 7,
            INVALID: 8
        });
        expect(ColumnSuggestionService.statistics.specific).toEqual({
            AVG_LENGTH: '10.13',
            AVG_LENGTH_WITH_BLANK: '11.78',
            MIN_LENGTH: 12,
            MIN_LENGTH_WITH_BLANK: 13,
            MAX_LENGTH: 14
        });
    }));
});