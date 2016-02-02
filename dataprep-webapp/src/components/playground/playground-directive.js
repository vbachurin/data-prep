/**
 * @ngdoc directive
 * @name data-prep.playground.directive:Playground
 * @description This directive create the playground.
 * @restrict E
 */
export default function Playground() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/playground/playground.html',
        bindToController: true,
        controllerAs: 'playgroundCtrl',
        controller: 'PlaygroundCtrl'
    };
}