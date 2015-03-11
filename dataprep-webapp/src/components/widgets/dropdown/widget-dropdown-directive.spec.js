'use strict';

describe('Dropdown directive', function () {
    var scope, element, html;

    beforeEach(module('talend.widget'));
    beforeEach(module('htmlTemplates'));

    var clickDropdownToggle = function (elm) {
        elm = elm || element;
        elm.find('.dropdown-action').eq(0).click();
    };

    var clickDropdownItem = function (elm) {
        elm = elm || element;
        elm.find('a[role="menuitem"]').eq(0).click();
    };

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });

    describe('closeable dropdown', function() {

        beforeEach(inject(function ($rootScope, $compile) {
            scope = $rootScope.$new();

            html = '<talend-dropdown id="dropdown1">' +
            '    <div class="dropdown-container grid-header">' +
            '        <div class="dropdown-action">' +
            '            <div class="grid-header-title dropdown-button">{{ column.id }}</div>' +
            '            <div class="grid-header-type">{{ column.type }}</div>' +
            '        </div>' +
            '        <ul class="dropdown-menu grid-header-menu" style="display:none;">' +
            '            <li role="presentation"><a role="menuitem" href="#">Hide Column</a></li>' +
            '            <li class="divider"></li>' +
            '            <li role="presentation"><a role="menuitem" href="#">Split first Space</a></li>' +
            '            <li role="presentation"><a role="menuitem" href="#">Uppercase</a></li>' +
            '        </ul>' +
            '    </div>' +
            '</talend-dropdown>';
            element = $compile(html)(scope);
            scope.$digest();
        }));

        it('should show dropdown-menu on dropdown-action click', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            expect(menu.hasClass('show-menu')).toBe(false);

            //when
            clickDropdownToggle();

            //then
            expect(menu.hasClass('show-menu')).toBe(true);
        });

        it('should show dropdown-menu on dropdown-action click when menu is visible', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            clickDropdownToggle();
            expect(menu.hasClass('show-menu')).toBe(true);

            //when
            clickDropdownToggle();

            //then
            expect(menu.hasClass('show-menu')).toBe(false);
        });

        it('should hide dropdown-menu on item click', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            clickDropdownToggle();
            expect(menu.hasClass('show-menu')).toBe(true);

            //when
            clickDropdownItem();

            //then
            expect(menu.hasClass('show-menu')).toBe(false);
        });

        it('should register window scroll handler on open', inject(function ($window) {
            //given
            expect($._data( angular.element($window)[0], 'events' )).not.toBeDefined();

            //when
            clickDropdownToggle();

            //then
            expect($._data( angular.element($window)[0], 'events' )).toBeDefined();
            expect($._data( angular.element($window)[0], 'events').scroll.length).toBe(1);
        }));

        it('should unregister window scroll on close', inject(function ($window) {
            //given
            clickDropdownToggle();
            expect($._data( angular.element($window)[0], 'events').scroll.length).toBe(1);

            //when
            clickDropdownToggle();

            //then
            expect($._data( angular.element($window)[0], 'events' )).not.toBeDefined();
        }));

        it('should hide dropdown-menu on body mousedown', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);

            clickDropdownToggle();
            expect(menu.hasClass('show-menu')).toBe(true);

            //when
            angular.element('body').mousedown();

            //then
            expect(menu.hasClass('show-menu')).toBe(false);
        });

        it('should unregister body mousedown on element remove', function () {
            //given
            expect($._data( angular.element('body')[0], 'events').mousedown.length).toBe(1);

            //when
            element.remove();

            //then
            expect($._data( angular.element('body')[0], 'events' )).not.toBeDefined();
        });

        it('should stop mousedown propagation on dropdown-menu mousedown', function () {
            //given
            var bodyMouseDown = false;
            angular.element('body').mousedown(function() {
                bodyMouseDown = true;
            });

            //when
            element.find('.dropdown-menu').mousedown();

            //then
            expect(bodyMouseDown).toBe(false);
        });
    });

    describe('not closeable on click dropdown', function() {
        beforeEach(inject(function ($rootScope, $compile) {
            scope = $rootScope.$new();

            html = '<talend-dropdown id="dropdown1" close-on-select="false">' +
            '    <div class="dropdown-container grid-header">' +
            '        <div class="dropdown-action">' +
            '            <div class="grid-header-title dropdown-button">{{ column.id }}</div>' +
            '            <div class="grid-header-type">{{ column.type }}</div>' +
            '        </div>' +
            '        <ul class="dropdown-menu grid-header-menu" style="display:none;">' +
            '            <li role="presentation"><a role="menuitem" href="#">Hide Column</a></li>' +
            '            <li class="divider"></li>' +
            '            <li role="presentation"><a role="menuitem" href="#">Split first Space</a></li>' +
            '            <li role="presentation"><a role="menuitem" href="#">Uppercase</a></li>' +
            '        </ul>' +
            '    </div>' +
            '</talend-dropdown>';
            element = $compile(html)(scope);
            scope.$digest();
        }));

        it('should not hide dropdown-menu on item click if closeOnSelect is false', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            clickDropdownToggle();
            expect(menu.hasClass('show-menu')).toBe(true);

            //when
            clickDropdownItem();

            //then
            expect(menu.hasClass('show-menu')).toBe(true);
        });
    });

    describe('with onOpen action', function() {
        beforeEach(inject(function ($rootScope, $compile) {
            scope = $rootScope.$new();
            scope.onOpen = function() {};
            spyOn(scope, 'onOpen').and.returnValue(true);

            html = '<talend-dropdown id="dropdown1" on-open="onOpen()">' +
            '    <div class="dropdown-container grid-header">' +
            '        <div class="dropdown-action">' +
            '            <div class="grid-header-title dropdown-button">{{ column.id }}</div>' +
            '            <div class="grid-header-type">{{ column.type }}</div>' +
            '        </div>' +
            '        <ul class="dropdown-menu grid-header-menu" style="display:none;">' +
            '            <li role="presentation"><a role="menuitem" href="#">Hide Column</a></li>' +
            '            <li class="divider"></li>' +
            '            <li role="presentation"><a role="menuitem" href="#">Split first Space</a></li>' +
            '            <li role="presentation"><a role="menuitem" href="#">Uppercase</a></li>' +
            '        </ul>' +
            '    </div>' +
            '</talend-dropdown>';
            element = $compile(html)(scope);
            scope.$digest();
        }));

        it('should call action on open click', function () {
            //given
            expect(scope.onOpen).not.toHaveBeenCalled();

            //when
            clickDropdownToggle();

            //then
            expect(scope.onOpen).toHaveBeenCalled();
        });
    });
});