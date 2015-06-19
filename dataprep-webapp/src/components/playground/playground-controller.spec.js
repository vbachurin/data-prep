describe('Playground controller', function() {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.playground'));

    beforeEach(inject(function($rootScope, $q, $controller, $state, PlaygroundService) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('PlaygroundCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn(PlaygroundService, 'createOrUpdatePreparation').and.returnValue($q.when(true));
        spyOn($state, 'go').and.returnValue();
    }));

    it('should bind showPlayground getter with PlaygroundService', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        expect(ctrl.showPlayground).toBe(false);

        //when
        PlaygroundService.show();

        //then
        expect(ctrl.showPlayground).toBe(true);
    }));

    it('should bind showPlayground setter with PlaygroundService', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        expect(PlaygroundService.visible).toBe(false);

        //when
        ctrl.showPlayground = true;

        //then
        expect(PlaygroundService.visible).toBe(true);
    }));

    it('should bind metadata getter with PlaygroundService', inject(function(PlaygroundService) {
        //given
        var metadata = {name: 'my dataset'};
        var ctrl = createController();
        expect(ctrl.metadata).toBeFalsy();

        //when
        PlaygroundService.currentMetadata = metadata;

        //then
        expect(ctrl.metadata).toBe(metadata);
    }));

    it('should bind preparationName getter with PlaygroundService', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        expect(ctrl.preparationName).toBeFalsy();

        //when
        PlaygroundService.preparationName = 'My preparation';

        //then
        expect(ctrl.preparationName).toBe('My preparation');
    }));

    it('should bind preparationName setter with PlaygroundService', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        expect(PlaygroundService.preparationName).toBeFalsy();

        //when
        ctrl.preparationName = 'My preparation';

        //then
        expect(PlaygroundService.preparationName).toBe('My preparation');
    }));

    it('should call service create/updateName service with clean name', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        ctrl.preparationName = 'My preparation ';

        //when
        ctrl.changeName();

        //then
        expect(PlaygroundService.createOrUpdatePreparation).toHaveBeenCalledWith('My preparation');
    }));

    it('should change route on create/update name', inject(function($rootScope, $state, PreparationService) {
        //given
        var ctrl = createController();
        ctrl.preparationName = 'My preparation ';
        PreparationService.currentPreparationId = 'fe6843da512545e';

        //when
        ctrl.changeName();
        $rootScope.$digest();

        //then
        expect($state.go).toHaveBeenCalledWith('nav.home.preparations', {prepid : 'fe6843da512545e'}, {location:'replace', inherit:false});
    }));

    it('should not call service create/updateName service if name is blank', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        ctrl.preparationName = ' ';

        //when
        ctrl.changeName();

        //then
        expect(PlaygroundService.createOrUpdatePreparation).not.toHaveBeenCalled();
    }));

    it('should bind previewInProgress getter with PreviewService', inject(function(PreviewService) {
        //given
        var ctrl = createController();
        expect(ctrl.previewInProgress).toBeFalsy();

        //when
        spyOn(PreviewService, 'previewInProgress').and.returnValue(true);

        //then
        expect(ctrl.previewInProgress).toBe(true);
    }));

    it('should bind showRecipe getter to PlaygroundService', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        expect(ctrl.showRecipe).toBeFalsy();

        //when
        PlaygroundService.showRecipe = true;

        //then
        expect(ctrl.showRecipe).toBe(true);
    }));

    it('should bind showRecipe setter to PlaygroundService', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        expect(PlaygroundService.showRecipe).toBeFalsy();

        //when
        ctrl.showRecipe = true;

        //then
        expect(PlaygroundService.showRecipe).toBe(true);
    }));
});
