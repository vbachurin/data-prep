/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Onboarding service', function () {
    'use strict';

    const TOUR_OPTIONS_KEY = 'org.talend.dataprep.tour_options';
    let stateMock;
    const introJsMock = {
        setOptions: function () {
            return this;
        },
        oncomplete: function () {
            return this;
        },
        onexit: function () {
            return this;
        },
        start: function () {
        }
    };

    beforeEach(angular.mock.module('data-prep.services.onboarding', ($provide) => {
        stateMock = {
            inventory: {
                homeFolderId: 'Lw=='
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(function ($state, $window) {
        $window.introJs = function () {
            return introJsMock;
        };

        spyOn(introJsMock, 'setOptions').and.callThrough();
        spyOn(introJsMock, 'oncomplete').and.callThrough();
        spyOn(introJsMock, 'onexit').and.callThrough();
        spyOn(introJsMock, 'start').and.callThrough();
        spyOn($state, 'go').and.returnValue();
    }));

    afterEach(inject(function ($window) {
        $window.localStorage.removeItem(TOUR_OPTIONS_KEY);
    }));

    it('should return true when tour has not been completed yet', inject(function ($window, OnboardingService) {
        //given
        $window.localStorage.removeItem(TOUR_OPTIONS_KEY);

        //when
        var result = OnboardingService.shouldStartTour('preparation');

        //then
        expect(result).toBe(true);
    }));

    it('should return false when tour has already been completed', inject(function ($window, OnboardingService) {
        //given
        $window.localStorage.setItem(TOUR_OPTIONS_KEY, JSON.stringify({preparation: true}));

        //when
        var result = OnboardingService.shouldStartTour('preparation');

        //then
        expect(result).toBe(false);
    }));

    it('should configure intro.js options', inject(function ($timeout, OnboardingService) {
        //given

        //when
        OnboardingService.startTour('preparation');
        $timeout.flush(200);

        //then
        expect(introJsMock.setOptions).toHaveBeenCalled();
        var options = introJsMock.setOptions.calls.argsFor(0)[0];
        expect(options.nextLabel).toBe('NEXT');
        expect(options.prevLabel).toBe('BACK');
        expect(options.skipLabel).toBe('SKIP');
        expect(options.doneLabel).toBe('LET ME TRY');
    }));

    it('should create/adapt preparation tour step', inject(function ($timeout, OnboardingService) {
        //when
        OnboardingService.startTour('preparation');
        $timeout.flush(200);

        //then
        expect(introJsMock.setOptions).toHaveBeenCalled();
        var options = introJsMock.setOptions.calls.argsFor(0)[0];
        expect(options.steps[0]).toEqual({
            element: '#nav_home_preparations',
            position: 'right',
            intro: '<div class="introjs-tooltiptitle"><center>Preparations</center></div><div class="introjs-tooltipcontent">Here you can browse through and manage the preparations you created.</br>A preparation is the outcome of the different steps applied to cleanse your data.</div>'
        });
    }));

    it('should create/adapt playground step', inject(function ($timeout, OnboardingService) {
        //when
        OnboardingService.startTour('playground');
        $timeout.flush(200);

        //then
        expect(introJsMock.setOptions).toHaveBeenCalled();
        var options = introJsMock.setOptions.calls.argsFor(0)[0];
        expect(options.steps[0]).toEqual({
            element: '.no-js',
            position: 'right',
            intro: '<div class="introjs-tooltiptitle"><center>Welcome to the preparation view</center></div><div class="introjs-tooltipcontent">In this view, you can apply preparation steps to your dataset.</br>This table represents the result of your preparation.</div>'
        });
    }));

    it('should create/adapt column selection', inject(function ($timeout, OnboardingService) {
        //when
        OnboardingService.startTour('playground');
        $timeout.flush(200);

        //then
        expect(introJsMock.setOptions).toHaveBeenCalled();
        var options = introJsMock.setOptions.calls.argsFor(0)[0];
        expect(options.steps[1]).toEqual({
            element: '#datagrid .slick-header-columns-right > .slick-header-column',
            position: 'right',
            intro: '<div class="introjs-tooltiptitle"><center>Columns</center></div><div class="introjs-tooltipcontent">Select a column to discover the transformation functions you can apply to your data.</div>'
        });
    }));

    it('should create/adapt recipe tour step', inject(function ($timeout, OnboardingService) {
        //when
        OnboardingService.startTour('recipe');
        $timeout.flush(200);

        //then
        expect(introJsMock.setOptions).toHaveBeenCalled();
        var options = introJsMock.setOptions.calls.argsFor(0)[0];
        expect(options.steps[0]).toEqual({
            element: '#help-recipe > .recipe',
            position: 'right',
            intro: '<div class="introjs-tooltiptitle"><center>Recipe</center></div><div class="introjs-tooltipcontent">Here is your recipe. A recipe is literally defined as "a set of directions with a list of ingredients for making or preparing something".</br>In Talend Data Preparation, the ingredients are the raw data, called datasets, and the directions are the set of functions applied to the dataset.</br>Here you can preview, edit, delete, activate or deactivate every function included in the recipe you created.</div>'
        });
    }));

    it('should save "preparation" state in localstorage on tour complete', inject(function ($timeout, $window, OnboardingService) {
        //given
        $window.localStorage.removeItem(TOUR_OPTIONS_KEY);

        OnboardingService.startTour('preparation');
        $timeout.flush(200);
        expect(introJsMock.oncomplete).toHaveBeenCalled();

        var oncomplete = introJsMock.oncomplete.calls.argsFor(0)[0];

        //when
        oncomplete();

        //then
        var options = JSON.parse($window.localStorage.getItem(TOUR_OPTIONS_KEY));
        expect(options.preparation).toBe(true);
    }));

    it('should save "preparation" state in localstorage on tour exit', inject(function ($timeout, $window, OnboardingService) {
        //given
        $window.localStorage.removeItem(TOUR_OPTIONS_KEY);

        OnboardingService.startTour('preparation');
        $timeout.flush(200);
        expect(introJsMock.onexit).toHaveBeenCalled();

        var onexit = introJsMock.onexit.calls.argsFor(0)[0];

        //when
        onexit();

        //then
        var options = JSON.parse($window.localStorage.getItem(TOUR_OPTIONS_KEY));
        expect(options.preparation).toBe(true);
    }));

    it('should start onboarding', inject(function ($timeout, $window, OnboardingService) {
        //when
        OnboardingService.startTour('preparation');
        $timeout.flush(200);

        //then
        expect(introJsMock.start).toHaveBeenCalled();
    }));

    it('should redirect to "preparations" before starting onboarding', inject(($state, OnboardingService) => {
        //given
        $state.current = {
            name: 'nav.index.datasets'
        };

        //when
        OnboardingService.startTour('preparation');

        //then
        expect($state.go).toHaveBeenCalledWith('nav.index.preparations', {folderId: stateMock.inventory.homeFolderId});
    }));

    it('should redirect BACK to "datasets" after redirecting to "preparations" ', inject(($timeout, $state, OnboardingService) => {
        //given
        $state.current = {
            name: 'nav.index.datasets'
        };

        OnboardingService.startTour('preparation');
        expect($state.go).toHaveBeenCalledWith('nav.index.preparations', {folderId: stateMock.inventory.homeFolderId})

        //when
        $timeout.flush(200);
        expect(introJsMock.onexit).toHaveBeenCalled();
        var onexit = introJsMock.onexit.calls.argsFor(0)[0];

        //when
        onexit();

        //then
        expect($state.go).toHaveBeenCalledWith('nav.index.datasets');
    }));
});