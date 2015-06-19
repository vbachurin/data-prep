(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name talend.widget.directive:TalendTabs
     * @description Tabs directive. This MUST be used with tabs directive. It register itself to the tabs component.
     * @restrict E
     * @usage
     <talend-tabs>
        <talend-tabs-item title="tab 1 title">
            Content tab 1
        </talend-tabs-item>
        <talend-tabs-item title="tab 2 title" default="true">
            Content tab 2
        </talend-tabs-item>
        <talend-tabs-item title="tab 3 title">
            Content tab 3
        </talend-tabs-item>
     </talend-tabs>
     * @param {string} title The tab title to display
     * @param {boolean} default The default tab to select
     */
    function TalendTabsItem() {
        return {
            restrict: 'E',
            replace: true,
            transclude : true,
            templateUrl: 'components/widgets/tabs/tabs-item.html',
            require: '^^talendTabs',
            scope: {
                title: '@',
                'default': '='
            },
            bindToController: true,
            controller: function() {},
            controllerAs: 'tabsItemCtrl',
            link: function(scope, iElement, iAttrs, tabsCtrl) {
                var ctrl = scope.tabsItemCtrl;

                tabsCtrl.register(ctrl);
                if(ctrl.default) {
                    tabsCtrl.select(ctrl);
                }
            }
        };
    }

    angular.module('talend.widget')
        .directive('talendTabsItem', TalendTabsItem);
})();