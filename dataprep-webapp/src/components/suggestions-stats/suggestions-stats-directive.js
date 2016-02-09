/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function () {
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
            link: function (scope, iElement) {

                $timeout(function () {
                    var handler = iElement.find('.split-handler').eq(0);
                    var panel1 = iElement.find('.split-pane1').eq(0);
                    var panel2 = iElement.find('.split-pane2').eq(0);

                    //Initialization of the right panel
                    // 325px : to have at least 5 actions in the top panel
                    panel1.css('height', '310px');
                    handler.css('top', '310px');
                    panel2.css('top', '310px');
                }, 0, false);
            }
        };
    }

    angular.module('data-prep.suggestions-stats')
        .directive('suggestionsStats', SuggestionsStats);
})();