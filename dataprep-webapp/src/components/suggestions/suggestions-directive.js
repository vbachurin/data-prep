(function() {
    'use strict';

    function Suggestions() {
        return {
            restrict: 'E',
            templateUrl: 'components/suggestions/suggestions.html',
            scope: {
                metadata: '='
            },
            bindToController: true,
            controllerAs: 'suggestionsCtrl',
            controller: 'SuggestionsCtrl'
        };
    }

    angular.module('data-prep.suggestions')
        .directive('suggestions', Suggestions);
})();