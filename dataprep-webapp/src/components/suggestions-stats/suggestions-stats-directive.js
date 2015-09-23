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

                    var actionHeaderPanelsSizeMargin = 130;
                    var statHeaderPanelsSizeMargin = 100;

                    iElement.bind('mousemove', function () {
                        if (!drag) {
                            return;
                        }
                        iElement.find('.action-suggestion-tab-items').css('height', panel1.height()- actionHeaderPanelsSizeMargin + 'px');
                        iElement.find('.stat-detail-tab-items').css('height', panel2.height()- statHeaderPanelsSizeMargin + 'px');
                    });

                    handler.bind('mousedown', function (ev) {
                        ev.preventDefault();
                        drag = true;
                    });

                    angular.element(document).bind('mouseup', function () {
                        drag = false;
                    });

                    //Initialisation
                    panel1.css('height', '325px');
                    handler.css('top', '325px');
                    panel2.css('top', '325px');

                    iElement.find('.action-suggestion-tab-items').css('height', panel1.height()- actionHeaderPanelsSizeMargin + 'px');
                    iElement.find('.stat-detail-tab-items').css('height', panel2.height()- statHeaderPanelsSizeMargin + 'px');

                });
            }
        };
    }

    angular.module('data-prep.suggestions-stats')
        .directive('suggestionsStats', SuggestionsStats);
})();