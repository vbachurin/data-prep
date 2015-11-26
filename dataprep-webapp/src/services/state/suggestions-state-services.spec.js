describe('Grid state service', function () {
    'use strict';

    beforeEach(module('data-prep.services.state'));

    describe('loading flag', function() {
        it('should set loading flag', inject(function (suggestionsState, SuggestionsStateService) {
            //given
            suggestionsState.isLoading = false;

            //when
            SuggestionsStateService.setLoading(true);

            //then
            expect(suggestionsState.isLoading).toBe(true);
        }));
    });

    describe('line scope transformations', function() {
        it('should init transformations list to empty', inject(function (suggestionsState) {
            //then
            expect(suggestionsState.line.allTransformations).toEqual([]);
            expect(suggestionsState.line.filteredTransformations).toEqual([]);
            expect(suggestionsState.line.allCategories).toBe(null);
        }));

        it('should set transformations', inject(function (suggestionsState, SuggestionsStateService) {
            //given
            var transformations = {
                allTransformations: [{name: 'delete'}, {name: 'uppercase'}],
                filteredTransformations: [{name: 'delete'}],
                allCategories: {
                    'clean': [{name: 'delete'}],
                    'case': [{name: 'uppercase'}]
                }
            };

            expect(suggestionsState.line).not.toBe(transformations);

            //when
            SuggestionsStateService.setLineTransformations(transformations);

            //then
            expect(suggestionsState.line).toBe(transformations);
        }));
    });

    describe('reset', function() {
        it('should reset loading flag', inject(function (suggestionsState, SuggestionsStateService) {
            //given
            suggestionsState.isLoading = true;

            //when
            SuggestionsStateService.reset();

            //then
            expect(suggestionsState.isLoading).toBe(false);
        }));

        it('should reset line transformations', inject(function (suggestionsState, SuggestionsStateService) {
            //given
            suggestionsState.line = {
                allTransformations: [{name: 'delete'}, {name: 'uppercase'}],
                filteredTransformations: [{name: 'delete'}],
                allCategories: {
                    'clean': [{name: 'delete'}],
                    'case': [{name: 'uppercase'}]
                }
            };

            //when
            SuggestionsStateService.reset();

            //then
            expect(suggestionsState.line.allTransformations).toEqual([]);
            expect(suggestionsState.line.filteredTransformations).toEqual([]);
            expect(suggestionsState.line.allCategories).toBe(null);
        }));
    });
});
