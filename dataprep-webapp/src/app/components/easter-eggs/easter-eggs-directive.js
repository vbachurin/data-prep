/**
 * @ngdoc directive
 * @name data-prep.easter-eggs.directive:EasterEggs
 * @description DataPrep easter eggs
 * @restrict E
 * @usage <easter-eggs></easter-eggs>
 */
export default function EasterEggs() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/easter-eggs/easter-eggs.html',
        bindToController: true,
        controllerAs: 'easterEggsCtrl',
        controller: 'EasterEggsCtrl'
    };
}