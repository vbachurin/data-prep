'use strict';

describe('Button Switch directive', function () {
    var scope, element, createElement;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function() {
            scope.buttonAction = jasmine.createSpy('buttonAction');

            scope.buttonCurrentObj = {id: 'name', name: 'NAME_SORT'};

            scope.buttonValues = [
                    {id: 'name', name: 'NAME_SORT'},
                    {id: 'date', name: 'DATE_SORT'}
                ];

            var html = '<talend-button-switch ' +
                'button-values="buttonValues" ' +
                'button-current-obj="buttonCurrentObj" ' +
                'button-action="buttonAction(param)">' +
                '</talend-button-switch>';
            element = $compile(html)(scope);
            scope.$digest();
        };

    }));

    it('should call action on main button click', function() {
        //given
        createElement();

        //when
        element.find('.button-action').eq(0).click();

        //then
        expect(scope.buttonAction).toHaveBeenCalledWith({id: 'date', name: 'DATE_SORT'});
    });


});
