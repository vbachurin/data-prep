describe('Playground controller', function () {
    'use strict';

    var createController, scope, stateMock;

    beforeEach(module('data-prep.playground', function ($provide) {
        stateMock = {playground: {
            dataset: {},
            lookup: {
                actions: []
            }
        }};
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(function ($rootScope, $q, $controller, $state, PlaygroundService) {
        scope = $rootScope.$new();

        createController = function () {
            return $controller('PlaygroundCtrl', {
                $scope: scope
            });
        };

        spyOn(PlaygroundService, 'createOrUpdatePreparation').and.returnValue($q.when(true));
        spyOn($state, 'go').and.returnValue();

    }));

    describe('bindings', function () {
        it('should bind preparationName getter with PlaygroundService', inject(function (PlaygroundService) {
            //given
            var ctrl = createController();
            expect(ctrl.preparationName).toBeFalsy();

            //when
            PlaygroundService.preparationName = 'My preparation';

            //then
            expect(ctrl.preparationName).toBe('My preparation');
        }));

        it('should bind preparationName setter with PlaygroundService', inject(function (PlaygroundService) {
            //given
            var ctrl = createController();
            expect(PlaygroundService.preparationName).toBeFalsy();

            //when
            ctrl.preparationName = 'My preparation';

            //then
            expect(PlaygroundService.preparationName).toBe('My preparation');
        }));

        it('should bind previewInProgress getter with PreviewService', inject(function (PreviewService) {
            //given
            var ctrl = createController();
            expect(ctrl.previewInProgress).toBeFalsy();

            //when
            spyOn(PreviewService, 'previewInProgress').and.returnValue(true);

            //then
            expect(ctrl.previewInProgress).toBe(true);
        }));
    });

    describe('recipe header', function () {

        it('should create/update preparation with clean name on name edition confirmation', inject(function (PlaygroundService) {
            //given
            var ctrl = createController();
            ctrl.preparationName = 'my old name';

            //when
            ctrl.confirmPrepNameEdition('  my new name  ');

            //then
            expect(PlaygroundService.createOrUpdatePreparation).toHaveBeenCalledWith('my new name');
        }));

        it('should change route to preparation route on name edition confirmation', inject(function ($rootScope, $state) {
            //given
            var ctrl = createController();
            ctrl.preparationName = 'My old preparation ';
            stateMock.playground.preparation = {id: 'fe6843da512545e'};

            //when
            ctrl.confirmPrepNameEdition('My preparation ');
            $rootScope.$digest();

            //then
            expect($state.go).toHaveBeenCalledWith('nav.home.preparations', {prepid: 'fe6843da512545e'}, {
                location: 'replace',
                inherit: false
            });
        }));

        it('should not call service create/updateName service if name is blank on name edition confirmation', inject(function (PlaygroundService) {
            //given
            var ctrl = createController();

            //when
            ctrl.confirmPrepNameEdition(' ');

            //then
            expect(PlaygroundService.createOrUpdatePreparation).not.toHaveBeenCalled();
        }));
    });

    describe('implicit preparation', function () {
        var ctrl;
        var preparation;

        beforeEach(inject(function ($q, PreparationService, StateService) {
            preparation = {id: '9af874865e42b546', draft: true};
            stateMock.playground.preparation = preparation;

            spyOn(PreparationService, 'delete').and.returnValue($q.when(true));
            spyOn(StateService, 'hidePlayground').and.returnValue();

            ctrl = createController();
        }));

        it('should return true (allow playground close) with NOT implicit preparation', function () {
            //given
            preparation.draft = false;

            //when
            var result = ctrl.beforeClose();

            //then
            expect(result).toBe(true);
        });

        it('should return false (block playground close) with implicit preparation', function () {
            //when
            var result = ctrl.beforeClose();

            //then
            expect(result).toBe(false);
        });

        it('should show save/discard modal with implicit preparation', function () {
            //given
            expect(ctrl.showNameValidation).toBeFalsy();

            //when
            ctrl.beforeClose();

            //then
            expect(ctrl.showNameValidation).toBe(true);
        });

        it('should delete current preparation on save discard', inject(function (PreparationService) {
            //when
            ctrl.discardSaveOnClose();

            //then
            expect(PreparationService.delete).toHaveBeenCalledWith(preparation);
        }));

        it('should hide save/discard and playground modals on save discard', inject(function (StateService) {
            //given
            ctrl.showNameValidation = true;
            expect(StateService.hidePlayground).not.toHaveBeenCalled();

            //when
            ctrl.discardSaveOnClose();
            scope.$digest();

            //then
            expect(ctrl.showNameValidation).toBe(false);
            expect(StateService.hidePlayground).toHaveBeenCalled();
        }));

        it('should change preparation name on save confirm', inject(function (PlaygroundService) {
            //given
            ctrl.preparationName = '  my preparation ';

            //when
            ctrl.confirmSaveOnClose();

            //then
            expect(PlaygroundService.createOrUpdatePreparation).toHaveBeenCalledWith('my preparation');
        }));

        it('should manage saving flag on save confirm', function () {
            //given
            expect(ctrl.saveInProgress).toBeFalsy();

            //when
            ctrl.confirmSaveOnClose();
            expect(ctrl.saveInProgress).toBe(true);
            scope.$digest();

            //then
            expect(ctrl.saveInProgress).toBe(false);
        });

        it('should hide save/discard and playground modals on save confirm', inject(function (StateService) {
            //given
            ctrl.showNameValidation = true;
            expect(StateService.hidePlayground).not.toHaveBeenCalled();

            //when
            ctrl.confirmSaveOnClose();
            scope.$digest();

            //then
            expect(ctrl.showNameValidation).toBe(false);
            expect(StateService.hidePlayground).toHaveBeenCalled();
        }));
    });

    describe('lookup', function () {
        var ctrl;

        beforeEach(inject(function ($q, LookupService, StateService) {
            spyOn(StateService, 'setLookupVisibility').and.returnValue();
            spyOn(LookupService, 'loadContent').and.returnValue();
            spyOn(StateService, 'setLookupDataset').and.returnValue();

            ctrl = createController();
        }));

        it('should toggle lookup visibility', inject(function (StateService) {
            //given
            stateMock.playground.actionsVisibility = false;

            //when
            ctrl.toggleLookup();

            //then
            expect(StateService.setLookupVisibility).toHaveBeenCalledWith(true);
        }));

        it('should fetch lookup datasets when it is not already initialized', inject(function ($q, LookupService) {
            //given
            stateMock.playground.dataset = {id: 'ds54sd-ds5d4s-4dssd8'};
            stateMock.playground.lookup.actions = [];
            spyOn(LookupService, 'getActions').and.returnValue($q.when(true));

            //when
            ctrl.toggleLookup();

            //then
            expect(LookupService.getActions).toHaveBeenCalledWith(stateMock.playground.dataset.id);
        }));

        it('should NOT fetch lookup datasets when it is already initialized', inject(function ($q, LookupService) {
            //given
            stateMock.playground.dataset = {id: 'ds54sd-ds5d4s-4dssd8'};
            stateMock.playground.lookup.actions = [{}];
            spyOn(LookupService, 'getActions').and.returnValue($q.when(true));

            //when
            ctrl.toggleLookup();

            //then
            expect(LookupService.getActions).not.toHaveBeenCalled();
        }));

        it('should load lookup dataset content when there are potential lookup datasets', inject(function ($q, LookupService) {
            //given
            var lookupDataset = {
                name: 'lookup',
                parameters: []
            };
            var lookupDataset2 = {
                name: 'lookup2',
                parameters: []
            };
            stateMock.playground.dataset = {id: 'ds54sd-ds5d4s-4dssd8'};
            stateMock.playground.lookup.actions = [];
            spyOn(LookupService, 'getActions').and.returnValue($q.when([lookupDataset, lookupDataset2]));

            //when
            ctrl.toggleLookup();
            scope.$digest();

            //then
            expect(LookupService.loadContent).toHaveBeenCalledWith(lookupDataset);
        }));

        it('should NOT load lookup dataset content when the potential lookup datasets are empty', inject(function ($q, LookupService) {
            //given
            stateMock.playground.dataset = {id: 'ds54sd-ds5d4s-4dssd8'};
            stateMock.playground.lookup.actions = [];
            spyOn(LookupService, 'getActions').and.returnValue($q.when(true));

            //when
            ctrl.toggleLookup();
            scope.$digest();

            //then
            expect(LookupService.loadContent).not.toHaveBeenCalled();
        }));
    });

    describe('feedback ', function() {
        beforeEach(inject(function (StateService) {
            spyOn(StateService, 'enableFeedback').and.returnValue();
        }));

        it('should open feedback modal', inject(function (StateService) {
            //given
            var ctrl = createController();

            //when
            ctrl.openFeedbackForm();

            //then
            expect(StateService.enableFeedback).toHaveBeenCalled();
        }));

    });
});
