(function () {
    'use strict';

    function NavbarCtrl($state, $translate, version, OnboardingService, DatasetService, FeedbackService) {
        var vm =this;
        vm.feedbackModal = false;
        vm.feedback = {
            title : '',
            mail : '',
            severity : '',
            type : '',
            description: ''
        };

        vm.feedbackTypes = [
            {name: $translate.instant('FEEDBACK_TYPE_BUGS')},
            {name: $translate.instant('FEEDBACK_TYPE_IMPROVEMENTS')}];
        vm.feedbackSeverities = [
            {name:  $translate.instant('FEEDBACK_SEVERITY_1')},
            {name:  $translate.instant('FEEDBACK_SEVERITY_2')},
            {name:  $translate.instant('FEEDBACK_SEVERITY_3')},
            {name:  $translate.instant('FEEDBACK_SEVERITY_4')}];

        //////////////////////////////ONBOARDING/////////////////////////
        var tourId = 'dataset';
        this.startTour = OnboardingService.startTour;
        this.version = version;

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
            FeedbackService.sendFeedback(vm.feedback).then (
                function(){
                    vm.feedbackModal = false;
                }
            );
        };

    }

    angular.module('data-prep.navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();
