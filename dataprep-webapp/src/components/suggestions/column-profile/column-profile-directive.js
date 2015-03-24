(function() {
    'use strict';

    function ColumnProfile() {
        return {
            restrict: 'E',
            templateUrl: 'components/suggestions/column-profile/column-profile.html',
            bindToController: true,
            controllerAs: 'columnProfileCtrl',
            controller: 'ColumnProfileCtrl'
        };
    }

    angular.module('data-prep.column-profile')
        .directive('columnProfile', ColumnProfile);
})();