describe('Message service', function() {
    'use strict';

    beforeEach(module('data-prep.services.utils'));

    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en_US', {
            'TITLE': 'TITLE_VALUE',
            'CONTENT_WITHOUT_ARG': 'CONTENT_WITHOUT_ARG_VALUE',
            'CONTENT_WITH_ARG': 'CONTENT_WITH_ARG_VALUE : {{argValue}}'
        });
        $translateProvider.preferredLanguage('en_US');
    }));

    beforeEach(inject(function(toaster) {
        spyOn(toaster, 'pop').and.returnValue();
    }));

    it('should show toast on error without translate arg', inject(function($rootScope, MessageService, toaster) {
        //given
        var titleId = 'TITLE';
        var contentId = 'CONTENT_WITHOUT_ARG';

        //when
        MessageService.error(titleId, contentId);
        $rootScope.$digest();

        //then
        expect(toaster.pop).toHaveBeenCalledWith('error', 'TITLE_VALUE', 'CONTENT_WITHOUT_ARG_VALUE', 0);
    }));

    it('should show toast on error with translate arg', inject(function($rootScope, MessageService, toaster) {
        //given
        var titleId = 'TITLE';
        var contentId = 'CONTENT_WITH_ARG';
        var args = {argValue: 'my value'};

        //when
        MessageService.error(titleId, contentId, args);
        $rootScope.$digest();

        //then
        expect(toaster.pop).toHaveBeenCalledWith('error', 'TITLE_VALUE', 'CONTENT_WITH_ARG_VALUE : my value', 0);
    }));

    it('should show toast on warning without translate arg', inject(function($rootScope, MessageService, toaster) {
        //given
        var titleId = 'TITLE';
        var contentId = 'CONTENT_WITHOUT_ARG';

        //when
        MessageService.warning(titleId, contentId);
        $rootScope.$digest();

        //then
        expect(toaster.pop).toHaveBeenCalledWith('warning', 'TITLE_VALUE', 'CONTENT_WITHOUT_ARG_VALUE', 0);
    }));

    it('should show toast on warning with translate arg', inject(function($rootScope, MessageService, toaster) {
        //given
        var titleId = 'TITLE';
        var contentId = 'CONTENT_WITH_ARG';
        var args = {argValue: 'my value'};

        //when
        MessageService.warning(titleId, contentId, args);
        $rootScope.$digest();

        //then
        expect(toaster.pop).toHaveBeenCalledWith('warning', 'TITLE_VALUE', 'CONTENT_WITH_ARG_VALUE : my value', 0);
    }));

    it('should show toast on success without translate arg', inject(function($rootScope, MessageService, toaster) {
        //given
        var titleId = 'TITLE';
        var contentId = 'CONTENT_WITHOUT_ARG';

        //when
        MessageService.success(titleId, contentId);
        $rootScope.$digest();

        //then
        expect(toaster.pop).toHaveBeenCalledWith('success', 'TITLE_VALUE', 'CONTENT_WITHOUT_ARG_VALUE', 5000);
    }));

    it('should show toast on success with translate arg', inject(function($rootScope, MessageService, toaster) {
        //given
        var titleId = 'TITLE';
        var contentId = 'CONTENT_WITH_ARG';
        var args = {argValue: 'my value'};

        //when
        MessageService.success(titleId, contentId, args);
        $rootScope.$digest();

        //then
        expect(toaster.pop).toHaveBeenCalledWith('success', 'TITLE_VALUE', 'CONTENT_WITH_ARG_VALUE : my value', 5000);
    }));
});