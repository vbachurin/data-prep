describe('Tabs directive', function () {
    'use strict';

    var scope, element, createElement;
    var body = angular.element('body');
    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    beforeEach(inject(function ($rootScope, $compile, $timeout) {
        scope = $rootScope.$new();

        createElement = function () {

            scope.onTabChange = jasmine.createSpy('onTabChange');
            scope.onTabInit = jasmine.createSpy('onTabInit');

            var template = '<talend-tabs tab="selectedTab" on-tab-change="onTabChange()">' +
                '   <talend-tabs-item tab-title="tab 1 title" on-init="onTabInit()">' +
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
            body.append(element);
            scope.$digest();
            $timeout.flush();
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

    it('should call setSelectedTab when tab changes', inject(function ($rootScope) {
        //given
        createElement();
        var ctrl = element.controller('talendTabs');
        spyOn(ctrl, 'setSelectedTab');

        //when
        scope.selectedTab = 1;
        $rootScope.$digest();

        //then
        expect(ctrl.setSelectedTab).toHaveBeenCalled();
    }));

    it('should NOT call setSelectedTab if tab is not defined', inject(function ($rootScope) {
        //given
        createElement();
        var ctrl = element.controller('talendTabs');
        spyOn(ctrl, 'setSelectedTab');

        //when
        scope.selectedTab = undefined;
        $rootScope.$digest();

        //then
        expect(ctrl.setSelectedTab).not.toHaveBeenCalled();
    }));

    it('should execute callback when changing tabs', inject(function () {
        //given
        createElement();
        expect(scope.onTabChange).not.toHaveBeenCalled();

        //when
        scope.selectedTab = 2;
        scope.$apply();

        //then
        expect(scope.onTabChange).toHaveBeenCalled();
    }));

    it('should execute callback when initializing the tabs', inject(function () {
        //when
        createElement();

        //then
        expect(scope.onTabInit).toHaveBeenCalled();
        expect(scope.onTabInit.calls.count()).toBe(1);
    }));
});