export default function Home() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/home/home.html',
        scope: {},
        bindToController: true,
        controller: 'HomeCtrl',
        controllerAs: 'homeCtrl'
    };
}