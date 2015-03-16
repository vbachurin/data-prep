describe('Transformation menu directive', function () {
    'use strict';
    var scope, createElement, extractedParams;

    beforeEach(module('data-prep.transformation-params'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        extractedParams = null;
        scope = $rootScope.$new();
        scope.onSubmit = function(args) {
            extractedParams = args.params;
        };

        createElement = function() {
            var element = angular.element('<transform-params transformation="transformation" on-submit="onSubmit"></transform-params>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };
    }));

    it('should render an action with parameters', function() {
        //given
        scope.transformation = {
            name: 'menu with param',
            parameters: [
                {
                    'name': 'param1',
                    'type': 'string',
                    'inputType': 'text',
                    'default': '.'
                },
                {
                    'name': 'param2',
                    'type': 'integer',
                    'inputType': 'number',
                    'default': '5'
                }
            ]
        };

        //when
        var element = createElement();

        //then
        expect(element.find('.param-name').length).toBe(2);
        expect(element.find('.param-name').eq(0).text().trim()).toBe('param1 :');
        expect(element.find('.param-name').eq(1).text().trim()).toBe('param2 :');
        expect(element.find('.param-input').length).toBe(2);
        expect(element.find('.param-input').eq(0).find('input[type="text"]').length).toBe(1);
        expect(element.find('.param-input').eq(1).find('input[type="number"]').length).toBe(1);
    });

    it('should render an action with simple choice', function() {
        //given
        scope.transformation = {
            name: 'menu with param',
            items: [{
                name: 'my choice',
                values: [
                    {
                        name: 'noParamChoice1'
                    },
                    {
                        name: 'noParamChoice2'
                    }
                ]
            }]
        };

        //when
        var element = createElement();

        //then
        var paramChoice = element.find('.param-choice').eq(0);
        expect(paramChoice.find('.param-choice-name').length).toBe(1);
        expect(paramChoice.find('.param-choice-name').eq(0).text().trim()).toBe('my choice :');
        expect(paramChoice.find('.param-choice-select').length).toBe(1);
        expect(paramChoice.find('.param-choice-select').eq(0).find('select').length).toBe(1);
        expect(paramChoice.find('.param-choice-select').eq(0).find('option').length).toBe(3);
        expect(paramChoice.find('.param-choice-select').eq(0).find('option').eq(1).text()).toBe('noParamChoice1');
        expect(paramChoice.find('.param-choice-select').eq(0).find('option').eq(2).text()).toBe('noParamChoice2');
    });

    it('should render an action with choice containing parameters', function() {
        //given
        scope.transformation = {
            name: 'menu with param',
            items: [{
                name: 'my choice',
                values: [
                    {
                        name: 'noParamChoice'
                    },
                    {
                        name: 'twoParams',
                        parameters: [
                            {
                                name: 'param1',
                                type: 'string',
                                'inputType': 'text',
                                default: '.'
                            },
                            {
                                name: 'param2',
                                type: 'float',
                                'inputType': 'number',
                                default: '5'
                            }
                        ]
                    }
                ]
            }]
        };
        var element = createElement();
        var paramChoice = element.find('.param-choice').eq(0);

        //when
        scope.transformation.items[0].selectedValue = scope.transformation.items[0].values[0];
        scope.$digest();

        //then
        expect(paramChoice.find('.param-name').length).toBe(0);

        //when
        scope.transformation.items[0].selectedValue = scope.transformation.items[0].values[1];
        scope.$digest();

        //then
        expect(paramChoice.find('.param-name').length).toBe(2);
        expect(paramChoice.find('.param-name').eq(0).text().trim()).toBe('param1 :');
        expect(paramChoice.find('.param-name').eq(1).text().trim()).toBe('param2 :');

        expect(paramChoice.find('.param-input').length).toBe(2);
        expect(paramChoice.find('.param-input').eq(0).find('input[type="text"]').length).toBe(1);
        expect(paramChoice.find('.param-input').eq(1).find('input[type="number"]').length).toBe(1);
    });
});