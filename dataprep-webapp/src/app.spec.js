describe('Dataprep app', function() {
    'use strict';

    beforeEach(module('pascalprecht.translate'));
    beforeEach(module('data-prep.services.utils'));

    describe('run', function() {
        it('should set language from navigator', inject(function($rootScope, $injector, $window, $translate) {
            //given
            var myModule = angular.module('data-prep');
            var runBlock = myModule._runBlocks[0];

            spyOn($translate, 'use').and.returnValue();

            //when
            $injector.invoke(runBlock);
            $rootScope.$digest();

            //then
            expect($translate.use).toHaveBeenCalledWith($window.navigator.language === 'fr' ? 'fr' : 'en');
        }));
    });

    describe('config', function() {
        it('should set $httpProvider useApplyAsync config to true', function() {
            //given
            var httpProviderIt = null;

            //when
            module('data-prep', function($httpProvider) {
                httpProviderIt = $httpProvider;
            });
            inject(function($injector) {
                var $httpBackend = $injector.get('$httpBackend');
                $httpBackend.when('GET', 'i18n/en.json').respond({});
                $httpBackend.when('GET', 'i18n/fr.json').respond({});
            });

            //then
            expect(httpProviderIt.useApplyAsync()).toBe(true);
        });
    });
});