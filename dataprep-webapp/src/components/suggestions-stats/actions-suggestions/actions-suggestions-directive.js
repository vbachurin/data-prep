(function() {
    'use strict';

    function ActionsSuggestions() {
        return {
            restrict: 'E',
            templateUrl: 'components/suggestions-stats/actions-suggestions/actions-suggestions.html',
            bindToController: true,
            controllerAs: 'actionsSuggestionsCtrl',
            controller: 'ActionsSuggestionsCtrl'
        };
    }

    angular.module('data-prep.actions-suggestions')
        .directive('actionsSuggestions', ActionsSuggestions);
})();