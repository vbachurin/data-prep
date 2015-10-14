describe('Navbar directive', function() {
    'use strict';

    var scope, createElement, element;

    beforeEach(module('data-prep.navbar'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function () {
            element = angular.element('<navbar></navbar>');
            $compile(element)(scope);
            scope.$digest();
            return element;
        };
    }));

    it('should render navigation bar', function() {
        //when
        createElement();

        //then
        expect(element.find('talend-navbar').length).toBe(1);
    });

    it('should render footer bar', function() {
        //when
        createElement();

        //then
        expect(element.find('footer').length).toBe(1);
    });

    it('should render content insertion point', function() {
        //when
        createElement();

        //then
        expect(element.find('ui-view.content').length).toBe(1);
    });
});