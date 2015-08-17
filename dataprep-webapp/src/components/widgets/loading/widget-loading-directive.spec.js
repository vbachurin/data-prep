describe('Loading directive', function () {
    'use strict';

    var scope, element;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();

        var html = '<talend-loading></talend-dropdown>';
        element = $compile(html)(scope);
        scope.$digest();
    }));

    beforeEach(function () {
        jasmine.clock().install();
    });
    afterEach(function () {
        jasmine.clock().uninstall();
    });

    it('should immediatly add "is-loading" class when "talend.loading.start" is emitted', inject(function ($rootScope) {
        //given
        expect(element.hasClass('is-loading')).toBe(false);

        //when
        $rootScope.$emit('talend.loading.start');
        $rootScope.$digest();

        //then
        expect(element.hasClass('is-loading')).toBe(true);
    }));

    it('should add "show-loading" class after 200ms when "talend.loading.start" is emitted', inject(function ($rootScope) {
        //given
        expect(element.hasClass('show-loading')).toBe(false);

        //when
        $rootScope.$emit('talend.loading.start');
        $rootScope.$digest();
        expect(element.hasClass('show-loading')).toBe(false);
        jasmine.clock().tick(200);

        //then
        expect(element.hasClass('show-loading')).toBe(true);
    }));

    it('should remove "show-loading" class when "talend.loading.start" is emitted', inject(function ($rootScope) {
        //given
        element.addClass('is-loading show-loading');

        //when
        $rootScope.$emit('talend.loading.stop');
        $rootScope.$digest();

        //then
        expect(element.hasClass('show-loading')).toBe(false);
        expect(element.hasClass('is-loading')).toBe(false);
    }));
});