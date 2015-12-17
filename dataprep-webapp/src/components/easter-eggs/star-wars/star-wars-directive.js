(function() {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.star-wars.directive:StarWars
     * @description StarWars easter eggs
     * @restrict E
     * @usage
     <star-wars>
     </star-wars>
     */
    function StarWars() {
        return {
            restrict: 'E',
            transclude: false,
            replace: true,
            templateUrl: 'components/easter-eggs/star-wars/star-wars.html',
            bindToController: true,
            link: {
            }
        };
    }

    angular.module('data-prep.easter-eggs')
        .directive('starWars', StarWars);
})();