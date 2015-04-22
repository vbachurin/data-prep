(function() {
    'use strict';

    function PreparationList() {
        return {
            restrict: 'E',
            templateUrl: 'components/preparation-list/preparation-list.html',
            bindToController: true,
            controllerAs: 'preparationListCtrl',
            controller: 'PreparationListCtrl'
        };
    }

    angular.module('data-prep.preparation-list')
        .directive('preparationList', PreparationList);
})();