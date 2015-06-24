(function() {
    'use strict';

    function DisableRightClick() {
        return {
            restrict: 'A',
            link: function(scope, iElement) {
                iElement.bind('contextmenu', function(e) {
                    e.preventDefault();
                });
            }
        };
    }

    angular.module('data-prep.services.utils')
        .directive('disableRightClick', DisableRightClick);
})();