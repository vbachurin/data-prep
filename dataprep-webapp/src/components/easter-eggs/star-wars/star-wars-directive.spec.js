describe('StarWars directive', function() {
    'use strict';

    var scope, createElement, element;

    beforeEach(module('data-prep.easter-eggs'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function () {
            element = angular.element('<star-wars></star-wars>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };
    }));

    it('should render star wars div', function() {
        //when
        createElement();

        //then
        expect(element.find('.title').text()).toBe('Talend Data Preparation');
    });

});