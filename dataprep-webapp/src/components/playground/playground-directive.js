(function () {
    'use strict';

    function Playground() {
        return {
            restrict: 'E',
            templateUrl: 'components/playground/playground.html',
            bindToController: true,
            controllerAs: 'playgroundCtrl',
            controller: 'PlaygroundCtrl'
        };
    }

    angular.module('data-prep.playground')
        .directive('playground', Playground);
})();