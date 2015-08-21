describe('Not Blank Validation directive', function() {
    'use strict';

    var scope, createElement;

    beforeEach(module('data-prep.validation'));

    beforeEach(inject(function($rootScope, $compile) {
        scope = $rootScope.$new();
        createElement = function(directiveScope) {
            var element = angular.element('<form name="myForm"><input name="myInput" ng-model="myInput" can-be-blank="{{canBeBlank}}" /></form>');
            $compile(element)(directiveScope);
            directiveScope.$digest();
            return element;
        };
    }));

    it('should validate empty input when it can be blank', function() {
        //given
        scope.canBeBlank = true;
        scope.myInput = '';

        //when
        createElement(scope);

        //then
        expect(scope.myForm.$invalid).toBeFalsy();
    });

    it('should invalidate input when it cannot be blank and input is empty', function() {
        //given
        scope.canBeBlank = false;
        scope.myInput = '';

        //when
        createElement(scope);

        //then
        expect(scope.myForm.$invalid).toBeTruthy();

    });

    it('should validate input when it cannot be blank and input is not empty', function() {
        //given
        scope.canBeBlank = false;
        scope.myInput = 'city';

        //when
        createElement(scope);

        //then
        expect(scope.myForm.$invalid).toBeFalsy();
    });
});