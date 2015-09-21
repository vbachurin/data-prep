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

                    //These elements are created by bg-splitter
                    var handler = iElement.find('.split-handler');
                    var panel1 = iElement.find('.split-pane1');
                    var panel2 = iElement.find('.split-pane2');

                    var drag = false;

                    iElement.bind('mousemove', function () {
                        if (!drag) {
                            return;
                        }
                        iElement.find('.action-suggestion-tab-items').css('height', panel1.height()-100 + 'px');
                        iElement.find('.stat-detail-tab-items').css('height', panel2.height()-100 + 'px');
                    });

                    handler.bind('mousedown', function (ev) {
                        ev.preventDefault();
                        drag = true;
                    });

                    angular.element(document).bind('mouseup', function () {
                        drag = false;
                    });
                });
            }
        };
    }

    angular.module('data-prep.suggestions-stats')
        .directive('suggestionsStats', SuggestionsStats);
})();