/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Preparation breadcrumb controller', () => {
    let createController;
    let scope;

    beforeEach(angular.mock.module('data-prep.preparation-breadcrumb'));

    beforeEach(inject(($rootScope, $componentController) => {
        scope = $rootScope.$new(true);

        createController = () => $componentController(
            'preparationBreadcrumb',
            { $scope: scope }
        );
    }));

    it('should go to folder', inject(($state) => {
        // given
        const ctrl = createController();
        const folder = { id: '1' };

        spyOn($state, 'go').and.returnValue();

        // when
        ctrl.go(folder);

        // then
        expect($state.go).toHaveBeenCalledWith(
            'nav.index.preparations',
            { folderId: folder.id }
        );
    }));

    it('should update folder children', inject((FolderService) => {
        // given
        const ctrl = createController();
        const folder = { id: '1' };

        spyOn(FolderService, 'refreshBreadcrumbChildren').and.returnValue();

        // when
        ctrl.fetchChildren(folder);

        // then
        expect(FolderService.refreshBreadcrumbChildren).toHaveBeenCalledWith(folder.id);
    }));
});
