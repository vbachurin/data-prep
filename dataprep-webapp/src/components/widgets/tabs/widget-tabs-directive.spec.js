describe('Tabs directive', function () {
    'use strict';

    var scope, element, createElement;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    beforeEach(inject(function ($rootScope, $compile) {
        scope = $rootScope.$new();

        createElement = function () {
            var template = '<talend-tabs>' +
                '   <talend-tabs-item tab-title="tab 1 title">' +
                '       <div id="tab1Content">Content tab 1</div>' +
                '   </talend-tabs-item>' +
                '   <talend-tabs-item tab-title="tab 2 title" default="true">' +
                '       <div id="tab2Content">Content tab 2</div>' +
                '   </talend-tabs-item>' +
                '   <talend-tabs-item tab-title="tab 3 title">' +
                '       <div id="tab3Content">Content tab 3</div>' +
                '   </talend-tabs-item>' +
                '</talend-tabs>';
            element = $compile(template)(scope);
            scope.$digest();
        };
    }));

    it('should render tabs headers', function () {
        //when
        createElement();

        //then
        expect(element.find('.tabs-item').length).toBe(3);
        expect(element.find('.tabs-item').eq(0).text().trim()).toBe('tab 1 title');
        expect(element.find('.tabs-item').eq(1).text().trim()).toBe('tab 2 title');
        expect(element.find('.tabs-item').eq(2).text().trim()).toBe('tab 3 title');
    });

    it('should display default tab content', function () {
        //when
        createElement();

        //then
        expect(element.find('#tab1Content').length).toBe(0);
        expect(element.find('#tab2Content').length).toBe(1);
        expect(element.find('#tab3Content').length).toBe(0);
    });

    it('should display clicked tab', function () {
        //given
        createElement();

        expect(element.find('#tab1Content').length).toBe(0);
        expect(element.find('#tab2Content').length).toBe(1);
        expect(element.find('#tab3Content').length).toBe(0);

        //when
        element.find('.tabs-item').eq(0).click();

        //then
        expect(element.find('#tab1Content').length).toBe(1);
        expect(element.find('#tab2Content').length).toBe(0);
        expect(element.find('#tab3Content').length).toBe(0);
    });

    it('should unregister tabs on scope destroy', function () {
        //given
        createElement();
        expect(element.controller('talendTabs').tabs.length).toBe(3);

        //when
        scope.$destroy();

        //then
        expect(element.controller('talendTabs').tabs.length).toBe(0);
    });
});