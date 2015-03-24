(function() {
    'use strict';

    function ColumnSuggestions() {
        return {
            restrict: 'E',
            templateUrl: 'components/suggestions/column-suggestions/column-suggestions.html',
            bindToController: true,
            controllerAs: 'columnSuggestionsCtrl',
            controller: 'ColumnSuggestionsCtrl'
        };
    }

    angular.module('data-prep.column-suggestions')
        .directive('columnSuggestions', ColumnSuggestions);
})();