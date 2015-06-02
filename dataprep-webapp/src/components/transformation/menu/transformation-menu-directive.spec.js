describe('Transformation menu directive', function () {
    'use strict';
    var scope, createElement, element;

    beforeEach(module('data-prep.transformation-menu'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function() {
            element = angular.element('<transform-menu column="column" menu="menu"></transform-menu>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };
    }));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should render divider', function() {
        //given
        var body = angular.element('body');
        scope.menu = {isDivider: true};

        //when
        var element = createElement();

        //then
        expect(element.hasClass('divider')).toBe(true);
        expect(body.find('talend-modal').length).toBe(0);

    });

    it('should render a simple action', function() {
        //given
        var body = angular.element('body');
        scope.menu = {label: 'uppercase'};

        //when
        var element = createElement();

        //then
        expect(element.hasClass('divider')).toBe(false);
        expect(element.find('a').text().trim()).toBe('uppercase');
        expect(body.find('talend-modal').length).toBe(0);
    });

    it('should render an action with parameters', function() {
        //given
        scope.menu = {
            name: 'menuWithParam',
            label: 'menu with param',
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
        expect(element.hasClass('divider')).toBe(false);
        expect(element.find('a').text().trim()).toBe('menu with param');
        expect(angular.element('body').find('.transformation-params').length).toBe(1);
    });

    it('should render an action with simple choice', function() {
        //given
        scope.menu = {
            name: 'menuWithParam',
            label: 'menu with param',
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
        expect(element.hasClass('divider')).toBe(false);
        expect(element.find('a').text().trim()).toBe('menu with param');
        expect(angular.element('body').find('.transformation-params').length).toBe(1);
    });
});
