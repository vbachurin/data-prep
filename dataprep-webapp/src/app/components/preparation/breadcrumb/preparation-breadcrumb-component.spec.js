/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Preparation breadcrumb component', () => {
    let scope;
    let element;
    let createElement;
    let items;
    let itemsChildren;
    let stateMock;

    beforeEach(angular.mock.module('data-prep.preparation-breadcrumb', ($provide) => {
        stateMock = {
            inventory: {
                breadcrumb: [],
                breadcrumbChildren: [],
            }
        };
        $provide.constant('state', stateMock);
    }));
    beforeEach(angular.mock.module('htmlTemplates'));

    beforeEach(inject(($rootScope, $compile) => {
        scope = $rootScope.$new(true);

        createElement = () => {
            const html = '<preparation-breadcrumb></preparation-breadcrumb>';
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
        it('should render each breadcrumb folder', () => {
            // given
            stateMock.inventory.breadcrumb = items;

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
            stateMock.inventory.breadcrumb = items;
            stateMock.inventory.breadcrumbChildren = itemsChildren;

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
        it('should redirect to item folder on name click', inject(($state) => {
            // given
            stateMock.inventory.breadcrumb = items;
            stateMock.inventory.breadcrumbChildren = itemsChildren;
            createElement();

            spyOn($state, 'go').and.returnValue();

            // when
            element.find('.breadcrumb-item').eq(1).find('.name').eq(0).click();

            // then
            expect($state.go).toHaveBeenCalledWith(
                'nav.index.preparations',
                { folderId: items[1].id }
            );
        }));

        it('should redirect to children folder on children click', inject(($state) => {
            // given
            stateMock.inventory.breadcrumb = items;
            stateMock.inventory.breadcrumbChildren = itemsChildren;
            createElement();

            spyOn($state, 'go').and.returnValue();

            // when
            element.find('.breadcrumb-item').eq(1).find('.name').eq(0).click();

            // then
            expect($state.go).toHaveBeenCalledWith(
                'nav.index.preparations',
                { folderId: itemsChildren['1'][0].id }
            );
        }));
    });

    describe('onListOpen', () => {
        it('should refresh children on folder dropdown open', inject((FolderService) => {
            // given
            stateMock.inventory.breadcrumb = items;
            stateMock.inventory.breadcrumbChildren = itemsChildren;
            createElement();

            spyOn(FolderService, 'refreshBreadcrumbChildren').and.returnValue();

            // when
            element.find('.breadcrumb-item').eq(0)
                .find('.sc-dropdown-trigger').eq(0)
                .click();

            // then
            expect(FolderService.refreshBreadcrumbChildren).toHaveBeenCalledWith(items[0].id);
        }));
    });
});
