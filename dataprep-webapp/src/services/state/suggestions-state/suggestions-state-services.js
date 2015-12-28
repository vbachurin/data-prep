(function () {
    'use strict';

    var suggestionsState = {
        isLoading: false,
        line: {
            allTransformations: [],
            filteredTransformations: [],
            allCategories: null
        },
        column: {
            allSuggestions: [],     // all selected column suggestions
            allTransformations: [], // all selected column transformations
            filteredTransformations: [], // categories with their transformations to display, result of filter
            transformationsForEmptyCells: [], // all column transformations applied to empty cells
            transformationsForInvalidCells: [],// all column transformations applied to invalid cells,
            allCategories: null,
            searchActionString: ''
        }
    };

    function SuggestionsStateService() {

        return {
            setLineTransformations: setLineTransformations,
            setColumnTransformations: setColumnTransformations,
            setLoading: setLoading,
            updateFilteredTransformations: updateFilteredTransformations,
            resetColumnSuggestions: resetColumnSuggestions,
            reset: reset
        };

        function setLoading(isLoading) {
            suggestionsState.isLoading = isLoading;
        }

        function setLineTransformations(lineTransformations) {
            suggestionsState.line = lineTransformations;
        }

        function setColumnTransformations(columnTransformations) {
            suggestionsState.column = columnTransformations;
        }

        function updateFilteredTransformations(filteredTransformations) {
            suggestionsState.column.filteredTransformations = filteredTransformations;
        }

        function resetColumnSuggestions() {
            suggestionsState.column.allSuggestions = [];     // all selected column suggestions
            suggestionsState.column.allTransformations = []; // all selected column transformations
            suggestionsState.column.filteredTransformations = []; // categories with their transformations to display, result of filter
            suggestionsState.column.allCategories = null;
            suggestionsState.column.searchActionString = '';
        }

        function reset() {
            suggestionsState.isLoading = false;
            suggestionsState.line = {
                allTransformations: [],
                filteredTransformations: [],
                allCategories: null
            };

            resetColumnSuggestions();
        }
    }

    angular.module('data-prep.services.state')
        .service('SuggestionsStateService', SuggestionsStateService)
        .constant('suggestionsState', suggestionsState);
})();