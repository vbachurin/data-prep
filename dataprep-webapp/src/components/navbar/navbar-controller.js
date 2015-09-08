(function () {
    'use strict';

    function NavbarCtrl($state, OnboardingService, $timeout, DatasetService) {

        var tourId = 'dataset';
        this.startTour = OnboardingService.startTour;

        if ($state.current.name === 'nav.home.datasets' && !$state.params.datasetid && OnboardingService.shouldStartTour(tourId)) {
            DatasetService.getDatasets().then(function () {
                $timeout(OnboardingService.startTour.bind(null, tourId));
            });
        }
    }

    angular.module('data-prep.navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();