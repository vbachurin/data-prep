(function() {
    'use strict';

    function Home() {
        return {
            restrict: 'E',
            templateUrl: 'components/home/home.html',
            scope: {},
            bindToController: true,
            controller: 'HomeCtrl',
            controllerAs: 'homeCtrl'
        };
    }

    angular.module('data-prep.home')
        .directive('home', Home);
})();