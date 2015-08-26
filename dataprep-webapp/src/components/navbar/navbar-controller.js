(function () {
    'use strict';

    function NavbarCtrl($state, OnboardingService) {
        this.startTour = OnboardingService.startTour;

        if ($state.current.name === 'nav.home.datasets' && !$state.params.datasetid && OnboardingService.shouldStartTour()) {
            OnboardingService.startTour();
        }
    }

    angular.module('data-prep.navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();