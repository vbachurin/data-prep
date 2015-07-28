describe('Dataset column header directive', function() {
    'use strict';
    var scope, createElement, element;
    var body = angular.element('body');

    beforeEach(module('data-prep.datagrid-header'));
    beforeEach(module('htmlTemplates'));

    afterEach(function() {
        scope.$destroy();
        element.remove();
    });


    beforeEach(inject(function($rootScope, $compile, $timeout) {

        scope = $rootScope.$new(true);
        createElement = function(directiveScope) {
            element = angular.element('<datagrid-header column="column"></datagrid-header>');
            body.append(element);
            $compile(element)(directiveScope);
            directiveScope.$digest();
            $timeout.flush();
        };
    }));
    
    it('should display column title and domain', function() {
        //given
        scope.column = {
            'id': '0',
            'name': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string',
            'domain': 'city'
        };

        //when
        createElement(scope);

        //then
        expect(element.find('.grid-header-title').text()).toBe('MostPopulousCity');
        expect(element.find('.grid-header-type').text()).toBe('city');
    });

    it('should display column title and type', function() {
        //given
        scope.column = {
            'id': '0',
            'name': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string'
        };

        //when
        createElement(scope);

        //then
        expect(element.find('.grid-header-title').text()).toBe('MostPopulousCity');
        expect(element.find('.grid-header-type').text()).toBe('text');
    });

    it('should close dropdown on get transform list error', function(done) {
        //given
        scope.column = {
            'id': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string'
        };

        createElement(scope);
        var menu = element.find('.grid-header-menu').eq(0);
        menu.addClass('show-menu');

        //when
        element.controller('datagridHeader').transformationsRetrieveError = true;
        scope.$apply();

        //then
       setTimeout(function() {
           expect(menu.hasClass('show-menu')).toBe(false);
           done();
       }, 300);

    });

    it('should show input to rename column name when double click', inject(function($rootScope, $timeout) {
        //given
        scope.column = {
            'id': '0000',
            'name': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string'
        };

        createElement(scope);
        var headerTitle = element.find('.grid-header-title').eq(0);

        var ctrl = element.controller('datagridHeader');

        expect(ctrl.isEditMode).toBeFalsy();
        expect(ctrl.updateEnabled).toBeFalsy();

        //when
        headerTitle.dblclick();
        $timeout.flush();

        //then
        expect(ctrl.isEditMode).toBeTruthy();

        expect(document.activeElement).toBe(element.find('.grid-header-title-input').eq(0)[0]);
        expect(window.getSelection().toString()).toBe('MostPopulousCity');

    }));


    it('should switch from input to text on  ESC keydown', function (done) {


        inject(function($timeout) {

            //given
            scope.column = {
                'id': '0000',
                'name': 'MostPopulousCity',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            };

            createElement(scope);
            var headerTitle = element.find('.grid-header-title').eq(0);

            var ctrl = element.controller('datagridHeader');

            expect(ctrl.isEditMode).toBeFalsy();
            expect(ctrl.updateEnabled).toBeFalsy();

            //when
            headerTitle.dblclick();
            $timeout.flush();

            //then
            expect(ctrl.isEditMode).toBeTruthy();
            expect(document.activeElement).toBe(element.find('.grid-header-title-input').eq(0)[0]);
            expect(window.getSelection().toString()).toBe('MostPopulousCity');


            //given
            var event = angular.element.Event('keydown');
            event.keyCode = 27;

            //when
            element.find('.grid-header-title-input').eq(0).trigger(event);
            $timeout.flush();

            //then
            setTimeout(function() {
                expect(ctrl.isEditMode).toBeFalsy();
                expect(ctrl.updateEnabled).toBeFalsy();
                done();
            }, 100);

        });

    });


    it('should switch from input to text on  ENTER keyup when no changes', function (done) {

        inject(function($rootScope, $timeout) {

            //given
            scope.column = {
                'id': '0000',
                'name': 'MostPopulousCity',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            };

            createElement(scope);
            var headerTitle = element.find('.grid-header-title').eq(0);

            var ctrl = element.controller('datagridHeader');

            expect(ctrl.isEditMode).toBeFalsy();
            expect(ctrl.updateEnabled).toBeFalsy();

            //when
            headerTitle.dblclick();
            $timeout.flush();

            //then
            expect(ctrl.isEditMode).toBeTruthy();
            expect(document.activeElement).toBe(element.find('.grid-header-title-input').eq(0)[0]);
            expect(window.getSelection().toString()).toBe('MostPopulousCity');


            //given
            var event = angular.element.Event('keyup');
            event.keyCode = 13;

            //when
            element.find('.grid-header-title-input').eq(0).trigger(event);
            spyOn(ctrl, 'updateColumnName');

            $timeout.flush();

            //then
            setTimeout(function() {
                expect(ctrl.isEditMode).toBeFalsy();
                expect(ctrl.updateEnabled).toBeFalsy();
                expect(ctrl.updateColumnName).not.toHaveBeenCalled();
                done();
            }, 100);

        });

    });

    it('should submit update on  ENTER keyup when changes', function (done) {

        inject(function($rootScope, $timeout, PlaygroundService, $q) {

            spyOn(PlaygroundService, 'appendStep').and.returnValue($q.when(true));

            //given
            scope.column = {
                'id': '0000',
                'name': 'MostPopulousCity',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            };

            createElement(scope);
            var headerTitle = element.find('.grid-header-title').eq(0);

            var ctrl = element.controller('datagridHeader');

            expect(ctrl.isEditMode).toBeFalsy();
            expect(ctrl.updateEnabled).toBeFalsy();

            //when
            headerTitle.dblclick();
            $timeout.flush();

            //then
            expect(ctrl.isEditMode).toBeTruthy();
            expect(document.activeElement).toBe(element.find('.grid-header-title-input').eq(0)[0]);
            expect(window.getSelection().toString()).toBe('MostPopulousCity');


            //given
            ctrl.newName='MostPopulousCityInTheWorld';

            var event = angular.element.Event('keyup');
            event.keyCode = 13;

            //when
            element.find('.grid-header-title-input').eq(0).trigger(event);
            spyOn(ctrl, 'updateColumnName');

            $rootScope.$digest();

            //then
            setTimeout(function() {
                expect(ctrl.isEditMode).toBeFalsy();
                expect(ctrl.updateEnabled).toBeFalsy();
                expect(ctrl.oldName).toBe('MostPopulousCityInTheWorld');
                done();
            }, 1);

        });

    });


    it('should switch from input to text on  BLUR event when no changes', function (done) {

        inject(function($rootScope, $timeout) {

            //given
            scope.column = {
                'id': '0000',
                'name': 'MostPopulousCity',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            };

            createElement(scope);
            var headerTitle = element.find('.grid-header-title').eq(0);

            var ctrl = element.controller('datagridHeader');

            expect(ctrl.isEditMode).toBeFalsy();
            expect(ctrl.updateEnabled).toBeFalsy();

            //when
            headerTitle.dblclick();
            $timeout.flush();

            //then
            expect(ctrl.isEditMode).toBeTruthy();
            expect(document.activeElement).toBe(element.find('.grid-header-title-input').eq(0)[0]);
            expect(window.getSelection().toString()).toBe('MostPopulousCity');


            //given
            var event = angular.element.Event('blur');

            //when
            element.find('.grid-header-title-input').eq(0).trigger(event);
            spyOn(ctrl, 'updateColumnName');

            $timeout.flush();

            //then
            setTimeout(function() {
                expect(ctrl.isEditMode).toBeFalsy();
                expect(ctrl.updateEnabled).toBeFalsy();
                expect(ctrl.updateColumnName).not.toHaveBeenCalled();
                done();
            }, 100);

        });

    });

    it('should submit update on  BLUR event when changes', function (done) {

        inject(function($rootScope, $timeout, PlaygroundService, $q) {

            spyOn(PlaygroundService, 'appendStep').and.returnValue($q.when(true));

            //given
            scope.column = {
                'id': '0000',
                'name': 'MostPopulousCity',
                'quality': {
                    'empty': 5,
                    'invalid': 10,
                    'valid': 72
                },
                'type': 'string'
            };

            createElement(scope);
            var headerTitle = element.find('.grid-header-title').eq(0);

            var ctrl = element.controller('datagridHeader');

            expect(ctrl.isEditMode).toBeFalsy();
            expect(ctrl.updateEnabled).toBeFalsy();

            //when
            headerTitle.dblclick();
            $timeout.flush();

            //then
            expect(ctrl.isEditMode).toBeTruthy();
            expect(document.activeElement).toBe(element.find('.grid-header-title-input').eq(0)[0]);
            expect(window.getSelection().toString()).toBe('MostPopulousCity');


            //given
            ctrl.newName='MostPopulousCityInTheWorld';

            var event = angular.element.Event('blur');

            //when
            element.find('.grid-header-title-input').eq(0).trigger(event);
            spyOn(ctrl, 'updateColumnName');

            $rootScope.$digest();

            //then
            setTimeout(function() {
                expect(ctrl.isEditMode).toBeFalsy();
                expect(ctrl.updateEnabled).toBeFalsy();
                expect(ctrl.oldName).toBe('MostPopulousCityInTheWorld');
                done();
            }, 1);

        });

    });
});