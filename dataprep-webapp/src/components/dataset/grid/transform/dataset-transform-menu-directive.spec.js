describe('Transformation menu directive', function () {
    'use strict';
    var scope, createElement;

    beforeEach(module('data-prep-dataset'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function(directiveScope) {
            var element = angular.element('<dataset-transform-menu column="column" metadata="metadata" item="item"></dataset-transform-menu>');
            $compile(element)(directiveScope);
            directiveScope.$digest();
            return element;
        };
    }));

    it('should render divider', function() {
        //given
        var body = angular.element('body');
        scope.item = {isDivider: true};

        //when
        var element = createElement(scope);

        //then
        expect(element.hasClass('divider')).toBe(true);
        expect(body.find('talend-modal').length).toBe(0);

    });

    it('should render a simple action', function() {
        //given
        var body = angular.element('body');
        scope.item = {name: 'uppercase'};

        //when
        var element = createElement(scope);

        //then
        expect(element.hasClass('divider')).toBe(false);
        expect(element.find('a').text().trim()).toBe('uppercase');
        expect(body.find('talend-modal').length).toBe(0);
    });

    it('should render an action with parameters', function() {
        //given
        scope.item = {
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
        var element = createElement(scope);

        //then
        expect(element.hasClass('divider')).toBe(false);
        expect(element.find('a').text().trim()).toBe('menu with param');
        expect(element.find('talend-modal').length).toBe(1);

        var modal = element.find('talend-modal').eq(0);
        expect(modal.find('.param-name').length).toBe(2);
        expect(modal.find('.param-name').eq(0).text().trim()).toBe('param1 :');
        expect(modal.find('.param-name').eq(1).text().trim()).toBe('param2 :');
        expect(modal.find('.param-input').length).toBe(2);
        expect(modal.find('.param-input').eq(0).find('input[type="text"]').length).toBe(1);
        expect(modal.find('.param-input').eq(1).find('input[type="number"]').length).toBe(1);
    });

    it('should render an action with simple choice', function() {
        //given
        scope.item = {
            name: 'menu with param',
            choice: {
                name: 'my choice',
                values: [
                    {
                        name: 'noParamChoice1'
                    },
                    {
                        name: 'noParamChoice2'
                    }
                ]
            }
        };

        //when
        var element = createElement(scope);

        //then
        expect(element.hasClass('divider')).toBe(false);
        expect(element.find('a').text().trim()).toBe('menu with param');
        expect(element.find('talend-modal').length).toBe(1);

        var modal = element.find('talend-modal').eq(0);
        var paramChoice = modal.find('.param-choice').eq(0);
        expect(paramChoice.find('.param-choice-name').length).toBe(1);
        expect(paramChoice.find('.param-choice-name').eq(0).text().trim()).toBe('Option :');
        expect(paramChoice.find('.param-choice-select').length).toBe(1);
        expect(paramChoice.find('.param-choice-select').eq(0).find('select').length).toBe(1);
        expect(paramChoice.find('.param-choice-select').eq(0).find('option').length).toBe(3);
        expect(paramChoice.find('.param-choice-select').eq(0).find('option').eq(1).text()).toBe('noParamChoice1');
        expect(paramChoice.find('.param-choice-select').eq(0).find('option').eq(2).text()).toBe('noParamChoice2');
    });

    it('should render an action with choice containing parameters', function() {
        //given
        scope.item = {
            name: 'menu with param',
            choice: {
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
            }
        };
        var element = createElement(scope);
        var modal = element.find('talend-modal').eq(0);
        var paramChoice = modal.find('.param-choice').eq(0);

        //when
        scope.item.choice.selectedValue = scope.item.choice.values[0];
        scope.$digest();

        //then
        expect(paramChoice.find('.param-name').length).toBe(0);

        //when
        scope.item.choice.selectedValue = scope.item.choice.values[1];
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