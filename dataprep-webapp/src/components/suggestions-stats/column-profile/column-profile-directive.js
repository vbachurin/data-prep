export default function ColumnProfile() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/suggestions-stats/column-profile/column-profile.html',
        bindToController: true,
        controllerAs: 'columnProfileCtrl',
        controller: 'ColumnProfileCtrl'
    };
}