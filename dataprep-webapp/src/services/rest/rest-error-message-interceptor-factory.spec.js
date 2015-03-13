describe('Rest message interceptor factory', function () {
    'use strict';

    var $httpBackend;
    var httpProvider;

    beforeEach(module('data-prep.services.rest', function ($httpProvider) {
        httpProvider = $httpProvider;
    }));

    beforeEach(inject(function ($injector, MessageService) {
        $httpBackend = $injector.get('$httpBackend');
        $httpBackend.when('GET', 'i18n/en.json').respond({});
        $httpBackend.when('GET', 'i18n/fr.json').respond({});

        spyOn(MessageService, 'error').and.callThrough();
    }));

    it('should have the RestErrorMessageHandler as an interceptor', function () {
        expect(httpProvider.interceptors).toContain('RestErrorMessageHandler');
    });

    it('should not show alert when status code is not mapped', inject(function ($rootScope, $http, MessageService) {
        //given
        $httpBackend.expectGET('testService').respond(300);

        //when
        $http.get('testService');
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(MessageService.error).not.toHaveBeenCalled();
    }));
    
    it('should show alert when service is unavailable', inject(function ($rootScope, $http, MessageService) {
        //given
        $httpBackend.expectGET('testService').respond(0);

        //when
        $http.get('testService');
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(MessageService.error).toHaveBeenCalledWith('SERVER_ERROR_TITLE', 'SERVICE_UNAVAILABLE');
    }));

    it('should show toast on status 500', inject(function ($rootScope, $http, MessageService) {
        //given
        $httpBackend.expectGET('testService').respond(500);

        //when
        $http.get('testService');
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(MessageService.error).toHaveBeenCalledWith('SERVER_ERROR_TITLE', 'GENERIC_ERROR');
    }));
});