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

    beforeEach(inject(function ($rootScope, $compile) {

        scope = $rootScope.$new();
        scope.search = function() {};

        var html = '<typeahead search="search">' +
            '    <div class="inventory">' +
            '    </div>' +
            '</typeahead>';
        element = $compile(html)(scope);
        scope.$digest();

        ctrl = element.controller('typeahead');
    }));

    it('should show typeahead-menu when input changes', function () {
        //given
        var menu = element.find('.typeahead-menu').eq(0);
        expect(menu.hasClass('show-menu')).toBe(false);

        //when
        ctrl.searchString = 'test';
        scope.$digest();

        //then
        expect(menu.hasClass('show-menu')).toBe(true);
    });

    it('should hide typeahead-menu', function () {
        //given
        ctrl.searchString = 'test';
        scope.$digest();

        //when
        ctrl.searchString = '';
        scope.$digest();

        //then
        var menu = element.find('.typeahead-menu').eq(0);
        expect(menu.hasClass('show-menu')).toBe(false);
    });

    it('should not hide when clicking on input', function () {
        //given
        var menu = element.find('.typeahead-menu').eq(0);
        ctrl.searchString = 'test';
        scope.$digest();

        //when
        element.find('input').eq(0).click();

        //then
        expect(menu.hasClass('show-menu')).toBe(true);
    });

    it('should hide typeahead-menu on item click', function () {
        //given
        var menu = element.find('.typeahead-menu').eq(0);
        menu.addClass('show-menu');

        //when
        element.find('.inventory').eq(0).click();

        //then
        expect(menu.hasClass('show-menu')).toBe(false);
    });

    it('should hide typeahead-menu on body click', function () {
        //given
        var menu = element.find('.typeahead-menu').eq(0);
        menu.addClass('show-menu');

        //when
        angular.element('body').click();

        //then
        expect(menu.hasClass('show-menu')).toBe(false);
    });

    it('should unregister body click on element remove', function () {
        //given
        expect($._data(angular.element('body')[0], 'events').click.length).toBe(1);

        //when
        element.remove();

        //then
        expect($._data(angular.element('body')[0], 'events')).not.toBeDefined();
    });

    it('should stop click propagation on typeahead-menu click', function () {
        //Given
        var bodyClick = false;
        var  clickCallBack = function () {
            bodyClick = true;
        };
        angular.element('body').click(clickCallBack);

        //when
        element.find('.typeahead-menu').click();

        //then
        expect(bodyClick).toBe(false);

        angular.element('body').off('click', clickCallBack);
    });

    it('should hide typeahead menu on ESC', function () {
        //given
        var input = element.find('input').eq(0);
        var menu = element.find('.typeahead-menu').eq(0);
        menu.addClass('show-menu');

        var event = angular.element.Event('keydown');
        event.keyCode = 27;

        //when
        input.trigger(event);

        //then
        expect(menu.hasClass('show-menu')).toBe(false);
    });

    it('should not hide typeahead menu on not ESC keydown', function () {
        //given
        var menu = element.find('.typeahead-menu').eq(0);
        var input = element.find('input').eq(0);
        menu.addClass('show-menu');

        var event = angular.element.Event('keydown');
        event.keyCode = 13;

        //when
        input.trigger(event);

        //then
        expect(menu.hasClass('show-menu')).toBe(true);
    });
});