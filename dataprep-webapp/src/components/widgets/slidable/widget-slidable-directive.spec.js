describe('Slidable directive', function () {
    'use strict';

    var scope, createElement;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    afterEach(function () {
        scope.$destroy();
        scope.$digest();
    });

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function (side) {
            var template = '<talend-slidable visible="visible" side="' + side + '">' +
                '   Content'+
                '</talend-slidable>';
            var element = $compile(template)(scope);
            scope.$digest();
            return element;
        };
    }));


    it('should hide slidable on creation', function () {
        //given
        scope.visible = false;

        //when
        var element = createElement();

        //then
        expect(element.hasClass('slide-hide')).toBe(true);
    });

    it('should show slidable on creation', function () {
        //given
        scope.visible = true;

        //when
        var element = createElement();

        //then
        expect(element.hasClass('slide-hide')).toBe(false);
    });

    it('should hide slidable on action click', function () {
        //given
        scope.visible = true;
        var element = createElement();
        expect(element.hasClass('slide-hide')).toBe(false);

        //when
        element.find('.action').eq(0).click();

        //then
        expect(element.hasClass('slide-hide')).toBe(true);
    });

    it('should show slidable on action click', function () {
        //given
        scope.visible = false;
        var element = createElement();
        expect(element.hasClass('slide-hide')).toBe(true);

        //when
        element.find('.action').eq(0).click();

        //then
        expect(element.hasClass('slide-hide')).toBe(false);
    });

    it('should show '>' when left slidable is hidden', function () {
        //given
        scope.visible = false;

        //when
        var element = createElement();

        //then
        var actionOnlySpan = element.find('.action').eq(0).find('span').eq(0);
        var displayedActionText = actionOnlySpan.find('span').not('.ng-hide').eq(0).text();
        var hiddenActionText = actionOnlySpan.find('span.ng-hide').eq(0).text();
        expect(displayedActionText).toBe('>');
        expect(hiddenActionText).toBe('<');
    });

    it('should show '<' when left slidable is displayed', function () {
        //given
        scope.visible = true;

        //when
        var element = createElement();

        //then
        var actionOnlySpan = element.find('.action').eq(0).find('span').eq(0);
        var displayedActionText = actionOnlySpan.find('span').not('.ng-hide').eq(0).text();
        var hiddenActionText = actionOnlySpan.find('span.ng-hide').eq(0).text();
        expect(displayedActionText).toBe('<');
        expect(hiddenActionText).toBe('>');
    });

    it('should show '<' when right slidable is hidden', function () {
        //given
        scope.visible = false;

        //when
        var element = createElement('right');

        //then
        var actionOnlySpan = element.find('.action').eq(0).find('span').eq(0);
        var displayedActionText = actionOnlySpan.find('span').not('.ng-hide').eq(0).text();
        var hiddenActionText = actionOnlySpan.find('span.ng-hide').eq(0).text();
        expect(displayedActionText).toBe('<');
        expect(hiddenActionText).toBe('>');
    });

    it('should show '>' when right slidable is displayed', function () {
        //given
        scope.visible = true;

        //when
        var element = createElement('right');

        //then
        var actionOnlySpan = element.find('.action').eq(0).find('span').eq(0);
        var displayedActionText = actionOnlySpan.find('span').not('.ng-hide').eq(0).text();
        var hiddenActionText = actionOnlySpan.find('span.ng-hide').eq(0).text();
        expect(displayedActionText).toBe('>');
        expect(hiddenActionText).toBe('<');
    });
});