describe('Dataset column header directive', function () {
    'use strict';
    var scope, createElement, element, ctrl;
    var body = angular.element('body');
    var column = {
        'id': '0001',
        'name': 'MostPopulousCity',
        'quality': {
            'empty': 5,
            'invalid': 10,
            'valid': 72
        },
        'type': 'string'
    };

    beforeEach(module('data-prep.datagrid-header'));
    beforeEach(module('htmlTemplates'));

    beforeEach(inject(function ($rootScope, $compile, $timeout) {
        scope = $rootScope.$new(true);
        scope.column = column;

        createElement = function () {
            element = angular.element('<datagrid-header column="column"></datagrid-header>');
            body.append(element);
            $compile(element)(scope);
            scope.$digest();
            $timeout.flush();

            ctrl = element.controller('datagridHeader');
            spyOn(ctrl, 'updateColumnName').and.returnValue();
        };
    }));

    afterEach(function () {
        scope.$destroy();
        element.remove();
    });

    it('should display column title and domain', function () {
        //given
        scope.column = {
            id: '0001',
            name: 'MostPopulousCity',
            quality: {
                empty: 5,
                invalid: 10,
                valid: 72
            },
            type: 'string',
            domain: 'city'
        };

        //when
        createElement();

        //then
        expect(element.find('.grid-header-title').text()).toBe('MostPopulousCity');
        expect(element.find('.grid-header-type').text()).toBe('city');
    });

    it('should display column title and type when there is no domain', function () {
        //when
        createElement();

        //then
        expect(element.find('.grid-header-title').text()).toBe('MostPopulousCity');
        expect(element.find('.grid-header-type').text()).toBe('text');
    });

    it('should close dropdown on get transform list error', function (done) {
        //given
        createElement();
        var menu = element.find('.grid-header-menu').eq(0);
        menu.addClass('show-menu');

        //when
        element.controller('datagridHeader').transformationsRetrieveError = true;
        scope.$apply();

        //then
        setTimeout(function () {
            expect(menu.hasClass('show-menu')).toBe(false);
            done();
        }, 250);

    });

    it('should show input to rename column name when double click', inject(function ($rootScope, $timeout) {
        //given
        createElement();

        var headerTitle = element.find('.grid-header-title').eq(0);
        expect(ctrl.isEditMode).toBeFalsy();

        //when
        headerTitle.dblclick();
        $timeout.flush();

        //then
        expect(ctrl.isEditMode).toBeTruthy();
    }));

    it('should select input text when edition mode is tuned on', inject(function ($rootScope, $timeout) {
        //given
        createElement();

        var headerTitle = element.find('.grid-header-title').eq(0);

        //when
        headerTitle.dblclick();
        $timeout.flush();

        //then
        expect(document.activeElement).toBe(element.find('.grid-header-title-input').eq(0)[0]);
        expect(window.getSelection().toString()).toBe('MostPopulousCity');

    }));

    it('should switch from input to text on ESC keydown', inject(function ($timeout) {
        //given
        createElement();

        ctrl.setEditMode(true);

        var event = angular.element.Event('keydown');
        event.keyCode = 27;

        //when
        element.find('.grid-header-title-input').eq(0).trigger(event);
        $timeout.flush();

        //then
        expect(ctrl.isEditMode).toBe(false);
    }));

    it('should reset column name on ESC keydown', function () {
        //given
        createElement();

        ctrl.setEditMode(true);
        ctrl.newName = 'toto';

        var event = angular.element.Event('keydown');
        event.keyCode = 27;

        //when
        element.find('.grid-header-title-input').eq(0).trigger(event);

        //then
        expect(ctrl.newName).toBe('MostPopulousCity');
    });

    it('should switch from input to text on ENTER event without changes', inject(function ($timeout) {
        //given
        createElement();

        ctrl.setEditMode(true);

        var event = angular.element.Event('keydown');
        event.keyCode = 13;

        //when
        element.find('.grid-header-title-input').eq(0).trigger(event);
        $timeout.flush();

        //then
        expect(ctrl.isEditMode).toBe(false);
    }));

    it('should submit update on ENTER with changes', function () {
        //given
        createElement();

        ctrl.setEditMode(true);
        ctrl.newName = 'MostPopulousCityInTheWorld';

        var event = angular.element.Event('keydown');
        event.keyCode = 13;

        //when
        element.find('.grid-header-title-input').eq(0).trigger(event);

        //then
        expect(ctrl.updateColumnName).toHaveBeenCalled();
    });

    it('should switch from input to text on BLUR event without changes', inject(function ($timeout) {
        //given
        createElement();

        ctrl.setEditMode(true);

        var event = angular.element.Event('blur');

        //when
        element.find('.grid-header-title-input').eq(0).trigger(event);
        $timeout.flush();

        //then
        expect(ctrl.isEditMode).toBe(false);
    }));

    it('should submit update on BLUR event with changes', function () {
        //given
        createElement();

        ctrl.setEditMode(true);
        ctrl.newName = 'MostPopulousCityInTheWorld';

        var event = angular.element.Event('blur');

        //when
        element.find('.grid-header-title-input').eq(0).trigger(event);

        //then
        expect(ctrl.updateColumnName).toHaveBeenCalled();
    });
});