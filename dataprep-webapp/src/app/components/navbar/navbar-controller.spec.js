/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

describe('Navbar controller', function () {
    'use strict';

    var createController;
    var scope;
    var $stateMock;

    beforeEach(angular.mock.module('data-prep.navbar'));

    beforeEach(inject(function ($rootScope, $controller, $q, DatasetService, OnboardingService) {
        scope = $rootScope.$new();
        $stateMock = {};

        createController = function () {
            return $controller('NavbarCtrl', {
                $scope: scope,
                $state: $stateMock
            });
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
            $stateMock.current = { name: 'nav.index.datasets' };

            //when
            createController();
            scope.$digest();

            //then
            expect(OnboardingService.startTour).toHaveBeenCalled();
        }));

        it('should start tour on preparation page', inject(function (DatasetService, OnboardingService) {
            //given
            $stateMock.params = {};
            $stateMock.current = { name: 'nav.index.preparations' };

            //when
            createController();
            scope.$digest();

            //then
            expect(OnboardingService.startTour).toHaveBeenCalled();
        }));

        it('should not start tour on dataset playground page', inject(function ($timeout, DatasetService, OnboardingService) {
            //given
            $stateMock.params = { datasetid: '154645' };
            $stateMock.current = { name: 'nav.index.datasets' };

            //when
            createController();
            scope.$digest();

            //then
            expect(OnboardingService.startTour).not.toHaveBeenCalled();
        }));

        it('should not start tour on other than dataset page', inject(function ($timeout, DatasetService, OnboardingService) {
            //given
            $stateMock.params = {};
            $stateMock.current = { name: 'nav.index.other' };

            //when
            createController();
            scope.$digest();

            //then
            expect(OnboardingService.startTour).not.toHaveBeenCalled();
        }));
    });

    describe('onboarding completed', function() {
        beforeEach(inject(function(OnboardingService) {
            spyOn(OnboardingService, 'shouldStartTour').and.returnValue(false);
        }));

        it('should not start tour on dataset page', inject(function ($timeout, DatasetService, OnboardingService) {
            //given
            $stateMock.params = {};
            $stateMock.current = { name: 'nav.index.datasets' };

            //when
            createController();
            scope.$digest();
            $timeout.flush(100);

            //then
            expect(OnboardingService.startTour).not.toHaveBeenCalled();
        }));
    });

    describe('feedback ', function() {
        beforeEach(inject(function (StateService) {
            spyOn(StateService, 'showFeedback').and.returnValue();
        }));

        it('should open feedback modal', inject(function (StateService) {
            //given
            $stateMock.params = {};
            $stateMock.current = { name: 'nav.index.datasets' };
            var ctrl = createController();

            //given
            ctrl.openFeedbackForm();

            //then
            expect(StateService.showFeedback).toHaveBeenCalled();
        }));
    });
});
