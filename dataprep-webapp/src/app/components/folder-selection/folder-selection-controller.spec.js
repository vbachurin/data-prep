/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('folder selection controller', function () {

    let createController, scope, ctrl;

    beforeEach(angular.mock.module('data-prep.folder-selection'));

    beforeEach(angular.mock.module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'HOME_FOLDER': 'Home'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(inject(function ($rootScope, $componentController) {
        scope = $rootScope.$new();

        createController = function (selectedFolder) {
            return $componentController('folderSelection', {
                $scope: scope
            }, selectedFolder);
        };
    }));

    /******************************************************************************************************************/
    /******************************************* INITIALIZATION *******************************************************/
    /******************************************************************************************************************/
    describe('folders initialization', function () {
        it('should init folders tree at initialization', inject(function ($translate) {
            //when
            ctrl = createController(null);
            ctrl.$onInit();

            //then
            expect(ctrl.folderItems[0]).toEqual({
                path: '',
                level: 0,
                childrenFetched: true,
                display: true,
                collapsed: false,
                name: $translate.instant('HOME_FOLDER'),
                originalFolder: {
                    path: '',
                    name: $translate.instant('HOME_FOLDER')
                }
            });
        }));

        it('should locate current folder in the folders tree at initialization at 1st level', inject(function ($q, FolderService) {
            //given
            let currentFolderContent = {name: 'lookup', path: 'lookup'};
            let currentFolder = {selectedFolder: currentFolderContent};
            spyOn(FolderService, 'children').and.returnValue($q.when([currentFolderContent]));

            //when
            ctrl = createController(currentFolder);
            ctrl.$onInit();
            scope.$digest();

            //then
            expect(FolderService.children).toHaveBeenCalledWith('');
            expect(FolderService.children.calls.count()).toBe(1);
            expect(ctrl.selectedFolder.selected).toBe(true);
        }));

        it('should locate current folder in the folders tree at initialization at Root level', inject(function ($q, FolderService) {
            //given
            let currentFolderContent = {name: 'Home', path: '/'};
            let currentFolder = {selectedFolder: currentFolderContent};
            spyOn(FolderService, 'children').and.returnValue($q.when([]));

            //when
            ctrl = createController(currentFolder);
            ctrl.$onInit();
            scope.$digest();

            //then
            expect(FolderService.children).toHaveBeenCalledWith('');
            expect(FolderService.children.calls.count()).toBe(1);
            expect(ctrl.selectedFolder.selected).toBe(true);
            expect(ctrl.selectedFolder).toEqual(ctrl.folderItems[0]);
            expect(ctrl.selectedFolder.childrenFetched).toBe(true);
        }));

        it('should locate current folder in the folders tree at initialization at 2nd/nth level', inject(function ($q, $rootScope, FolderService) {
            //given
            let currentFolderContent = {name: 'subSubFolder', path: 'folder/subFolder/subSubFolder'};
            let currentFolder = {selectedFolder: currentFolderContent};


            var counter = 0;
            spyOn(FolderService, 'children').and.callFake(function () {
                if (counter === 0) {
                    counter++;
                    return $q.when([{name: 'folder', path: 'folder'}]);
                }
                else if (counter === 1) {
                    counter++;
                    return $q.when([{name: 'subFolder', path: 'folder/subFolder'}]);
                }
                else if (counter === 2) {
                    return $q.when([currentFolderContent])
                }
            });

            //when
            ctrl = createController(currentFolder);
            ctrl.$onInit();
            $rootScope.$digest();

            //then
            expect(ctrl.selectedFolder.selected).toBe(true);
        }));
    });

    /******************************************************************************************************************/
    /******************************************** ACTIONS ON FOLDERS **************************************************/
    /******************************************************************************************************************/
    describe('updating folders display on toggling', function () {

        var folderToToggle = {
            name: '1-folder11',
            path: '1-folder11',
            level: 1,
            display: true
        };

        var folderChildren = [
            {
                path: '1-folder11/2-folder111',
                name: '2-folder111',
                level: 2
            },
            {
                path: '1-folder11/2-folder112',
                name: '2-folder112',
                level: 2
            },
            {
                path: '1-folder11/2-folder113',
                name: '2-folder113',
                level: 2
            }
        ];

        beforeEach(function () {
            let currentFolderContent = {name: 'Home', path: '/'};
            let currentFolder = {selectedFolder: currentFolderContent};
            ctrl = createController(currentFolder);
        });

        describe('with children', function () {

            beforeEach(inject(function ($q, FolderService) {
                spyOn(FolderService, 'children').and.returnValue($q.when(folderChildren));
            }));

            it('should show folder\'s children for the first time', inject(function ($rootScope) {
                //given
                var allFolders = [
                    {
                        path: '',
                        level: 0,
                        childrenFetched: true,
                        display: true,
                        collapsed: false,
                        name: 'Home'
                    },
                    folderToToggle
                ];
                ctrl.folderItems = allFolders;
                ctrl.tree = ctrl.folderItems;

                folderToToggle.collapsed = true;
                folderToToggle.childrenFetched = false;

                //when
                ctrl.toggle(folderToToggle);
                $rootScope.$digest();

                //then
                expect(ctrl.folderItems.length).toBe(5);
                expect(ctrl.folderItems[2].originalFolder).toEqual(folderChildren[0]);

                expect(ctrl.folderItems[2].level).toBe(2);
                expect(ctrl.folderItems[2].collapsed).toBe(true);
                expect(ctrl.folderItems[2].display).toBe(true);

                expect(ctrl.folderItems[3].level).toBe(2);
                expect(ctrl.folderItems[3].collapsed).toBe(true);
                expect(ctrl.folderItems[3].display).toBe(true);

                expect(folderToToggle.collapsed).toBe(false);
            }));

            it('should hide folder\'s children when no parent siblings', function () {
                //given
                folderToToggle.collapsed = false;
                var allFolders = [{
                    path: '',
                    level: 0,
                    childrenFetched: true,
                    display: true,
                    collapsed: false,
                    name: 'Home'
                },
                    folderToToggle];

                _.each(folderChildren, function(child){
                    child.display = true;
                });
                ctrl.folderItems = allFolders.concat(folderChildren);
                ctrl.tree = ctrl.folderItems;

                //when
                ctrl.toggle(folderToToggle);

                //then
                expect(ctrl.folderItems[2].display).toBe(false);
                expect(ctrl.folderItems[3].display).toBe(false);
                expect(ctrl.folderItems[4].display).toBe(false);
                expect(folderToToggle.collapsed).toBe(true);
            });

            it('should show folder\'s children when parent siblings exist', function () {
                //given
                folderToToggle.collapsed = true;
                folderToToggle.childrenFetched = true;
                var allFolders = [{
                    path: '',
                    level: 0,
                    childrenFetched: true,
                    display: true,
                    collapsed: false,
                    name: 'Home'
                },
                    folderToToggle];
                _.each(folderChildren, function(child){
                    child.display = false;
                });
                ctrl.folderItems = allFolders.concat(folderChildren);
                ctrl.folderItems.push({
                    name: '1-folder12',
                    path: '1-folder12',
                    level: 1,
                    display: true
                });
                ctrl.tree = ctrl.folderItems;

                //when
                ctrl.toggle(folderToToggle);

                //then
                expect(ctrl.folderItems[2].display).toBe(true);
                expect(ctrl.folderItems[3].display).toBe(true);
                expect(ctrl.folderItems[4].display).toBe(true);
                expect(ctrl.folderItems[5]).toEqual({
                    name: '1-folder12',
                    path: '1-folder12',
                    level: 1,
                    display: true
                });

                expect(folderToToggle.collapsed).toBe(false);
            });

            it('should show the same subfolders before their parent has been collapsed', function(){
                //given
                folderToToggle.collapsed = true;
                folderToToggle.childrenFetched = true;
                var allFolders = [{
                    path: '',
                    level: 0,
                    childrenFetched: true,
                    display: true,
                    collapsed: false,
                    name: 'Home'
                },
                    folderToToggle];

                _.each(folderChildren, function(child){
                    child.display = false;
                });
                ctrl.folderItems = allFolders.concat(folderChildren);
                ctrl.folderItems.push({
                    name: '2-folder1131',
                    path: '1-folder11/2-folder113/3-folder1131',
                    level: 3,
                    display: false
                });
                ctrl.folderItems[4].collapsed = true;
                ctrl.tree = ctrl.folderItems;

                //when
                ctrl.toggle(folderToToggle);

                //then
                expect(ctrl.folderItems[5].display).toBe(false);
            });
        });

        describe('with no children', function(){
            beforeEach(inject(function($q, FolderService){
                spyOn(FolderService, 'children').and.returnValue($q.when([]));
            }));

            it('should not show any folder\'s descendants', inject(function ($rootScope) {
                //given
                var allFolders = [{
                    path: '',
                    level: 0,
                    childrenFetched: true,
                    display: true,
                    collapsed: false,
                    name: 'Home'
                },
                    folderToToggle];
                ctrl.folderItems = allFolders;
                ctrl.tree = ctrl.folderItems;

                folderToToggle.collapsed = true;
                folderToToggle.childrenFetched = false;

                //when
                ctrl.toggle(folderToToggle);
                $rootScope.$digest();

                //then
                expect(folderToToggle.childrenFetched).toBe(true);
                expect(ctrl.folderItems.length).toBe(2);

                expect(folderToToggle.collapsed).toBe(false);
            }));
        });
    });

    describe('select a destination folder', function(){
        it('should set the destination folder as the selected one', function(){
            //given
            ctrl = createController();
            var previousSelectedFolder = {selected:true};
            ctrl.selectedFolder = previousSelectedFolder;
            var folder = {};

            //when
            ctrl.chooseFolder(folder);

            //then
            expect(previousSelectedFolder.selected).toBe(false);
            expect(ctrl.selectedFolder).toBe(folder);
            expect(ctrl.selectedFolder.selected).toBe(true);
        })
    });

    /******************************************************************************************************************/
    /************************************************ SEARCH FOLDERS **************************************************/
    /******************************************************************************************************************/
    describe('folders search', function () {
        describe('with results match', function () {
            var foldersSearchResult = [
                {path: 'folder-1', name: 'folder-1'},
                {path: 'folder-1/sub-1', name: 'sub-1'},
                {path: 'folder-1/sub-2/folder-1-beer', name: 'folder-1-beer'}
            ];

            beforeEach(inject(function ($q, FolderService) {
                ctrl = createController(null);
                spyOn(FolderService, 'search').and.returnValue($q.when(foldersSearchResult));
            }));

            it('should call search folders service', inject(function (FolderService) {
                //given
                ctrl.searchFolderQuery = 'beer';

                //when
                ctrl.populateSearchResult();

                //then
                expect(FolderService.search).toHaveBeenCalledWith(ctrl.searchFolderQuery);
            }));

            it('should call filter root folder and search folders service', inject(function () {
                //given
                ctrl.searchFolderQuery = 'e';

                //when
                ctrl.populateSearchResult();
                scope.$digest();

                //then
                expect(ctrl.folderItems.length).toBe(foldersSearchResult.length + 1);
            }));

            it('should call search folders service', inject(function () {
                //given
                ctrl.searchFolderQuery = 'f';

                //when
                ctrl.populateSearchResult();
                scope.$digest();

                //then
                expect(ctrl.folderItems.length).toBe(foldersSearchResult.length);
                expect(ctrl.folderItems[0].originalFolder).toEqual(foldersSearchResult[0]);
                expect(ctrl.folderItems[1].originalFolder).toEqual(foldersSearchResult[1]);
                expect(ctrl.folderItems[2].originalFolder).toEqual(foldersSearchResult[2]);
            }));

            it('should show the folders tree and not the search result', inject(function ($translate) {
                //given
                ctrl.searchFolderQuery = '';
                ctrl.tree = [{
                    path: '',
                    display: true,
                    hasNoChildren: true,
                    name: $translate.instant('HOME_FOLDER'),
                    originalFolder: {
                        name: $translate.instant('HOME_FOLDER'),
                        path: ''
                    }
                }];

                //when
                ctrl.populateSearchResult();
                scope.$digest();

                //then
                expect(ctrl.folderItems).toEqual(ctrl.tree);
            }));

            it('should select the 1st result by default', inject(function () {
                //given
                ctrl.searchFolderQuery = 'f';

                //when
                ctrl.populateSearchResult();
                scope.$digest();

                //then
                expect(ctrl.selectedFolder.originalFolder).toBe(foldersSearchResult[0]);
                expect(ctrl.selectedFolder.selected).toBe(true);
            }));
        });

        describe('without results match', function () {
            beforeEach(inject(function ($q, FolderService) {
                ctrl = createController(null);
                spyOn(FolderService, 'search').and.returnValue($q.when([]));
            }));

            it('should not show any search result', inject(function () {
                //given
                ctrl.searchFolderQuery = 'f';

                //when
                ctrl.populateSearchResult();
                scope.$digest();

                //then
                expect(ctrl.selectedFolder).toBe(undefined);
            }));
        });
    });

});