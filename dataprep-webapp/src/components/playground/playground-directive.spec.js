describe('Playground directive', function() {
    'use strict';

    var scope, createElement, element;

    var metadata = {
        'id': '12ce6c32-bf80-41c8-92e5-66d70f22ec1f',
        'name': 'US States',
        'author': 'anonymousUser',
        'created': '02-03-2015 14:52',
        records: '3'
    };

    beforeEach(module('data-prep.playground'));
    beforeEach(module('htmlTemplates'));
    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'FILE_DETAILS': 'File: {{name}} ({{records}} lines)'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(inject(function($state, $rootScope, $compile, PreparationService) {
        scope = $rootScope.$new();
        createElement = function() {
            element = angular.element('<playground></playground>');
            angular.element('body').append(element);

            $compile(element)(scope);
            scope.$digest();
        };

        spyOn(PreparationService, 'refreshPreparations').and.callFake(function() {});
        spyOn($state, 'go').and.callFake(function() {});
    }));

    afterEach(inject(function($stateParams) {
        scope.$destroy();
        element.remove();

        $stateParams.prepid = null;
        $stateParams.datasetid = null;
    }));

    it('should render playground elements', inject(function(PlaygroundService) {
        //given
        PlaygroundService.currentMetadata = metadata;

        //when
        createElement();

        //then
        var playground = angular.element('body').find('.playground').eq(0);
        var playgroundModal = playground.parent();

        //check header is present and contains description and search filter
        expect(playgroundModal.find('.modal-header').length).toBe(1);
        expect(playgroundModal.find('.modal-header').eq(0).find('li').eq(1).text().trim()).toBe('File: US States (3 lines)');

        //check left slidable is hidden recipe with left slide action
        expect(playground.eq(0).find('.slidable').eq(0).hasClass('recipe')).toBe(true);
        expect(playground.eq(0).find('.slidable').eq(0).hasClass('slide-hide')).toBe(true);
        expect(playground.eq(0).find('.slidable').eq(0).find('.action').eq(0).hasClass('right')).toBe(false);

        //check right slidable is displayed transformations with right slide action
        expect(playground.eq(0).find('.slidable').eq(1).hasClass('suggestions')).toBe(true);
        expect(playground.eq(0).find('.slidable').eq(1).hasClass('slide-hide')).toBe(false);
        expect(playground.eq(0).find('.slidable').eq(1).find('.action').eq(0).hasClass('right')).toBe(true);

        //check datagrid and filters are present
        expect(playground.eq(0).find('.filter-list').length).toBe(1);
        expect(playground.eq(0).find('.filter-list').find('.filter-search').length).toBe(1);
        expect(playground.eq(0).find('datagrid').length).toBe(1);
    }));

    it('should change ng-show ng-hide button', inject(function(PlaygroundService) {
        //given
        PlaygroundService.currentMetadata = metadata;

        //when
        createElement();

        //then
        var stepsHeader = angular.element('body').find('#stepsHeaderId');
        //console.log(stepsHeader.length);
        //var stepsHeader = playground.find('#stepsHeaderId').eq(0);
        var editionBtn = stepsHeader.find('button').eq(0)[0];
        //console.log(editionBtn.length);
        //console.log(editionBtn);
        var event = new angular.element.Event('click');
		//
        ////when
        editionBtn.trigger(event);
        expect(editionBtn.hasClass('ng-hide').toBe(true));
    }));

    describe('hide playground', function() {
        beforeEach(inject(function(PlaygroundService, PreparationService) {
            PlaygroundService.currentMetadata = metadata;
            createElement();

            PlaygroundService.show();
            scope.$apply();
            expect(PreparationService.refreshPreparations).not.toHaveBeenCalled();
        }));

        it('should change route to preparations list on preparation playground hide', inject(function($state, $stateParams, PlaygroundService) {
            //given: simulate playground route with preparation id
            $stateParams.prepid = '1234';

            //when
            PlaygroundService.hide();
            scope.$apply();

            //then
            expect($state.go).toHaveBeenCalledWith('nav.home.preparations', {prepid: null});
        }));

        it('should change route to datasets list on dataset playground hide', inject(function($state, $stateParams, PlaygroundService) {
            //given: simulate playground route with preparation id
            $stateParams.datasetid = '1234';

            //when
            PlaygroundService.hide();
            scope.$apply();

            //then
            expect($state.go).toHaveBeenCalledWith('nav.home.datasets', {datasetid: null});
        }));

        it('should do nothing if playground is not routed', inject(function($state, $stateParams, PlaygroundService, PreparationService) {
            //given: simulate no preparation id in route
            $stateParams.prepid = null;
            $stateParams.datasetid = null;

            //when
            PlaygroundService.hide();
            scope.$apply();

            //then
            expect(PreparationService.refreshPreparations).toHaveBeenCalled();
            expect($state.go).not.toHaveBeenCalled();
        }));

        it('should refresh preparations on playground hide', inject(function(PlaygroundService, PreparationService) {
            //when
            PlaygroundService.hide();
            scope.$apply();

            //then
            expect(PreparationService.refreshPreparations).toHaveBeenCalled();
        }));
    });

});