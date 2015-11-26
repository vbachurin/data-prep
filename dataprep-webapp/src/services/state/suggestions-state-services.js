(function () {
    'use strict';

    var suggestionsState = {
        isLoading: false,
        line: {
            allTransformations: [],
            filteredTransformations: [],
            allCategories: null
        }
    };

    function SuggestionsStateService() {

        return {
            setLineTransformations: setLineTransformations,
            setLoading: setLoading,
            reset: reset
        };

        function setLoading(isLoading) {
            suggestionsState.isLoading = isLoading;
        }

        function setLineTransformations(lineTransformations) {
            suggestionsState.line = lineTransformations;
        }

        function reset() {
            suggestionsState.isLoading = false;
            suggestionsState.line = {
                allTransformations: [],
                filteredTransformations: [],
                allCategories: null
            };
        }
    }

    angular.module('data-prep.services.state')
        .service('SuggestionsStateService', SuggestionsStateService)
        .constant('suggestionsState', suggestionsState);
})();