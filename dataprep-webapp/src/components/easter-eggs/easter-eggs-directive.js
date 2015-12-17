(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.easter-eggs.directive:EasterEggs
     * @description DataPrep easter eggs
     * @restrict E
     * @usage
     <easter-eggs>
     </easter-eggs>
     */
    function EasterEggs() {
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            templateUrl: 'components/easter-eggs/easter-eggs.html',
            bindToController: true,
            controllerAs: 'easterEggsCtrl',
            controller: 'EasterEggsCtrl'
        };
    }

    angular.module('data-prep.easter-eggs')
        .directive('easterEggs', EasterEggs);
})();