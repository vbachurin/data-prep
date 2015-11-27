describe('Navbar controller', function () {
    'use strict';

    var createController, scope, $stateMock;

    beforeEach(module('data-prep.navbar'));

    beforeEach(inject(function ($rootScope, $controller, $q, DatasetService, OnboardingService) {
        jasmine.clock().install();
        scope = $rootScope.$new();
        $stateMock = {};

        createController = function () {
            return $controller('NavbarCtrl', {
                $scope: scope,
                $state: $stateMock
            });
        };

        spyOn(DatasetService, 'getDatasets').and.returnValue($q.when());
        spyOn(OnboardingService, 'startTour').and.returnValue();
    }));

    afterEach(function() {
        jasmine.clock().uninstall();
    });

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
            expect(DatasetService.getDatasets).toHaveBeenCalled();
            expect(OnboardingService.startTour).not.toHaveBeenCalled();

            //when
            jasmine.clock().tick(100);

            //then
            expect(OnboardingService.startTour).toHaveBeenCalledWith('dataset');
        }));

        it('should not start tour on dataset playground page', inject(function (DatasetService, OnboardingService) {
            //given
            $stateMock.params = {datasetid: '154645'};
            $stateMock.current = {name: 'nav.home.datasets'};

            //when
            createController();
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(DatasetService.getDatasets).not.toHaveBeenCalled();
            expect(OnboardingService.startTour).not.toHaveBeenCalled();
        }));

        it('should not start tour on other than dataset page', inject(function (DatasetService, OnboardingService) {
            //given
            $stateMock.params = {};
            $stateMock.current = {name: 'nav.home.other'};

            //when
            createController();
            scope.$digest();
            jasmine.clock().tick(100);

            //then
            expect(DatasetService.getDatasets).not.toHaveBeenCalled();
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
            jasmine.clock().tick(100);

            //then
            expect(DatasetService.getDatasets).not.toHaveBeenCalled();
            expect(OnboardingService.startTour).not.toHaveBeenCalled();
        }));
    });

    describe('feedback ', function() {
        beforeEach(inject(function(OnboardingService) {
            spyOn(OnboardingService, 'shouldStartTour').and.returnValue(false);
            $stateMock.params = {};
            $stateMock.current = {name: 'nav.home.datasets'};
        }));

        it('should openFeedbackForm ', inject(function () {
            //given
            var ctrl = createController();

            //given
            ctrl.openFeedbackForm();

            //then
            expect(ctrl.feedbackModal ).toBe(true);
        }));

        it('should send feedback', inject(function ($q, FeedbackService) {
            //given
            var ctrl = createController();
            ctrl.feedback = {
                title : 'test',
                mail : 'test',
                severity : 'test',
                type : 'test',
                description: 'test'
            };
            spyOn(FeedbackService, 'sendFeedback').and.returnValue($q.when(true));

            //given
            ctrl.sendFeedback ();
            scope.$digest();

            //then
            expect(FeedbackService.sendFeedback).toHaveBeenCalledWith({
                title : 'test',
                mail : 'test',
                severity : 'test',
                type : 'test',
                description: 'test'
            });
            expect(ctrl.feedbackModal ).toBe(false);
        }));
    });
});
