/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Dataset Header controller', () => {

    let createController, scope, stateMock;

    var sortList = [
        {id: 'name', name: 'NAME_SORT', property: 'name'},
        {id: 'date', name: 'DATE_SORT', property: 'created'}
    ];

    var orderList = [
        {id: 'asc', name: 'ASC_ORDER'},
        {id: 'desc', name: 'DESC_ORDER'}
    ];

    beforeEach(angular.mock.module('data-prep.dataset-header', function ($provide) {
        stateMock = {
            inventory: {
                sortList: sortList,
                orderList: orderList,
                sort: sortList[0],
                order: orderList[0],
                currentFolder: {path: ''}
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(function ($rootScope, $componentController, $q, FolderService, StorageService, StateService) {
        scope = $rootScope.$new();

        createController = () => {
            const ctrl = $componentController('datasetHeader', {$scope: scope});
            ctrl.folderNameForm = {
                $commitViewValue: jasmine.createSpy('$commitViewValue')
            };
            return ctrl;
        };

        spyOn(FolderService, 'refreshDatasetsSort').and.returnValue();
        spyOn(FolderService, 'refreshDatasetsOrder').and.returnValue();
        spyOn(FolderService, 'create').and.returnValue($q.when());
        spyOn(StorageService, 'setDatasetsSort').and.returnValue();
        spyOn(StorageService, 'setDatasetsOrder').and.returnValue();
        spyOn(StateService, 'setDatasetsSort').and.returnValue();
        spyOn(StateService, 'setDatasetsOrder').and.returnValue();
    }));

    describe('init', () => {
        it('should refresh sort/order', inject((FolderService) => {
            //given
            const ctrl = createController();

            //when
            ctrl.$onInit();

            //then
            expect(FolderService.refreshDatasetsSort).toHaveBeenCalled();
            expect(FolderService.refreshDatasetsOrder).toHaveBeenCalled();
        }));
    });

    describe('folder', () => {
        describe('open', () => {
            it('should open creation modal', () => {
                //given
                const ctrl = createController();
                expect(ctrl.folderNameModal).toBeFalsy();

                //when
                ctrl.openFolderModal();

                //then
                expect(ctrl.folderNameModal).toBe(true);
            });

            it('should reset folder name model on open', () => {
                //given
                const ctrl = createController();
                ctrl.folderName = 'toto';

                //when
                ctrl.openFolderModal();

                //then
                expect(ctrl.folderName).toBe('');
            });
        });

        describe('create', () => {
            beforeEach(inject((FolderService) => {
                spyOn(FolderService, 'getContent').and.returnValue();
            }));

            it('should force input model sync', () => {
                //given
                const ctrl = createController();

                //when
                ctrl.addFolder();

                //then
                expect(ctrl.folderNameForm.$commitViewValue).toHaveBeenCalled();
            });

            it('should create folder from home', inject((FolderService) => {
                //given
                const ctrl = createController();
                ctrl.folderName = 'toto';
                stateMock.inventory.currentFolder.path = null;

                expect(FolderService.create).not.toHaveBeenCalled();

                //when
                ctrl.addFolder();

                //then
                expect(FolderService.create).toHaveBeenCalledWith('/toto');
            }));

            it('should create folder from current path', inject((FolderService) => {
                //given
                const ctrl = createController();
                ctrl.folderName = 'toto';
                stateMock.inventory.currentFolder.path = '/my/current/path';

                expect(FolderService.create).not.toHaveBeenCalled();

                //when
                ctrl.addFolder();

                //then
                expect(FolderService.create).toHaveBeenCalledWith('/my/current/path/toto');
            }));

            it('should refresh current folder', inject((FolderService) => {
                //given
                const ctrl = createController();
                ctrl.folderName = 'toto';

                expect(FolderService.getContent).not.toHaveBeenCalled();

                //when
                ctrl.addFolder();
                scope.$digest();

                //then
                expect(FolderService.getContent).toHaveBeenCalledWith(stateMock.inventory.currentFolder);
            }));

            it('should hide modal', inject((FolderService) => {
                //given
                const ctrl = createController();
                ctrl.folderName = 'toto';
                ctrl.folderNameModal = true;

                expect(FolderService.getContent).not.toHaveBeenCalled();

                //when
                ctrl.addFolder();
                scope.$digest();

                //then
                expect(ctrl.folderNameModal).toBe(false);
            }));
        });
    });

    //TODO updateSortBy & updateSortOrder
    describe('sort', () => {
        it('should set new sort in state', inject(($q, StateService, FolderService) => {
            //given
            const ctrl = createController();
            const sortType = sortList[1];

            spyOn(FolderService, 'getContent').and.returnValue($q.when());
            expect(StateService.setDatasetsSort).not.toHaveBeenCalled();

            //when
            ctrl.updateSortBy(sortType);

            //then
            expect(StateService.setDatasetsSort).toHaveBeenCalledWith(sortType);
        }));

        it('should store new sort in storage', inject(($q, StorageService, FolderService) => {
            //given
            const ctrl = createController();
            const sortType = sortList[1];

            spyOn(FolderService, 'getContent').and.returnValue($q.when());
            expect(StorageService.setDatasetsSort).not.toHaveBeenCalled();

            //when
            ctrl.updateSortBy(sortType);

            //then
            expect(StorageService.setDatasetsSort).toHaveBeenCalledWith(sortType.id);
        }));

        it('should refresh folder content', inject(($q, FolderService) => {
            //given
            const ctrl = createController();
            const sortType = sortList[1];

            spyOn(FolderService, 'getContent').and.returnValue($q.when());

            //when
            ctrl.updateSortBy(sortType);

            //then
            expect(FolderService.getContent).toHaveBeenCalled();
        }));

        it('should set back old sort in state when folder refresh fails', inject(($q, StateService, FolderService) => {
            //given
            const ctrl = createController();
            const sortType = sortList[1];
            const oldSortType = stateMock.inventory.sort;

            spyOn(FolderService, 'getContent').and.returnValue($q.reject());
            expect(StateService.setDatasetsSort).not.toHaveBeenCalledWith(oldSortType);

            //when
            ctrl.updateSortBy(sortType);
            expect(StateService.setDatasetsSort).not.toHaveBeenCalledWith(oldSortType);
            scope.$digest();

            //then
            expect(StateService.setDatasetsSort).toHaveBeenCalledWith(oldSortType);
        }));

        it('should set back old sort in storage when folder refresh fails', inject(($q, StorageService, FolderService) => {
            //given
            const ctrl = createController();
            const sortType = sortList[1];
            const oldSortType = stateMock.inventory.sort;

            spyOn(FolderService, 'getContent').and.returnValue($q.reject());
            expect(StorageService.setDatasetsSort).not.toHaveBeenCalledWith(oldSortType.id);

            //when
            ctrl.updateSortBy(sortType);
            expect(StorageService.setDatasetsSort).not.toHaveBeenCalledWith(oldSortType.id);
            scope.$digest();

            //then
            expect(StorageService.setDatasetsSort).toHaveBeenCalledWith(oldSortType.id);
        }));

        it('should do nothing when sort is already applied', inject((StateService, StorageService, FolderService) => {
            //given
            const ctrl = createController();
            const sortType = stateMock.inventory.sort;

            spyOn(FolderService, 'getContent');

            //when
            ctrl.updateSortBy(sortType);

            //then
            expect(StateService.setDatasetsSort).not.toHaveBeenCalled();
            expect(StorageService.setDatasetsSort).not.toHaveBeenCalled();
            expect(FolderService.getContent).not.toHaveBeenCalled();
        }));
    });

    describe('sort order', () => {
        it('should set new sort order in state', inject(($q, StateService, FolderService) => {
            //given
            const ctrl = createController();
            const sortOrder = orderList[1];

            spyOn(FolderService, 'getContent').and.returnValue($q.when());
            expect(StateService.setDatasetsOrder).not.toHaveBeenCalled();

            //when
            ctrl.updateSortOrder(sortOrder);

            //then
            expect(StateService.setDatasetsOrder).toHaveBeenCalledWith(sortOrder);
        }));

        it('should store new sort order in storage', inject(($q, StorageService, FolderService) => {
            //given
            const ctrl = createController();
            const sortOrder = orderList[1];

            spyOn(FolderService, 'getContent').and.returnValue($q.when());
            expect(StorageService.setDatasetsOrder).not.toHaveBeenCalled();

            //when
            ctrl.updateSortOrder(sortOrder);

            //then
            expect(StorageService.setDatasetsOrder).toHaveBeenCalledWith(sortOrder.id);
        }));

        it('should refresh folder content', inject(($q, FolderService) => {
            //given
            const ctrl = createController();
            const sortOrder = orderList[1];

            spyOn(FolderService, 'getContent').and.returnValue($q.when());

            //when
            ctrl.updateSortOrder(sortOrder);

            //then
            expect(FolderService.getContent).toHaveBeenCalled();
        }));

        it('should set back old sort order in state when folder refresh fails', inject(($q, StateService, FolderService) => {
            //given
            const ctrl = createController();
            const sortOrder = orderList[1];
            const oldSortOrder = stateMock.inventory.order;

            spyOn(FolderService, 'getContent').and.returnValue($q.reject());
            expect(StateService.setDatasetsOrder).not.toHaveBeenCalledWith(oldSortOrder);

            //when
            ctrl.updateSortOrder(sortOrder);
            expect(StateService.setDatasetsOrder).not.toHaveBeenCalledWith(oldSortOrder);
            scope.$digest();

            //then
            expect(StateService.setDatasetsOrder).toHaveBeenCalledWith(oldSortOrder);
        }));

        it('should set back old sort order in storage when folder refresh fails', inject(($q, StorageService, FolderService) => {
            //given
            const ctrl = createController();
            const sortOrder = orderList[1];
            const oldSortOrder = stateMock.inventory.order;

            spyOn(FolderService, 'getContent').and.returnValue($q.reject());
            expect(StorageService.setDatasetsOrder).not.toHaveBeenCalledWith(oldSortOrder.id);

            //when
            ctrl.updateSortOrder(sortOrder);
            expect(StorageService.setDatasetsOrder).not.toHaveBeenCalledWith(oldSortOrder.id);
            scope.$digest();

            //then
            expect(StorageService.setDatasetsOrder).toHaveBeenCalledWith(oldSortOrder.id);
        }));

        it('should do nothing when sort order is already applied', inject((StateService, StorageService, FolderService) => {
            //given
            const ctrl = createController();
            const sortOrder = stateMock.inventory.order;

            spyOn(FolderService, 'getContent');

            //when
            ctrl.updateSortOrder(sortOrder);

            //then
            expect(StateService.setDatasetsOrder).not.toHaveBeenCalled();
            expect(StorageService.setDatasetsOrder).not.toHaveBeenCalled();
            expect(FolderService.getContent).not.toHaveBeenCalled();
        }));
    });
});
