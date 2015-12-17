(function () {
    'use strict';

    function NavbarCtrl($state, $translate, version, OnboardingService, DatasetService, FeedbackRestService, MessageService, EasterEggsService) {
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

        $translate([
            'FEEDBACK_TYPE_BUG',
            'FEEDBACK_TYPE_IMPROVEMENT',
            'FEEDBACK_SEVERITY_CRITICAL',
            'FEEDBACK_SEVERITY_MAJOR',
            'FEEDBACK_SEVERITY_MINOR',
            'FEEDBACK_SEVERITY_TRIVIAL'
        ]).then(function(translations) {
            vm.feedbackTypes = [
                {name: translations.FEEDBACK_TYPE_BUG, value: 'BUG'},
                {name: translations.FEEDBACK_TYPE_IMPROVEMENT, value: 'IMPROVEMENT'}];
            vm.feedbackSeverities = [
                {name: translations.FEEDBACK_SEVERITY_CRITICAL, value: 'CRITICAL'},
                {name: translations.FEEDBACK_SEVERITY_MAJOR, value: 'MAJOR'},
                {name: translations.FEEDBACK_SEVERITY_MINOR, value: 'MINOR'},
                {name: translations.FEEDBACK_SEVERITY_TRIVIAL, value: 'TRIVIAL'}];
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

        //--------------------------------------------------------------------------------------------------------------
        //----------------------------------------------SEARCH----------------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------

        vm.searchInput='';
        vm.search = function() {
            // just in case something funny happens...
            EasterEggsService.enableEasterEgg(vm.searchInput);
        };

    }



    angular.module('data-prep.navbar')
        .controller('NavbarCtrl', NavbarCtrl);
})();
