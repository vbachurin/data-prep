describe('Datagrid tooltip directive', function() {
    'use strict';
    var scope, element;

    beforeEach(module('data-prep.datagrid-tooltip'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function($rootScope, $compile, $window) {
        scope = $rootScope.$new();
        element = angular.element('<datagrid-tooltip record="record" key="colId" position="position" requested-state="showTooltip" html-str="htmlStr"></datagrid-tooltip>');
        $compile(element)(scope);
        scope.$digest();

        angular.element('body').append(element);
        $window.innerWidth = 1920;
        $window.innerHeight = 1080;
    }));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    it('should display tooltip with the right content', function() {
        //given
        scope.htmlStr = '    Toto aux toilettes';
        scope.colId = 'name';
        scope.showTooltip = true;

        expect(element.find('.datagrid-tooltip').hasClass('ng-hide')).toBe(true);
        //when
        scope.$digest();

        //then
        expect(element.find('.datagrid-tooltip').hasClass('ng-hide')).toBe(false);
        expect(element.find('.datagrid-tooltip-content').eq(0).text()).toBe('    Toto aux toilettes');
    });

    it('should focus on inner textarea when edit mode is turn on', inject(function($timeout) {
        //given
        var textarea = element.find('textarea').eq(0)[0];
        scope.showTooltip = true;
        scope.$digest();

        expect(document.activeElement).not.toBe(textarea);

        //when
        element.find('.datagrid-tooltip-content').eq(0).click();
        scope.$digest();
        $timeout.flush();

        //then
        expect(document.activeElement).toBe(textarea);
    }));
});