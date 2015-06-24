(function () {
    'use strict';

    function NavbarCtrl($state, $timeout, DatasetService, OnboardingService) {
        this.startTour = OnboardingService.startTour;

        if ($state.current.name === 'nav.home.datasets' && !$state.params.datasetid && OnboardingService.shouldStartTour()) {
            DatasetService.refreshDatasets().then(function () {
                $timeout(OnboardingService.startTour);
            });
        }
    }

    angular.module('data-prep.navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();