/**
 * @ngdoc directive
 * @name data-prep.star-wars.directive:StarWars
 * @description StarWars easter eggs
 * @restrict E
 * @usage <star-wars></star-wars>
 */
export default function StarWars() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/easter-eggs/star-wars/star-wars.html'
    };
}