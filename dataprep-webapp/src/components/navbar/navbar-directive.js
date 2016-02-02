export default function Navbar() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/navbar/navbar.html',
        scope: {},
        bindToController: true,
        controller: 'NavbarCtrl',
        controllerAs: 'navbarCtrl'
    };
}