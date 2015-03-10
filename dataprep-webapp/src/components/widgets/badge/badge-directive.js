(function() {
    'use strict';

    function TalendBadge() {
        return {
            restrict: 'E',
            replace: true,
            templateUrl: 'components/widgets/badge/badge.html',
            scope: {
                onClose: '&',
                text: '@'
            },
            bindToController: true,
            controllerAs: 'badgeCtrl',
            controller: function() {}
        };
    }

    angular.module('talend.widget')
        .directive('talendBadge', TalendBadge);
})();