/*jshint camelcase: false */

describe('Transform params controller', function () {
    'use strict';

    var createController, scope, extractedParams;

    beforeEach(module('data-prep.transformation-params'));

    beforeEach(inject(function ($rootScope, $controller) {
        extractedParams = null;
        scope = $rootScope.$new();

        createController = function (transformation) {
            var ctrlFn = $controller('TransformParamsCtrl', {
                $scope: scope
            }, true);
            ctrlFn.instance.transformation = transformation;
            ctrlFn.instance.onSubmit = function(args) {
                extractedParams = args.params;
            };
            return ctrlFn();
        };
    }));

    it('should set numeric default value', function () {
        //given
        var transformation = {parameters: [{name: 'param1', type: 'numeric', default: '5'}]};
        var ctrl = createController(transformation);

        //when
        ctrl.transformWithParam();

        //then
        expect(ctrl.transformation.parameters[0].value).toBe(5);
    });

    it('should set integer default value', function () {
        //given
        var transformation = {parameters: [{name: 'param1', type: 'integer', default: '5'}]};
        var ctrl = createController(transformation);

        //when
        ctrl.transformWithParam();

        //then
        expect(ctrl.transformation.parameters[0].value).toBe(5);
    });

    it('should set double default value', function () {
        //given
        var transformation = {parameters: [{name: 'param1', type: 'double', default: '5.1'}]};
        var ctrl = createController(transformation);

        //when
        ctrl.transformWithParam();

        //then
        expect(ctrl.transformation.parameters[0].value).toBe(5.1);
    });

    it('should set float default value', function () {
        //given
        var transformation = {parameters: [{name: 'param1', type: 'float', default: '5.1'}]};
        var ctrl = createController(transformation);

        //when
        ctrl.transformWithParam();

        //then
        expect(ctrl.transformation.parameters[0].value).toBe(5.1);
    });

    it('should set 0 value if default value is not numeric with numeric type', function () {
        //given
        var transformation = {parameters: [{name: 'param1', type: 'numeric', default: 'a'}]};
        var ctrl = createController(transformation);

        //when
        ctrl.transformWithParam();

        //then
        expect(ctrl.transformation.parameters[0].value).toBe(0);
    });

    it('should not set default value if no default value is provided', function () {
        //given
        var transformation = {parameters: [{name: 'param1', type: 'text', default: null}]};
        var ctrl = createController(transformation);

        //when
        ctrl.transformWithParam();

        //then
        expect(ctrl.transformation.parameters[0].value).toBeUndefined();
    });

    it('should init params default values', function() {
        //given
        var transformation = {
            name: 'uppercase',
            category: 'case',
            parameters: [
                {name: 'param1', type: 'text', default: 'param1Value'},
                {name: 'param2', type: 'integer', default: '5'}
            ]
        };
        var ctrl = createController(transformation);

        //when
        ctrl.transformWithParam();

        //then
        expect(extractedParams).toEqual({ param1: 'param1Value', param2: 5 });
    });

    it('should extract param', function() {
        //given
        var transformation = {
            name: 'uppercase',
            category: 'case',
            parameters: [
                {name: 'param1', type: 'text', default: ''},
                {name: 'param2', type: 'integer', default: '5'}
            ]
        };
        var ctrl = createController(transformation);
        ctrl.transformation.parameters[0].value = 'param1Value';
        ctrl.transformation.parameters[1].value = 4;

        //when
        ctrl.transformWithParam();

        //then
        expect(extractedParams).toEqual({ param1: 'param1Value', param2: 4 });
    });

    it('should init choice default value', function() {
        //given
        var transformation = {
            name: 'split',
            category: 'split',
            items: [{
                name: 'mode',
                values: [
                    {name: 'regex'},
                    {name: 'index', default: true}
                ]
            }]
        };
        var ctrl = createController(transformation);

        //when
        ctrl.transformWithParam();

        //then
        expect(extractedParams).toEqual({ mode: 'index'});
    });

    it('should extract simple choice param', function() {
        //given
        var transformation = {
            name: 'split',
            category: 'split',
            items: [{
                name: 'mode',
                values: [
                    {name: 'regex'},
                    {name: 'index'}
                ]
            }]
        };
        var ctrl = createController(transformation);
        ctrl.transformation.items[0].selectedValue = ctrl.transformation.items[0].values[1];

        //when
        ctrl.transformWithParam();

        //then
        expect(extractedParams).toEqual({ mode: 'index'});
    });

    it('should extract parameterized choice params', function() {
        //given
        var transformation = {
            name: 'split',
            category: 'split',
            items: [{
                name: 'mode',
                values: [
                    {
                        name: 'regex',
                        parameters : [
                            {name: 'regex', type: 'text', default: '', value: 'param1Value'},
                            {name: 'comment', type: 'text', default: '', value: 'my comment'}
                        ]
                    },
                    {name: 'index'}
                ]
            }]
        };
        var ctrl = createController(transformation);
        ctrl.transformation.items[0].selectedValue = ctrl.transformation.items[0].values[0];

        //when
        ctrl.transformWithParam();

        //then
        expect(extractedParams).toEqual({ mode: 'regex', regex: 'param1Value', comment: 'my comment'});
    });

    it('should extract parameters and parameterized choice transformation select', function() {
        //given
        var transformation = {
            name: 'split',
            category: 'split',
            parameters: [
                {name: 'param1', type: 'text', default: ''},
                {name: 'param2', type: 'integer', default: '5'}
            ],
            items: [{
                name: 'mode',
                values: [
                    {
                        name: 'regex',
                        parameters : [
                            {name: 'regex', type: 'text', default: '', value: 'param1Value'},
                            {name: 'comment', type: 'text', default: '', value: 'my comment'}
                        ]
                    },
                    {name: 'index'}
                ]
            }]
        };
        var ctrl = createController(transformation);
        ctrl.transformation.items[0].selectedValue = ctrl.transformation.items[0].values[0];
        ctrl.transformation.parameters[0].value = 'param1Value';
        ctrl.transformation.parameters[1].value = 4;

        //when
        ctrl.transformWithParam();

        //then
        expect(extractedParams).toEqual({ mode: 'regex', regex: 'param1Value', comment: 'my comment', param1: 'param1Value', param2: 4});
    });

    it('should extract param and call ctrl onSubmitHoverOn function', function() {
        //given
        var transformation = {
            name: 'uppercase',
            category: 'case',
            parameters: [
                {name: 'param1', type: 'text', default: ''},
                {name: 'param2', type: 'integer', default: '5'}
            ]
        };
        var ctrl = createController(transformation);
        ctrl.transformation.parameters[0].value = 'param1Value';
        ctrl.transformation.parameters[1].value = 4;

        var hoverArgs = {};
        ctrl.onSubmitHoverOn = function(args) {
            hoverArgs = args;
        };

        //when
        ctrl.submitHoverOn();

        //then
        expect(hoverArgs.params).toEqual({ param1: 'param1Value', param2: 4 });
    });

    it('should extract param and call ctrl onSubmitHoverOff function', function() {
        //given
        var transformation = {
            name: 'uppercase',
            category: 'case',
            parameters: [
                {name: 'param1', type: 'text', default: ''},
                {name: 'param2', type: 'integer', default: '5'}
            ]
        };
        var ctrl = createController(transformation);
        ctrl.transformation.parameters[0].value = 'param1Value';
        ctrl.transformation.parameters[1].value = 4;

        var hoverArgs = {};
        ctrl.onSubmitHoverOff = function(args) {
            hoverArgs = args;
        };

        //when
        ctrl.submitHoverOff();

        //then
        expect(hoverArgs.params).toEqual({ param1: 'param1Value', param2: 4 });
    });
});