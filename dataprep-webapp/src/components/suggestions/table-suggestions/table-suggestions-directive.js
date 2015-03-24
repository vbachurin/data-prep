(function() {
    'use strict';

    function TableSuggestions() {
        return {
            restrict: 'E',
            templateUrl: 'components/suggestions/table-suggestions/table-suggestions.html',
            bindToController: true,
            controllerAs: 'tableSuggestionsCtrl',
            controller: 'TableSuggestionsCtrl'
        };
    }

    angular.module('data-prep.table-suggestions')
        .directive('tableSuggestions', TableSuggestions);
})();