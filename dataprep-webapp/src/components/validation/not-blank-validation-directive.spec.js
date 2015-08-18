describe('Not Blank Validation directive', function() {
    'use strict';

    var scope, createElement;

    beforeEach(module('data-prep.validation'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function(directiveScope) {
            var element = angular.element('<form name="myForm"><input name="myInput" ng-model="myInput" not-blank-validation="{{nullable}}" /></form>');
            $compile(element)(directiveScope);
            directiveScope.$digest();
            return element;
        };
    }));

    it('should validate empty input', function() {
        //given
        scope.nullable = true;
        scope.myInput = '';

        //when
        createElement(scope);

        //then
        expect(scope.myForm.$invalid).toBeFalsy();
    });

    it('should not validate input when nullable is not authorized and input is empty', function() {
        //given
        scope.nullable = false;
        scope.myInput = '';

        //when
        createElement(scope);

        //then
        expect(scope.myForm.$invalid).toBeTruthy();

    });

    it('should validate input when nullable is not authorized and input is not empty', function() {
        //given
        scope.nullable = false;
        scope.myInput = 'city';

        //when
        createElement(scope);

        //then
        expect(scope.myForm.$invalid).toBeFalsy();
    });
});