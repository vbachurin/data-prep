/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

export default class PreparationBreadcrumbController {
    constructor($state, state, FolderService) {
        'ngInject';
        this.$state = $state;
        this.state = state;
        this.FolderService = FolderService;
    }
    
    go(folder) {
        this.$state.go('nav.index.preparations', { folderId: folder.id });
    }

    fetchChildren(folder) {
        this.FolderService.refreshBreadcrumbChildren(folder.id);
    }
}
