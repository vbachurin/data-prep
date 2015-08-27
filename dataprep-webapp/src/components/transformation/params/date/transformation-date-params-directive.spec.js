describe('Transformation date params directive', function () {
    'use strict';
    var scope, createElement;

    beforeEach(module('data-prep.transformation-params'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function() {
            var element = angular.element('<transform-date-params parameters="parameters"></transform-date-params>');
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
                'type': 'date',
                'default': '02/01/2012 10:00:12'
            }
        ];

        //when
        var element = createElement();

        //then
        expect(element.find('.param-name').length).toBe(1);
        expect(element.find('.param-name').eq(0).text().trim()).toBe('Param 1:');
        expect(element.find('.datetimepicker').length).toBe(1);
        expect(element.find('.datetimepicker').eq(0).val()).toBe('02/01/2012 10:00:12');
    });
});
