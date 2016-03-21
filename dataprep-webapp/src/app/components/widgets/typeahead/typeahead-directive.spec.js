/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

'use strict';

describe('Typeahead directive', () => {
    var scope, element;

    beforeEach(angular.mock.module('talend.widget'));
    beforeEach(angular.mock.module('htmlTemplates'));

    beforeEach(inject(($rootScope, $compile) => {

        scope = $rootScope.$new();
        scope.search = jasmine.createSpy('search');

        var html = `
            <typeahead search="search">
                <div id="inventory"></div>
            </typeahead>
        `;
        element = $compile(html)(scope);
        angular.element('body').append(element);
        scope.$digest();

        const ctrl = element.controller('typeahead');
        ctrl.searchString = 'aze';
        ctrl.results = true;
        scope.$digest();
    }));

    afterEach(() => {
        scope.$destroy();
        element.remove();
    });

    describe('input keydown', () => {
        it('should hide results on ESC', () => {
            //given
            const input = element.find('.input-search');
            const event = angular.element.Event('keydown');
            event.keyCode = 27;

            expect(element.find('.typeahead-result').length).toBe(1);

            //when
            input.trigger(event);

            //then
            expect(element.find('.typeahead-result').length).toBe(0);
        });

        it('should NOT hide results on non ESC', () => {
            //given
            const input = element.find('.input-search');
            const event = angular.element.Event('keydown');
            event.keyCode = 13;

            expect(element.find('.typeahead-result').length).toBe(1);

            //when
            input.trigger(event);

            //then
            expect(element.find('.typeahead-result').length).toBe(1);
        });
    });

    describe('click', () => {
        it('should not hide on input click', () => {
            //given
            expect(element.find('.typeahead-result').length).toBe(1);

            //when
            element.find('input').eq(0).click();

            //then
            expect(element.find('.typeahead-result').length).toBe(1);
        });

        it('should hide on body click', () => {
            //given
            expect(element.find('.typeahead-result').length).toBe(1);

            //when
            angular.element('body').click();

            //then
            expect(element.find('.typeahead-result').length).toBe(0);
        });
    });
});