(function() {
    'use strict';

    function SuggestionsStats($timeout) {
        return {
            restrict: 'E',
            templateUrl: 'components/suggestions-stats/suggestions-stats.html',
            scope: {
                metadata: '='
            },
            bindToController: true,
            controllerAs: 'suggestionsStatsCtrl',
            controller: 'SuggestionsStatsCtrl',
            link: function(scope, iElement) {

                //Resize action-suggestion and stat-detail panels
                $timeout(function(){
                    var handler = angular.element('.split-handler');
                    var panel1 = angular.element('.split-pane1');
                    var panel2 = angular.element('.split-pane2');

                    var drag = false;

                    iElement.bind('mousemove', function () {
                        if (!drag) {
                            return;
                        }
                        angular.element('.action-suggestion-tab-items').css('height', panel1.height()-100 + 'px');
                        angular.element('.stat-detail-tab-items').css('height', panel2.height()-100 + 'px');
                    });

                    handler.bind('mousedown', function (ev) {
                        ev.preventDefault();
                        drag = true;
                    });

                    angular.element(document).bind('mouseup', function () {
                        drag = false;
                    });
                }, 200);

            }
        };
    }

    angular.module('data-prep.suggestions-stats')
        .directive('suggestionsStats', SuggestionsStats);
})();