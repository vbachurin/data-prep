export default function StatsDetails() {
    return {
        restrict: 'E',
        templateUrl: 'app/components/suggestions-stats/stats-details/stats-details.html',
        bindToController: true,
        controllerAs: 'statsDetailsCtrl',
        controller: 'StatsDetailsCtrl'
    };
}