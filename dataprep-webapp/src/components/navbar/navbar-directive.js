(function() {
    'use strict';

    function Navbar() {
        return {
            restrict: 'E',
            templateUrl: 'components/navbar/navbar.html',
            scope: {},
            bindToController: true,
            controller: 'NavbarCtrl',
            controllerAs: 'navbarCtrl'
        };
    }

    angular.module('data-prep.navbar')
        .directive('navbar', Navbar);
})();