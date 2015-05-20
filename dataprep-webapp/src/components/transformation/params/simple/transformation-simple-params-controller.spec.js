describe('Transform simple params controller', function () {
    'use strict';

    var createController, scope, parameters;

    beforeEach(module('data-prep.transformation-params'));

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrlFn = $controller('TransformSimpleParamsCtrl', {
                $scope: scope
            }, true);
            ctrlFn.instance.parameters = parameters;
            return ctrlFn();
        };
    }));

    it('should set numeric default value', function () {
        //given
        parameters = [{name: 'param1', type: 'numeric', default: '5'}];

        //when
        var ctrl = createController();

        //then
        expect(ctrl.parameters[0].value).toBe(5);
    });

    it('should set integer default value', function () {
        //given
        parameters = [{name: 'param1', type: 'integer', default: '5'}];

        //when
        var ctrl = createController();

        //then
        expect(ctrl.parameters[0].value).toBe(5);
    });

    it('should set double default value', function () {
        //given
        parameters = [{name: 'param1', type: 'double', default: '5.1'}];

        //when
        var ctrl = createController();

        //then
        expect(ctrl.parameters[0].value).toBe(5.1);
    });

    it('should set float default value', function () {
        //given
        parameters = [{name: 'param1', type: 'float', default: '5.1'}];

        //when
        var ctrl = createController();

        //then
        expect(ctrl.parameters[0].value).toBe(5.1);
    });

    it('should set 0 value if default value is not numeric with numeric type', function () {
        //given
        parameters = [{name: 'param1', type: 'numeric', default: 'a'}];

        //when
        var ctrl = createController();

        //then
        expect(ctrl.parameters[0].value).toBe(0);
    });

    it('should not set default value if no default value is provided', function () {
        //given
        parameters = [{name: 'param1', type: 'text', default: null}];

        //when
        var ctrl = createController();

        //then
        expect(ctrl.parameters[0].value).toBeUndefined();
    });

    it('should init params default values', function() {
        //given
        parameters = [
            {name: 'param1', type: 'text', default: 'param1Value'},
            {name: 'param2', type: 'integer', default: '5'}
        ];

        //when
        var ctrl = createController();

        //then
        expect(ctrl.parameters[0].value).toBe('param1Value');
        expect(ctrl.parameters[1].value).toBe(5);
    });
});