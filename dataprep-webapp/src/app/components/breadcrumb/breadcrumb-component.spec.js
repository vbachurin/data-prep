/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Breadcrumb component', () => {
    let scope;
    let element;
    let createElement;
    let items;
    let itemsChildren;

    beforeEach(angular.mock.module('data-prep.breadcrumb'));
    beforeEach(angular.mock.module('htmlTemplates'));

    beforeEach(inject(($rootScope, $compile) => {
        scope = $rootScope.$new(true);

        createElement = () => {
            const html = `
                <breadcrumb items="items"
                            children="children"
                            on-list-open="onListOpen(item)"
                            on-select="onSelect(item)"></breadcrumb>
            `;
            element = $compile(html)(scope);
            scope.$digest();
        };
    }));

    beforeEach(() => {
        items = [
            { id: '1', name: 'HOME' },
            { id: '2', name: 'JSO' },
            { id: '3', name: 'Perso' },
        ];

        itemsChildren = {
            '1': [
                { id: '2', name: 'JSO' },
                { id: '4', name: 'Others' },
            ]
        };
    });

    afterEach(() => {
        scope.$destroy();
        element.remove();
    });

    describe('render', () => {
        it('should render each items', () => {
            // given
            scope.items = items;

            // when
            createElement();

            // then
            expect(element.find('.breadcrumb-item').length).toBe(3);
            expect(element.find('.breadcrumb-item').eq(0).text().trim()).toBe('HOME');
            expect(element.find('.breadcrumb-item').eq(1).text().trim()).toBe('JSO');
            expect(element.find('.breadcrumb-item').eq(2).text().trim()).toBe('Perso');
        });

        it('should display children in dropdown', () => {
            // given
            scope.items = items;
            scope.children = itemsChildren;

            // when
            createElement();

            // then
            const dropdownContent = element.find('.breadcrumb-item').eq(0).find('sc-dropdown-content').eq(0);
            expect(dropdownContent.find('.breadcrumb-item-child').length).toBe(2);
            expect(dropdownContent.find('.breadcrumb-item-child').eq(0).text().trim()).toBe('JSO');
            expect(dropdownContent.find('.breadcrumb-item-child').eq(1).text().trim()).toBe('Others');
        });
    });

    describe('onSelect', () => {
        it('should trigger callback on item name click', () => {
            // given
            scope.onSelect = jasmine.createSpy('onSelect');
            scope.items = items;
            createElement();

            expect(scope.onSelect).not.toHaveBeenCalled();

            // when
            element.find('.breadcrumb-item').eq(1).find('.name').eq(0).click();

            // then
            expect(scope.onSelect).toHaveBeenCalledWith(items[1]);
        });

        it('should trigger callback on children click', () => {
            // given
            scope.onSelect = jasmine.createSpy('onSelect');
            scope.items = items;
            scope.children = itemsChildren;
            createElement();

            expect(scope.onSelect).not.toHaveBeenCalled();

            // when
            element.find('.breadcrumb-item').eq(0).find('.breadcrumb-item-child').eq(0).click();
            // then
            expect(scope.onSelect).toHaveBeenCalledWith(itemsChildren['1'][0]);
        });
    });

    describe('onListOpen', () => {
        it('should trigger callback on dropdown open', () => {
            // given
            scope.onListOpen = jasmine.createSpy('onListOpen');
            scope.items = items;
            scope.children = itemsChildren;
            createElement();

            expect(scope.onListOpen).not.toHaveBeenCalled();

            // when
            element.find('.breadcrumb-item').eq(0)
                .find('.sc-dropdown-trigger').eq(0)
                .click();

            // then
            expect(scope.onListOpen).toHaveBeenCalledWith(items[0]);
        });
    });
});
