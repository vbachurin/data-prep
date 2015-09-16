(function () {
    'use strict';

    function NavbarCtrl($state, OnboardingService, DatasetService) {

        var tourId = 'dataset';
        this.startTour = OnboardingService.startTour;

        if ($state.current.name === 'nav.home.datasets' && !$state.params.datasetid && OnboardingService.shouldStartTour(tourId)) {
            DatasetService.getDatasets().then(function () {
                setTimeout(OnboardingService.startTour.bind(null, tourId), 100);
            });
        }
    }

    angular.module('data-prep.navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();