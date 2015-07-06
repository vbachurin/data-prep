describe('Transformation simple params directive', function () {
    'use strict';
    var scope, createElement;

    beforeEach(module('data-prep.transformation-params'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function() {
            var element = angular.element('<transform-simple-params parameters="parameters"></transform-simple-params>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };
    }));

    it('should render an action with parameters', function() {
        //given
        scope.parameters = [
            {
                'name': 'param1',
                'label': 'Param 1',
                'type': 'string',
                'inputType': 'text',
                'default': '.'
            },
            {
                'name': 'param2',
                'label': 'Param 2',
                'type': 'integer',
                'inputType': 'number',
                'default': '5'
            }
        ];

        //when
        var element = createElement();

        //then
        expect(element.find('.param-name').length).toBe(2);
        expect(element.find('.param-name').eq(0).text().trim()).toBe('Param 1:');
        expect(element.find('.param-name').eq(1).text().trim()).toBe('Param 2:');
        expect(element.find('.param-input').length).toBe(2);
        expect(element.find('.param-input').eq(0).find('input[type="text"]').length).toBe(1);
        expect(element.find('.param-input').eq(1).find('input[type="number"]').length).toBe(1);
    });
});
