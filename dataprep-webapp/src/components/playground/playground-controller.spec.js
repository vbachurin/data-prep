describe('Playground controller', function() {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.playground'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('PlaygroundCtrl', {
                $scope: scope
            });
            return ctrl;
        };
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

});
