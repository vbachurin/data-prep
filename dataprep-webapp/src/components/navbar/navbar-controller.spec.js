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
        beforeEach(inject(function($q, FeedbackRestService, MessageService, OnboardingService) {
            spyOn(FeedbackRestService, 'sendFeedback').and.returnValue($q.when(true));
            spyOn(MessageService, 'success').and.returnValue(false);
            spyOn(OnboardingService, 'shouldStartTour').and.returnValue(false);
            $stateMock.params = {};
            $stateMock.current = {name: 'nav.home.datasets'};
        }));

        it('should open feedback modal', inject(function () {
            //given
            var ctrl = createController();
            ctrl.feedbackModal = false;

            //given
            ctrl.openFeedbackForm();

            //then
            expect(ctrl.feedbackModal).toBe(true);
        }));

        it('should send feedback', inject(function (FeedbackRestService) {
            //given
            var feedback = {
                title : 'test',
                mail : 'test',
                severity : 'test',
                type : 'test',
                description: 'test'
            };
            var ctrl = createController();
            ctrl.feedbackForm = {$commitViewValue: jasmine.createSpy('$commitViewValue').and.returnValue()};
            ctrl.feedback = feedback;

            //given
            ctrl.sendFeedback();

            //then
            expect(FeedbackRestService.sendFeedback).toHaveBeenCalledWith(feedback);
        }));

        it('should manage sending flag', function () {
            //given
            var feedback = {
                title : 'test',
                mail : 'test',
                severity : 'test',
                type : 'test',
                description: 'test'
            };
            var ctrl = createController();
            ctrl.feedbackForm = {$commitViewValue: jasmine.createSpy('$commitViewValue').and.returnValue()};
            ctrl.feedback = feedback;
            ctrl.isSendingFeedback = false;

            //given
            ctrl.sendFeedback();
            expect(ctrl.isSendingFeedback).toBe(true);
            scope.$digest();

            //then
            expect(ctrl.isSendingFeedback).toBe(false);
        });

        it('should close feedback modal on send success', function () {
            //given
            var feedback = {
                title : 'test',
                mail : 'test',
                severity : 'test',
                type : 'test',
                description: 'test'
            };
            var ctrl = createController();
            ctrl.feedbackForm = {$commitViewValue: jasmine.createSpy('$commitViewValue').and.returnValue()};
            ctrl.feedback = feedback;
            ctrl.feedbackModal = true;

            //given
            ctrl.sendFeedback();
            scope.$digest();

            //then
            expect(ctrl.feedbackModal).toBe(false);
        });

        it('should show message on send success', inject(function (MessageService) {
            //given
            var feedback = {
                title : 'test',
                mail : 'test',
                severity : 'test',
                type : 'test',
                description: 'test'
            };
            var ctrl = createController();
            ctrl.feedbackForm = {$commitViewValue: jasmine.createSpy('$commitViewValue').and.returnValue()};
            ctrl.feedback = feedback;
            ctrl.feedbackModal = true;

            //given
            ctrl.sendFeedback();
            scope.$digest();

            //then
            expect(MessageService.success).toHaveBeenCalledWith('FEEDBACK_SENT_TITLE', 'FEEDBACK_SENT_CONTENT');
        }));

        it('should reset form on send success', inject(function (MessageService) {
            //given
            var feedback = {
                title : 'test',
                mail : 'test',
                severity : 'test',
                type : 'test',
                description: 'test'
            };
            var ctrl = createController();
            ctrl.feedbackForm = {$commitViewValue: jasmine.createSpy('$commitViewValue').and.returnValue()};
            ctrl.feedback = feedback;
            ctrl.feedbackModal = true;

            //given
            ctrl.sendFeedback();
            scope.$digest();

            //then
            expect(ctrl.feedback).toEqual({
                title: '',
                mail: '',
                severity: 'MINOR',
                type: 'BUG',
                description: ''
            });
        }));
    });
});
