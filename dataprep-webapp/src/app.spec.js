describe('Dataprep app', function() {
    'use strict';

    beforeEach(module('pascalprecht.translate'));
    beforeEach(inject(function($translate) {
        spyOn($translate, 'use').and.callThrough();
    }));

    it('should set language from navigator', inject(function($rootScope, $injector, $window, $translate) {
        //given
        var myModule = angular.module('data-prep');
        var runBlock = myModule._runBlocks[0];

        //when
        $injector.invoke(runBlock);
        $rootScope.$digest();

        //then
        expect($translate.use).toHaveBeenCalledWith($window.navigator.language === 'fr' ? 'fr' : 'en');
    }));
});