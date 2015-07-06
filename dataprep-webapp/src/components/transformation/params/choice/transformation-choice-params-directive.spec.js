describe('Transformation choice params directive', function () {
    'use strict';
    var scope, createElement;

    beforeEach(module('data-prep.transformation-params'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function() {
            var element = angular.element('<transform-choice-params choices="choices"></transform-choice-params>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };
    }));

    it('should render an action with simple choice', function() {
        //given
        scope.choices = [{
            name: 'myChoice',
            label: 'my choice',
            values: [
                {
                    name: 'noParamChoice1'
                },
                {
                    name: 'noParamChoice2'
                }
            ]
        }];

        //when
        var element = createElement();

        //then
        expect(element.find('.param-name').length).toBe(1);
        expect(element.find('.param-name').eq(0).text().trim()).toBe('my choice:');
        expect(element.find('.param-input').length).toBe(1);
        expect(element.find('.param-input').eq(0).find('select').length).toBe(1);
        expect(element.find('.param-input').eq(0).find('option').length).toBe(2);
        expect(element.find('.param-input').eq(0).find('option').eq(0).text()).toBe('noParamChoice1');
        expect(element.find('.param-input').eq(0).find('option').eq(1).text()).toBe('noParamChoice2');
    });

    it('should render an action with choice containing parameters', function() {
        //given
        scope.choices = [{
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
                            label: 'Param 1',
                            type: 'string',
                            'inputType': 'text',
                            default: '.'
                        },
                        {
                            name: 'param2',
                            label: 'Param 2',
                            type: 'float',
                            'inputType': 'number',
                            default: '5'
                        }
                    ]
                }
            ]
        }];
        var element = createElement();

        //when
        scope.choices[0].selectedValue = scope.choices[0].values[0];
        scope.$digest();

        //then
        expect(element.find('.param-name').length).toBe(1); // choice name only

        //when
        scope.choices[0].selectedValue = scope.choices[0].values[1];
        scope.$digest();

        //then
        expect(element.find('.param-name').length).toBe(3); // choice name + 2 input params name
        expect(element.find('.param-name').eq(1).text().trim()).toBe('Param 1:');
        expect(element.find('.param-name').eq(2).text().trim()).toBe('Param 2:');

        expect(element.find('.param-input').length).toBe(3); // choice + 2 input params
        expect(element.find('.param-input').eq(1).find('input[type="text"]').length).toBe(1);
        expect(element.find('.param-input').eq(2).find('input[type="number"]').length).toBe(1);
    });
});
