describe('Datagrid tooltip controller', function() {
    'use strict';

    var createController, scope;

    beforeEach(module('data-prep.datagrid-tooltip'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();
        createController = function() {
            var ctrl = $controller('DatagridTooltipCtrl', {
                $scope: scope
            });
            return ctrl;
        };
    }));

    it('should init edit mode to false', function() {
        //when
        var ctrl = createController();

        //then
        expect(ctrl.editMode).toBe(false);
    });

    it('should reinit editMode when tooltip change (based on position change)', function() {
        //given
        var ctrl = createController();
        ctrl.editMode = true;

        //when
        ctrl.position = {x: 100, y: 200};
        scope.$digest();

        //then
        expect(ctrl.editMode).toBe(false);
    });

    it('should enable edit mode and init input value', function() {
        //given
        var ctrl = createController();
        ctrl.editMode = false;

        ctrl.record = {name: 'toto'};
        ctrl.key = 'name';

        expect(ctrl.inputValue).not.toBe('toto');

        //when
        ctrl.edit();

        //then
        expect(ctrl.inputValue).toBe('toto');
    });

    it('should enable edit mode but not init input value when the cell to edit is the same as previous one', function() {
        //given
        var ctrl = createController();
        ctrl.editMode = false;

        ctrl.record = {name: 'toto'};
        ctrl.key = 'name';
        ctrl.edit();

        ctrl.inputValue = 'tata';
        ctrl.editMode = false;

        //when
        ctrl.edit();

        //then
        expect(ctrl.inputValue).toBe('tata');
    });
});