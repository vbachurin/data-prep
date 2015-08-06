(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendTabs
     * @description Tabs directive. This is paired with tabs item directive.
     * @restrict E
     * @usage
     <talend-tabs>
        <talend-tabs-item tab-title="tab 1 title">
            Content tab 1
        </talend-tabs-item>
        <talend-tabs-item tab-title="tab 2 title" default="true">
            Content tab 2
        </talend-tabs-item>
        <talend-tabs-item tab-title="tab 3 title">
            Content tab 3
        </talend-tabs-item>
     </talend-tabs>
     * @param {string} title The tab title to display
     * @param {boolean} default The default tab to select
     */
    function TalendTabs() {
        return {
            restrict: 'E',
            transclude : true,
            templateUrl: 'components/widgets/tabs/tabs.html',
            controller: 'TalendTabsCtrl',
            controllerAs: 'tabsCtrl'
        };
    }

    angular.module('talend.widget')
        .directive('talendTabs', TalendTabs);
})();