describe('Datetime validation directive', function() {
   'use strict';

    var scope, createElement;


    beforeEach(module('data-prep.validation'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function(directiveScope) {
            var element = angular.element('<form name="myForm"><input id="myInput" ng-model="myInput" is-date-time /></form>');
            $compile(element)(directiveScope);
            directiveScope.$digest();
            return element;
        };
    }));

    it('should not validate empty input', function() {
        //when
        createElement(scope);

        //then
        expect(scope.myForm.$invalid).toBeTruthy();
    });

    it('should not validate integer', function() {
        //given
        scope.myInput = 5.2;

        //when
        createElement(scope);

        //then
        expect(scope.myForm.$invalid).toBeTruthy();
    });


    it('should validate a date', function() {
      //given
      scope.myInput = '02/01/2012 10:02:23';

      //when
      createElement(scope);

      //then
      expect(scope.myForm.$invalid).toBeFalsy();
    });

});