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
            transclude: true,
            replace: true,
            templateUrl: 'components/widgets/star-wars/star-wars.html',
            bindToController: true,
            //controller: 'StarWarsCtrl',
            //controllerAs: 'starWarsCtrl',
            link: {
            }
        };
    }

    angular.module('talend.widget')
        .directive('starWars', StarWars);
})();