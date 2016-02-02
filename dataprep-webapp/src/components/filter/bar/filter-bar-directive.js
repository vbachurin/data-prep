export default function FilterBar(state, FilterService) {
    'ngInject';

    return {
        restrict: 'E',
        templateUrl: 'app/components/filter/bar/filter-bar.html',
        scope: {},
        bindToController: true,
        controller: function () {
            this.filterService = FilterService;
            this.state = state;
        },
        controllerAs: 'filterBarCtrl'
    };
}