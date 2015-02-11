'use strict';

describe('Rest message interceptor factory', function () {
    var $httpBackend;
    var httpProvider;

    beforeEach(module('data-prep', function ($httpProvider) {
        httpProvider = $httpProvider;
    }));

    beforeEach(inject(function ($injector, $window) {
        $httpBackend = $injector.get('$httpBackend');

        spyOn($window, 'alert').and.callThrough();
    }));

    it('should have the RestErrorMessageHandler as an interceptor', function () {
        expect(httpProvider.interceptors).toContain('RestErrorMessageHandler');
    });

    it('should not show alert when status code is not mapped', inject(function ($http, $window) {
        //given
        $httpBackend.expectGET('testService').respond(300);

        //when
        $http.get('testService');
        $httpBackend.flush();

        //then
        expect($window.alert).not.toHaveBeenCalled();
    }));


    it('should show alert when service is unavailable', inject(function ($http, $window) {
        //given
        $httpBackend.expectGET('testService').respond(0);

        //when
        $http.get('testService');
        $httpBackend.flush();

        //then
        expect($window.alert).toHaveBeenCalledWith('Service unavailable');
    }));
});