(function () {
    'use strict';

    function ActionsList() {
        return {
            restrict: 'E',
            templateUrl: 'components/suggestions-stats/actions-list/actions-list.html',
            bindToController: true,
            controllerAs: 'actionsListCtrl',
            controller: 'ActionsListCtrl',
            scope: {
                actions: '=',
                shouldRenderCategory: '=',
                shouldRenderAction: '=',
                scope: '@'
            }
        };
    }

    angular.module('data-prep.actions-list')
        .directive('actionsList', ActionsList);
})();