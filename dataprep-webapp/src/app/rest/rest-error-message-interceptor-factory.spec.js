'use strict';

describe('Rest message interceptor factory', function () {
    var $httpBackend;
    var httpProvider;

    beforeEach(module('data-prep', function ($httpProvider) {
        httpProvider = $httpProvider;
    }));

    beforeEach(inject(function ($injector, toaster) {
        $httpBackend = $injector.get('$httpBackend');

        spyOn(toaster, 'pop').and.callThrough();
    }));

    it('should have the RestErrorMessageHandler as an interceptor', function () {
        expect(httpProvider.interceptors).toContain('RestErrorMessageHandler');
    });

    it('should not show toast when status code is not mapped', inject(function ($http, toaster) {
        //given
        $httpBackend.expectGET('testService').respond(300);

        //when
        $http.get('testService');
        $httpBackend.flush();

        //then
        expect(toaster.pop).not.toHaveBeenCalled();
    }));


    it('should show toast when service is unavailable', inject(function ($http, toaster) {
        //given
        $httpBackend.expectGET('testService').respond(0);

        //when
        $http.get('testService');
        $httpBackend.flush();

        //then
        expect(toaster.pop).toHaveBeenCalledWith('error', 'Error', 'Service unavailable');
    }));

    it('should show toast on status 500', inject(function ($http, toaster) {
        //given
        $httpBackend.expectGET('testService').respond(500);

        //when
        $http.get('testService');
        $httpBackend.flush();

        //then
        expect(toaster.pop).toHaveBeenCalledWith('error', 'Error', 'An error occurred');
    }));
});