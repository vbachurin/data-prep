(function () {
    'use strict';

    function NavbarCtrl($state, OnboardingService, DatasetService, FeedbackService) {
        var vm =this;
        vm.feedbackModal = false;
        vm.feedback = {
            title : '',
            mail : '',
            severity : '',
            type : '',
            description: ''
        };

        vm.feedbackTypes = [{name : 'Bugs'}, {name: 'Improvements'}];
        vm.feedbackSeverities = [{name : 'Critical'}, {name: 'Major'},{name : 'Minor'}, {name: 'Trivial'}];

        //////////////////////////////ONBOARDING/////////////////////////
        var tourId = 'dataset';
        vm.startTour = OnboardingService.startTour;

        if ($state.current.name === 'nav.home.datasets' && !$state.params.datasetid && OnboardingService.shouldStartTour(tourId)) {
            DatasetService.getDatasets().then(function () {
                setTimeout(OnboardingService.startTour.bind(null, tourId), 100);
            });
        }

        //////////////////////////////FEEDBACK/////////////////////////
        vm.openFeedbackForm = function() {
            vm.feedbackModal = true;
        };

        vm.sendFeedback = function() {
            FeedbackService.sendFeedback(vm.feedback);
        };

    }

    angular.module('data-prep.navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();