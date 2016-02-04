export default function NavbarCtrl($timeout, $state, version, copyRights, OnboardingService, DatasetService, StateService, EasterEggsService) {
    'ngInject';

    var vm = this;

    //--------------------------------------------------------------------------------------------------------------
    //-------------------------------------------ONBOARDING---------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------
    var tourId = 'dataset';
    this.startTour = OnboardingService.startTour;
    this.version = version;
    this.copyRights = copyRights;

    if ($state.current.name === 'nav.home.datasets' && !$state.params.datasetid && OnboardingService.shouldStartTour(tourId)) {
        DatasetService.getDatasets().then(function () {
            $timeout(OnboardingService.startTour.bind(null, tourId), 100, false);
        });
    }


    //--------------------------------------------------------------------------------------------------------------
    //---------------------------------------------FEEDBACK---------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------
    vm.openFeedbackForm = StateService.showFeedback;


    //--------------------------------------------------------------------------------------------------------------
    //----------------------------------------------SEARCH----------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------

    vm.searchInput = '';
    vm.search = function () {
        // just in case something funny happens...
        EasterEggsService.enableEasterEgg(vm.searchInput);
    };

}