/*jshint camelcase: false */

describe('Transform params controller', function () {
    'use strict';

    var createController, scope, extractedParams, transformation;

    beforeEach(module('data-prep.transformation-params'));

    beforeEach(inject(function ($rootScope, $controller) {
        extractedParams = null;
        scope = $rootScope.$new();

        createController = function () {
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

    it('should extract param', function() {
        //given
        transformation = {
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

    it('should extract simple choice param', function() {
        //given
        transformation = {
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
        var ctrl = createController();
        ctrl.transformation.items[0].selectedValue = ctrl.transformation.items[0].values[1];

        //when
        ctrl.transformWithParam();

        //then
        expect(extractedParams).toEqual({ mode: 'index'});
    });

    it('should extract parameterized choice params', function() {
        //given
        transformation = {
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
        var ctrl = createController();
        ctrl.transformation.items[0].selectedValue = ctrl.transformation.items[0].values[0];

        //when
        ctrl.transformWithParam();

        //then
        expect(extractedParams).toEqual({ mode: 'regex', regex: 'param1Value', comment: 'my comment'});
    });

    it('should extract parameters and parameterized choice transformation select', function() {
        //given
        transformation = {
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
        var ctrl = createController();
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
        transformation = {
            name: 'uppercase',
            category: 'case',
            parameters: [
                {name: 'param1', type: 'text', default: ''},
                {name: 'param2', type: 'integer', default: '5'}
            ]
        };
        var ctrl = createController();
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
        transformation = {
            name: 'uppercase',
            category: 'case',
            parameters: [
                {name: 'param1', type: 'text', default: ''},
                {name: 'param2', type: 'integer', default: '5'}
            ]
        };
        var ctrl = createController();
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