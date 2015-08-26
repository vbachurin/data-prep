describe('Navbar controller', function () {
    'use strict';

    var createController, scope, $stateMock;

    beforeEach(module('data-prep.navbar'));

    beforeEach(inject(function ($rootScope, $controller, $q, DatasetService, OnboardingService) {
        scope = $rootScope.$new();
        $stateMock = {};

        createController = function () {
            var ctrl = $controller('NavbarCtrl', {
                $scope: scope,
                $state: $stateMock
            });
            return ctrl;
        };

        spyOn(OnboardingService, 'startTour').and.returnValue();
    }));

    describe('onboarding not completed yet', function() {
        beforeEach(inject(function(OnboardingService) {
            spyOn(OnboardingService, 'shouldStartTour').and.returnValue(true);
        }));

        it('should start tour on dataset page', inject(function ($timeout, DatasetService, OnboardingService) {
            //given
            $stateMock.params = {};
            $stateMock.current = {name: 'nav.home.datasets'};

            //when
            createController();
            scope.$digest();

            //then
            expect(OnboardingService.startTour).toHaveBeenCalled();

        }));

        it('should not start tour on dataset playground page', inject(function (DatasetService, OnboardingService) {
            //given
            $stateMock.params = {datasetid: '154645'};
            $stateMock.current = {name: 'nav.home.datasets'};

            //when
            createController();
            scope.$digest();

            //then
            expect(OnboardingService.startTour).not.toHaveBeenCalled();
        }));

        it('should not start tour on other than dataset page', inject(function (DatasetService, OnboardingService) {
            //given
            $stateMock.params = {};
            $stateMock.current = {name: 'nav.home.other'};

            //when
            createController();
            scope.$digest();

            //then
            expect(OnboardingService.startTour).not.toHaveBeenCalled();
        }));
    });

    describe('onboarding completed yet', function() {
        beforeEach(inject(function(OnboardingService) {
            spyOn(OnboardingService, 'shouldStartTour').and.returnValue(false);
        }));

        it('should not start tour on dataset page', inject(function ($timeout, DatasetService, OnboardingService) {
            //given
            $stateMock.params = {};
            $stateMock.current = {name: 'nav.home.datasets'};

            //when
            createController();
            scope.$digest();

            //then
            expect(OnboardingService.startTour).not.toHaveBeenCalled();
        }));
    });
});
