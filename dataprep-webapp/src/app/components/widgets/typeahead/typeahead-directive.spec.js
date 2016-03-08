/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

'use strict';

describe('Typeahead directive', function () {
    var scope, element, ctrl;

    beforeEach(angular.mock.module('talend.widget'));
    beforeEach(angular.mock.module('htmlTemplates'));

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    describe('closeable dropdown', function () {

        beforeEach(inject(function ($rootScope, $compile) {

            scope = $rootScope.$new();
            scope.search = function() {};
            scope.searchInput = '';

            var html = '<typeahead search-string="searchInput" on-change="search">' +
                '    <div class="inventory">' +
                '    </div>' +
                '</typeahead>';
            element = $compile(html)(scope);
            scope.$digest();

            ctrl = element.controller('typeahead');
        }));

        it('should show dropdown-menu when input changes', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            expect(menu.hasClass('show-menu')).toBe(false);

            //when
            ctrl.searchString = 'test';
            scope.$digest();

            //then
            expect(menu.hasClass('show-menu')).toBe(true);
        });

        it('should hide dropdown-menu', function () {
            //given
            ctrl.searchString = 'test';
            scope.$digest();

            //when
            ctrl.searchString = '';
            scope.$digest();

            //then
            var menu = element.find('.dropdown-menu').eq(0);
            expect(menu.hasClass('show-menu')).toBe(false);
        });

        it('should hide dropdown-menu on item click', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            menu.addClass('show-menu');

            //when
            element.find('.inventory').eq(0).click();

            //then
            expect(menu.hasClass('show-menu')).toBe(false);
        });

        it('should register window scroll handler on open', inject(function ($window) {
            //given
            expect($._data(angular.element($window)[0], 'events')).not.toBeDefined();

            //when
            ctrl.searchString = 'test';
            scope.$digest();

            //then
            expect($._data(angular.element($window)[0], 'events')).toBeDefined();
            expect($._data(angular.element($window)[0], 'events').scroll.length).toBe(1);
        }));

        it('should unregister window scroll on close', inject(function ($window) {
            //given
            ctrl.searchString = 'test';
            scope.$digest();

            expect($._data(angular.element($window)[0], 'events').scroll.length).toBe(1);

            //when
            ctrl.searchString = '';
            scope.$digest();

            //then
            expect($._data(angular.element($window)[0], 'events')).not.toBeDefined();
        }));

        it('should hide dropdown-menu on body mousedown', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            menu.addClass('show-menu');

            //when
            angular.element('body').mousedown();

            //then
            expect(menu.hasClass('show-menu')).toBe(false);
        });

        it('should unregister body mousedown on element remove', function () {
            //given
            expect($._data(angular.element('body')[0], 'events').mousedown.length).toBe(1);

            //when
            element.remove();

            //then
            expect($._data(angular.element('body')[0], 'events')).not.toBeDefined();
        });

        it('should stop mousedown propagation on dropdown-menu mousedown', function () {
            //Given
            var bodyMouseDown = false;
            var  mouseDownCallBack = function () {
                bodyMouseDown = true;
            };
            angular.element('body').mousedown(mouseDownCallBack);

            //when
            element.find('.dropdown-menu').mousedown();

            //then
            expect(bodyMouseDown).toBe(false);

            angular.element('body').off('mousedown', mouseDownCallBack);
        });

        it('should hide dropdown menu on ESC', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            menu.addClass('show-menu');

            var event = angular.element.Event('keydown');
            event.keyCode = 27;

            //when
            menu.trigger(event);

            //then
            expect(menu.hasClass('show-menu')).toBe(false);
        });

        it('should not hide dropdown menu on not ESC keydown', function () {
            //given
            var menu = element.find('.dropdown-menu').eq(0);
            menu.addClass('show-menu');

            var event = angular.element.Event('keydown');
            event.keyCode = 13;

            //when
            menu.trigger(event);

            //then
            expect(menu.hasClass('show-menu')).toBe(true);
        });
    });

    describe('not closeable on click dropdown', function () {
        var createElement;

        beforeEach(inject(function ($rootScope, $compile) {
            scope = $rootScope.$new();
            scope.search = function() {};
            scope.side = null;
            scope.searchInput = '';

            createElement = function() {
                var html = '<typeahead close-on-select="false" search-string="searchInput" on-change="search">' +
                    '    <div class="inventory">' +
                    '    </div>' +
                    '</typeahead>';
                element = angular.element(html);
                $compile(element)(scope);
                scope.$digest();
            };
        }));

        it('should not hide dropdown-menu on item click if closeOnSelect is false', function () {
            //given
            createElement();

            var menu = element.find('.dropdown-menu').eq(0);
            menu.addClass('show-menu');

            //when
            element.find('.inventory').eq(0).click();

            //then
            expect(menu.hasClass('show-menu')).toBe(true);
        });
    });

    describe('force placement side', function () {
        var createElement;

        beforeEach(inject(function ($rootScope, $compile) {
            scope = $rootScope.$new();
            scope.search = function() {};

            createElement = function() {
                var html = '<typeahead force-side="{{side}}" search-string="searchInput" on-change="search">' +
                    '    <div class="inventory">' +
                    '    </div>' +
                    '</typeahead>';
                element = angular.element(html);
                $compile(element)(scope);
                scope.$digest();

                ctrl = element.controller('typeahead');
            };
        }));

        it('should set menu placement to the right by default', function () {
            //given
            scope.side = null;
            scope.searchInput = '';
            createElement();

            var menu = element.find('.dropdown-menu').eq(0);
            expect(menu.hasClass('right')).toBe(false);

            //when
            ctrl.searchString = 'te';
            scope.$digest();

            //then
            expect(menu.hasClass('right')).toBe(true);
        });

        it('should force menu placement to the left', function () {
            //given
            scope.side = 'left';
            scope.searchInput = '';
            createElement();

            var menu = element.find('.dropdown-menu').eq(0);
            menu.addClass('right');

            //when
            ctrl.searchString = 'te';
            scope.$digest();

            //then
            expect(menu.hasClass('right')).toBe(false);
        });

        it('should force menu placement to the right', function () {
            //given
            scope.side = 'right';
            scope.searchInput = '';
            createElement();

            var menu = element.find('.dropdown-menu').eq(0);
            expect(menu.hasClass('right')).toBe(false);

            //when
            ctrl.searchString = 'te';
            scope.$digest();

            //then
            expect(menu.hasClass('right')).toBe(true);
        });
    });
});