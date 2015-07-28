(function() {
    'use strict';

    function SuggestionsStats() {
        return {
            restrict: 'E',
            templateUrl: 'components/suggestions-stats/suggestions-stats.html',
            scope: {
                metadata: '='
            },
            bindToController: true,
            controllerAs: 'suggestionsStatsCtrl',
            controller: 'SuggestionsStatsCtrl'
        };
    }

    angular.module('data-prep.suggestions-stats')
        .directive('suggestionsStats', SuggestionsStats);
})();