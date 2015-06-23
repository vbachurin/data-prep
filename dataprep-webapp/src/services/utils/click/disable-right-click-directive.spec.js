describe('Disable right click directive', function() {
    'use strict';

    var element, createElement;

    beforeEach(module('data-prep.services.utils'));

    beforeEach(inject(function($rootScope, $compile) {
        createElement = function() {
            element = angular.element('<div disable-right-click></div>');
            $compile(element)($rootScope.$new());
        };
    }));

    it('should prevent default behavior on right click', function() {
        //given
        createElement();
        var event = angular.element.Event('contextmenu');

        spyOn(event, 'preventDefault').and.callThrough();

        //when
        element.trigger(event);

        //then
        expect(event.preventDefault).toHaveBeenCalled();
    });
});