(function() {
    'use strict';

    function TalendConfirm() {
        return {
            restrict: 'E',
            templateUrl: 'components/widgets/confirm/confirm.html',
            scope: {
                disableEnter: '=',
                texts: '='
            },
            bindToController: true,
            controller: 'TalendConfirmCtrl',
            controllerAs: 'confirmCtrl'
        };
    }

    angular.module('talend.widget')
        .directive('talendConfirm', TalendConfirm);
})();