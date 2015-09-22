describe('Playground directive', function () {
    'use strict';

    var scope, createElement, element;
    var stateMock;

    var metadata = {
        'id': '12ce6c32-bf80-41c8-92e5-66d70f22ec1f',
        'name': 'US States',
        'author': 'anonymousUser',
        'created': '02-03-2015 14:52',
        records: '3'
    };

    beforeEach(module('data-prep.playground', function($provide) {
        stateMock = {playground: {visible: true}};
        $provide.constant('state', stateMock);
    }));
    beforeEach(module('htmlTemplates'));
    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'FILE_DETAILS': 'File: {{name}} ({{records}} lines)',
            'SAMPLE_SIZE': 'Below is a sample size of'
        });
        $translateProvider.preferredLanguage('en');
    }));


    beforeEach(inject(function ($state, $rootScope, $compile, $q, $timeout, PreparationService, PlaygroundService, ExportService) {
        stateMock.playground.visible = true;
        scope = $rootScope.$new();

        createElement = function () {
            element = angular.element('<playground></playground>');
            angular.element('body').append(element);

            $compile(element)(scope);
            scope.$digest();
            $timeout.flush();
        };

        spyOn(PreparationService, 'refreshPreparations').and.returnValue();
        spyOn(PlaygroundService, 'createOrUpdatePreparation').and.returnValue($q.when(true));
        spyOn($state, 'go').and.returnValue();
        spyOn(ExportService, 'refreshTypes').and.returnValue($q.when([]));
        spyOn(ExportService, 'getParameters').and.returnValue({});

    }));

    afterEach(inject(function ($stateParams) {
        scope.$destroy();
        element.remove();

        $stateParams.prepid = null;
        $stateParams.datasetid = null;
    }));

    it('should render playground elements', function () {
        //given
        stateMock.playground.dataset = metadata;

        //when
        createElement();

        //then
        var playground = angular.element('body').find('.playground').eq(0);
        var playgroundModal = playground.parent();

        //check header is present and contains description and search filter
        expect(playgroundModal.find('.modal-header').length).toBe(1);
        expect(playgroundModal.find('.modal-header').eq(0).find('li').eq(0).text().trim()).toBe('File: US States (3 lines)');
        expect(playgroundModal.find('.modal-header').eq(0).find('li').eq(1).find('span').text().trim()).toBe('Below is a sample size of:');

        //check left slidable is hidden recipe with left slide action
        expect(playground.eq(0).find('.recipe').eq(0).hasClass('slide-hide')).toBe(true);
        expect(playground.eq(0).find('.recipe').eq(0).find('.action').eq(0).hasClass('right')).toBe(false);

        //check right slidable is displayed transformations with right slide action
        expect(playground.eq(0).find('.suggestions').eq(0).hasClass('slide-hide')).toBe(false);
        expect(playground.eq(0).find('.suggestions').eq(0).find('.action').eq(0).hasClass('right')).toBe(true);

        //check datagrid and filters are present
        expect(playground.eq(0).find('.filter-list').length).toBe(1);
        expect(playground.eq(0).find('.filter-list').find('.filter-search').length).toBe(1);
        expect(playground.eq(0).find('datagrid').length).toBe(1);
    });

    describe('recipe header', function () {
        beforeEach(inject(function(StateService) {
            stateMock.playground.nameEditionMode = true;

            spyOn(StateService, 'setNameEditionMode').and.callFake(function(value) {
                stateMock.playground.nameEditionMode = value;
            });
        }));

        it('should show/hide action buttons in the recipe header', function () {
            //given
            stateMock.playground.preparation = {id: '3e41168465e15d4'};
            stateMock.playground.dataset = metadata;
            createElement();

            var stepsHeader = angular.element('body > talend-modal').find('.steps-header').eq(0);
            var nonEditionSection = stepsHeader.find('div[ng-show="!playgroundCtrl.state.playground.nameEditionMode"]').eq(0);
            var editionSection = stepsHeader.find('div[ng-show="playgroundCtrl.state.playground.nameEditionMode"]').eq(0);

            var confirmBtn = stepsHeader.find('a.check-btn').eq(0);
            var editionBtn = stepsHeader.find('a.edit-btn').eq(0);

            expect(nonEditionSection.is(':visible')).toBe(false);
            expect(editionSection.is(':visible')).toBe(true);

            element.controller('playground').preparationName = 'My Preparation';

            //when
            var event = new angular.element.Event('click');
            confirmBtn.trigger(event);

            //then
            expect(nonEditionSection.is(':visible')).toBe(true);
            expect(editionSection.is(':visible')).toBe(false);

            //when
            var event2 = new angular.element.Event('click');
            editionBtn.trigger(event2);

            //then
            expect(nonEditionSection.is(':visible')).toBe(false);
            expect(editionSection.is(':visible')).toBe(true);

        });

        it('should toggle recipe on click on the On/Off switch', inject(function (RecipeBulletService) {
            //given
            spyOn(RecipeBulletService, 'toggleRecipe').and.returnValue();

            createElement();
            var chkboxOnOff = angular.element('body').find('.label-switch > input[type="checkbox"]');

            //when
            chkboxOnOff.trigger('click');

            //then
            expect(RecipeBulletService.toggleRecipe).toHaveBeenCalled();
        }));

        it('should switch OFF the On/Off switch when the 1st step is INACTIVE', inject(function (RecipeService) {
            //given
            stateMock.playground.dataset = metadata;
            var step = {
                inactive: false,
                transformation: {
                    stepId: '92771a304130e9',
                    name: 'propercase',
                    parameters: [],
                    items: [],
                    dynamic: false
                }
            };
            RecipeService.getRecipe().push(step);
            createElement();

            var chkboxOnOff = angular.element('body').find('.label-switch > input[type="checkbox"]');
            expect(chkboxOnOff.prop('checked')).toBe(true);

            //when
            step.inactive = true;
            scope.$digest();

            //then
            expect(chkboxOnOff.prop('checked')).toBe(false);
        }));

        it('should switch ON the On/Off switch when the 1st step is ACTIVE', inject(function (RecipeService) {
            //given
            stateMock.playground.dataset = metadata;
            var step = {
                inactive: true,
                transformation: {
                    stepId: '92771a304130e9',
                    name: 'propercase',
                    parameters: [],
                    items: [],
                    dynamic: false
                }
            };
            RecipeService.getRecipe().push(step);
            createElement();

            var chkboxOnOff = angular.element('body').find('.label-switch > input[type="checkbox"]');
            expect(chkboxOnOff.prop('checked')).toBe(false);

            //when
            step.inactive = false;
            scope.$digest();

            //then
            expect(chkboxOnOff.prop('checked')).toBe(true);
        }));

        it('should confirm preparation name edition on ENTER keydown', inject(function (PlaygroundService) {
            //given
            stateMock.playground.dataset = metadata;
            PlaygroundService.preparationName = 'PrepName';

            createElement();
            var ctrl = element.controller('playground');
            spyOn(ctrl, 'confirmPrepNameEdition').and.returnValue();

            var event = new angular.element.Event('keydown', {keyCode: 13});

            //when
            var input = angular.element('body > talend-modal .steps-header').find('input#prepNameInput');
            input.trigger(event);

            //then
            expect(ctrl.confirmPrepNameEdition).toHaveBeenCalled();
        }));

        it('should cancel preparation name edition on ENTER keydown', inject(function ($timeout, PlaygroundService) {
            //given
            stateMock.playground.dataset = metadata;
            PlaygroundService.preparationName = 'PrepName';

            createElement();
            var ctrl = element.controller('playground');
            spyOn(ctrl, 'cancelPrepNameEdition').and.returnValue();

            var event = new angular.element.Event('keydown', {keyCode: 27});

            //when
            var input = angular.element('body > talend-modal .steps-header').find('input#prepNameInput');
            input.trigger(event);
            $timeout.flush();

            //then
            expect(ctrl.cancelPrepNameEdition).toHaveBeenCalled();
        }));

        it('should do nothing special on keydown other than ENTER/ESC', inject(function ($timeout, PlaygroundService) {
            //given
            stateMock.playground.dataset = metadata;
            PlaygroundService.preparationName = 'PrepName';

            createElement();
            var ctrl = element.controller('playground');
            spyOn(ctrl, 'confirmPrepNameEdition').and.returnValue();
            spyOn(ctrl, 'cancelPrepNameEdition').and.returnValue();

            var event = new angular.element.Event('keydown', {keyCode: 19});

            //when
            var input = angular.element('body > talend-modal .steps-header').find('input#prepNameInput');
            input.trigger(event);

            //then
            expect(ctrl.confirmPrepNameEdition).not.toHaveBeenCalled();
            expect(ctrl.cancelPrepNameEdition).not.toHaveBeenCalled();
        }));
    });

    describe('hide playground', function () {
        beforeEach(inject(function (PlaygroundService, PreparationService) {
            stateMock.playground.dataset = metadata;
            createElement();

            scope.$apply();
            expect(PreparationService.refreshPreparations).not.toHaveBeenCalled();
        }));

        it('should change route to preparations list on preparation playground hide', inject(function ($state, $stateParams) {
            //given: simulate playground route with preparation id
            $stateParams.prepid = '1234';

            //when
            stateMock.playground.visible = false;
            scope.$apply();

            //then
            expect($state.go).toHaveBeenCalledWith('nav.home.preparations', {prepid: null});
        }));

        it('should change route to datasets list on dataset playground hide', inject(function ($state, $stateParams) {
            //given: simulate playground route with preparation id
            $stateParams.datasetid = '1234';

            //when
            stateMock.playground.visible = false;
            scope.$apply();

            //then
            expect($state.go).toHaveBeenCalledWith('nav.home.datasets', {datasetid: null});
        }));

        it('should do nothing if playground is not routed', inject(function ($state, $stateParams, PreparationService) {
            //given: simulate no preparation id in route
            $stateParams.prepid = null;
            $stateParams.datasetid = null;

            //when
            stateMock.playground.visible = false;
            scope.$apply();

            //then
            expect(PreparationService.refreshPreparations).toHaveBeenCalled();
            expect($state.go).not.toHaveBeenCalled();
        }));

        it('should refresh preparations on playground hide', inject(function (PreparationService) {
            //when
            stateMock.playground.visible = false;
            scope.$apply();

            //then
            expect(PreparationService.refreshPreparations).toHaveBeenCalled();
        }));
    });

});