describe('Badge directive', function () {
    'use strict';

    var scope, createElement;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    afterEach(function () {
        scope.$destroy();
        scope.$digest();
    });

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function () {
            var template = '<talend-badge on-close="close()" text="Displayed text"></talend-badge>';
            var element = $compile(template)(scope);
            scope.$digest();
            return element;
        };
    }));


    it('should call onClose method on badge close button clicked', function () {
        //given
        var closeMethodCalled = false;
        scope.close = function() {
            closeMethodCalled = true;
        };
        var element = createElement();

        //when
        element.find('.badge-close').eq(0).click();

        //then
        expect(closeMethodCalled).toBe(true);
    });
});