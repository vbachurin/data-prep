(function () {
    'use strict';

    function NavbarCtrl($state, OnboardingService, $timeout, DatasetService) {
        this.startTour = OnboardingService.startTour;

        if ($state.current.name === 'nav.home.datasets' && !$state.params.datasetid && OnboardingService.shouldStartTour()) {
            DatasetService.getDatasets().then(function () {
                $timeout(OnboardingService.startTour);
            });
        }
    }

    angular.module('data-prep.navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();