describe('Dropdown directive', function () {
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

    it('should add "loading-open" class on body after 200ms when "talend.loading.start" is emitted', inject(function ($rootScope, $timeout) {
        //given
        var body = angular.element('body');
        expect(body.hasClass('loading-open')).toBe(false);

        //when
        $rootScope.$emit('talend.loading.start');
        $rootScope.$digest();
        expect(body.hasClass('loading-open')).toBe(false);
        $timeout.flush(200);

        //then
        expect(body.hasClass('loading-open')).toBe(true);
    }));

    it('should remove "loading-open" class on body when "talend.loading.start" is emitted', inject(function ($rootScope) {
        //given
        var body = angular.element('body');
        body.addClass('loading-open');

        //when
        $rootScope.$emit('talend.loading.stop');
        $rootScope.$digest();

        //then
        expect(body.hasClass('loading-open')).toBe(false);
    }));
});