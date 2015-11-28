(function () {
    'use strict';

    function NavbarCtrl($state, $translate, version, OnboardingService, DatasetService, FeedbackRestService, MessageService) {
        var vm = this;

        //--------------------------------------------------------------------------------------------------------------
        //-------------------------------------------ONBOARDING---------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        var tourId = 'dataset';
        this.startTour = OnboardingService.startTour;
        this.version = version;

        if ($state.current.name === 'nav.home.datasets' && !$state.params.datasetid && OnboardingService.shouldStartTour(tourId)) {
            DatasetService.getDatasets().then(function () {
                setTimeout(OnboardingService.startTour.bind(null, tourId), 100);
            });
        }

        //--------------------------------------------------------------------------------------------------------------
        //---------------------------------------------FEEDBACK---------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
        vm.feedbackModal = false;
        vm.isSendingFeedback = false;

        $translate.onReady(function () {
            vm.feedbackTypes = [
                {name: $translate.instant('FEEDBACK_TYPE_BUG'), value: 'BUG'},
                {name: $translate.instant('FEEDBACK_TYPE_IMPROVEMENT'), value: 'IMPROVEMENT'}];
            vm.feedbackSeverities = [
                {name: $translate.instant('FEEDBACK_SEVERITY_CRITICAL'), value: 'CRITICAL'},
                {name: $translate.instant('FEEDBACK_SEVERITY_MAJOR'), value: 'MAJOR'},
                {name: $translate.instant('FEEDBACK_SEVERITY_MINOR'), value: 'MINOR'},
                {name: $translate.instant('FEEDBACK_SEVERITY_TRIVIAL'), value: 'TRIVIAL'}];
        });

        resetForm();

        function resetForm() {
            vm.feedback = {
                title: '',
                mail: '',
                severity: 'MINOR',
                type: 'BUG',
                description: ''
            };
        }

        vm.openFeedbackForm = function () {
            vm.feedbackModal = true;
        };

        vm.sendFeedback = function () {
            vm.feedbackForm.$commitViewValue();
            vm.isSendingFeedback = true;
            FeedbackRestService.sendFeedback(vm.feedback)
                .then (function () {
                    resetForm();
                    vm.feedbackModal = false;
                    MessageService.success('FEEDBACK_SENT_TITLE', 'FEEDBACK_SENT_CONTENT');
                })
                .finally(function () {
                    vm.isSendingFeedback = false;
                });
        };

    }

    angular.module('data-prep.navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();
