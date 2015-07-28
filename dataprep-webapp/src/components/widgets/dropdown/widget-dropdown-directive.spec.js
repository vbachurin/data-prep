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

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    describe('closeable dropdown', function () {

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

        it('should show dropdown-menu on dropdown-action click', function (done) {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            expect(menu.hasClass('show-menu')).toBe(false);

            //when
            clickDropdownToggle();

            //then
            //setTimeout with 300ms is used because of the timer to detect the single click in the directive
            setTimeout(function() {
                expect(menu.hasClass('show-menu')).toBe(true);
                done();
            }, 300);

        });

        it('should focus on dropdown menu when it is shown', function () {
            //given
            jasmine.clock().install();
            var menu = element.find('.dropdown-menu').eq(0)[0];
            var body = angular.element('body');
            body.append(element);
            expect(document.activeElement).not.toBe(menu);

            //when
            clickDropdownToggle();
            jasmine.clock().tick(100);

            //then
            expect(document.activeElement).not.toBe(element.find('.dropdown-menu').eq(0)[0]);
            jasmine.clock().uninstall();
        });

        it('should show dropdown-menu on dropdown-action click when menu is visible', function (done) {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            clickDropdownToggle();

            setTimeout(function() {
                expect(menu.hasClass('show-menu')).toBe(true);

                //when
                clickDropdownToggle();

                //then
                setTimeout(function() {
                    expect(menu.hasClass('show-menu')).toBe(false);
                    done();
                }, 300);

            }, 300);


        });


        it('should hide dropdown-menu on item click', function (done) {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            clickDropdownToggle();

            setTimeout(function() {
                expect(menu.hasClass('show-menu')).toBe(true);

                //when
                clickDropdownItem();

                //then
                expect(menu.hasClass('show-menu')).toBe(false);
                done();
            }, 300);



        });

        it('should register window scroll handler on open', function(done) {

            inject(function($window) {
                //given
                expect($._data(angular.element($window)[0], 'events')).not.toBeDefined();

                //when
                clickDropdownToggle();

                //then
                setTimeout(function () {
                    expect($._data(angular.element($window)[0], 'events')).toBeDefined();
                    expect($._data(angular.element($window)[0], 'events').scroll.length).toBe(1);
                    done();
                }, 300);

            });

        });

        it('should unregister window scroll on close', function ( done) {

            inject(function($window) {

                //given
                clickDropdownToggle();

                setTimeout(function() {
                    expect($._data(angular.element($window)[0], 'events').scroll.length).toBe(1);

                    //when
                    clickDropdownToggle();

                    //then
                    setTimeout(function() {
                        expect($._data(angular.element($window)[0], 'events')).not.toBeDefined();
                        done();
                    }, 300);

                }, 300);
            });

        });

        it('should hide dropdown-menu on body mousedown', function (done) {
            //given
            var menu = element.find('.dropdown-menu').eq(0);

            clickDropdownToggle();

            setTimeout(function() {

                expect(menu.hasClass('show-menu')).toBe(true);

                //when
                angular.element('body').mousedown();

                //then
                expect(menu.hasClass('show-menu')).toBe(false);
                done();

            }, 300);

        });

        it('should unregister body mousedown on element remove', function () {
            //given
            expect($._data(angular.element('body')[0], 'events').mousedown.length).toBe(1);

            //when
            element.remove();

            //then
            expect($._data(angular.element('body')[0], 'events')).not.toBeDefined();
        });

        it('should stop mousedown propagation on dropdown-menu mousedown', function (done) {
            //given
            var bodyMouseDown = false;
            angular.element('body').mousedown(function () {
                bodyMouseDown = true;
            });

            //when
            element.find('.dropdown-menu').mousedown();

            //then
            setTimeout(function() {
                expect(bodyMouseDown).toBe(false);
                done();
            }, 300);

        });

        it('should hide dropdown menu on ESC', function (done) {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            clickDropdownToggle();

            setTimeout(function() {
                expect(menu.hasClass('show-menu')).toBe(true);

                var event = angular.element.Event('keydown');
                event.keyCode = 27;

                //when
                menu.trigger(event);

                //then
                expect(menu.hasClass('show-menu')).toBe(false);
                done();
            }, 300);

        });

        it('should not hide dropdown menu on not ESC keydown', function (done) {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            clickDropdownToggle();

            setTimeout(function() {
                expect(menu.hasClass('show-menu')).toBe(true);
            }, 300);


            var event = angular.element.Event('keydown');
            event.keyCode = 13;

            //when
            menu.trigger(event);

            //then
            setTimeout(function() {
                expect(menu.hasClass('show-menu')).toBe(true);
                done();
            }, 300);

        });

        it('should focus on dropdown action when menu is hidden by ESC', function () {
            //given
            jasmine.clock().install();

            var action = element.find('.dropdown-action').eq(0);
            var menu = element.find('.dropdown-menu').eq(0);
            angular.element('body').append(element);
            expect(document.activeElement).not.toBe(menu[0]);

            clickDropdownToggle();
            jasmine.clock().tick(100);

            var event = angular.element.Event('keydown');
            event.keyCode = 27;

            //when
            menu.trigger(event);
            jasmine.clock().tick(100);

            //then
            expect(document.activeElement).toBe(action[0]);
            jasmine.clock().uninstall();
        });

    });

    describe('not closeable on click dropdown', function () {
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

        it('should not hide dropdown-menu on item click if closeOnSelect is false', function (done) {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            clickDropdownToggle();

            setTimeout(function() {
                expect(menu.hasClass('show-menu')).toBe(true);
            }, 300);

            //when
            clickDropdownItem();

            //then
            setTimeout(function() {
                expect(menu.hasClass('show-menu')).toBe(true);
                done();
            }, 300);

        });
    });

    describe('with onOpen action', function () {
        beforeEach(inject(function ($rootScope, $compile) {
            scope = $rootScope.$new();
            scope.onOpen = function () {
            };
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

        it('should call action on open click', function (done) {
            //given
            expect(scope.onOpen).not.toHaveBeenCalled();

            //when
            clickDropdownToggle();

            //then
            setTimeout(function() {
                expect(scope.onOpen).toHaveBeenCalled();
                done();
            }, 300);

        });
    });
});