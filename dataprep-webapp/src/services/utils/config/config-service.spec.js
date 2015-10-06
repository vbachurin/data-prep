describe('Config service', function() {
    'use strict';

    var $httpBackend;

    beforeEach(module('data-prep.services.utils'));

    beforeEach(inject(function(RestURLs, $injector) {
        $httpBackend = $injector.get('$httpBackend');

        spyOn(RestURLs, 'setServerUrl').and.returnValue();
    }));

    it('should get server url and configure api url', inject(function($rootScope, ConfigService, RestURLs) {
        //given
        var serverUrl = 'http://10.10.10.10:8888';
        $httpBackend
            .expectGET('/assets/config/config.json')
            .respond(200, {
                serverUrl: serverUrl
            });

        expect(RestURLs.setServerUrl).not.toHaveBeenCalled();

        //when
        ConfigService.init();
        $httpBackend.flush();

        //then
        expect(RestURLs.setServerUrl).toHaveBeenCalledWith(serverUrl);
    }));
});