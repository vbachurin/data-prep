    /*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Folder services', function () {
    'use strict';

    var stateMock, preparations;

    var sortList = [
        {id: 'name', name: 'NAME_SORT', property: 'name'},
        {id: 'date', name: 'DATE_SORT', property: 'created'}
    ];

    var orderList = [
        {id: 'asc', name: 'ASC_ORDER'},
        {id: 'desc', name: 'DESC_ORDER'}
    ];

    beforeEach(angular.mock.module('data-prep.services.folder', function ($provide) {
        stateMock = {
            inventory: {
                datasets: [],
                sortList: sortList,
                orderList: orderList,
                sort: sortList[0],
                order: orderList[0],
                currentFolderContent: {}
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(angular.mock.module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'HOME_FOLDER': 'Home'
        });
        $translateProvider.preferredLanguage('en');
    }));

    beforeEach(inject(function ($q, FolderRestService) {
        preparations = [
            {
                'id': 'ab136cbf0923a7f11bea713adb74ecf919e05cfa',
                'dataSetId': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
                'author': 'anonymousUser',
                'creationDate': 1427447300300,
                'steps': [
                    '35890aabcf9115e4309d4ce93367bf5e4e77b82a',
                    '4ff5d9a6ca2e75ebe3579740a4297fbdb9b7894f',
                    '8a1c49d1b64270482e8db8232357c6815615b7cf',
                    '599725f0e1331d5f8aae24f22cd1ec768b10348d'
                ],
                'actions': [
                    {
                        'action': 'lowercase',
                        'parameters': {
                            'column_name': 'birth'
                        }
                    },
                    {
                        'action': 'uppercase',
                        'parameters': {
                            'column_name': 'country'
                        }
                    },
                    {
                        'action': 'cut',
                        'parameters': {
                            'pattern': '.',
                            'column_name': 'first_item'
                        }
                    }
                ]
            },
            {
                'id': 'fbaa18e82e913e97e5f0e9d40f04413412be1126',
                'dataSetId': '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7',
                'author': 'anonymousUser',
                'lastModificationDate': 1427447330693,
                'steps': [
                    '47e2444dd1301120b539804507fd307072294048',
                    'ae1aebf4b3fa9b983c895486612c02c766305410',
                    '24dcd68f2117b9f93662cb58cc31bf36d6e2867a',
                    '599725f0e1331d5f8aae24f22cd1ec768b10348d'
                ],
                'actions': [
                    {
                        'action': 'cut',
                        'parameters': {
                            'pattern': '-',
                            'column_name': 'birth'
                        }
                    },
                    {
                        'action': 'fillemptywithdefault',
                        'parameters': {
                            'default_value': 'N/A',
                            'column_name': 'state'
                        }
                    },
                    {
                        'action': 'uppercase',
                        'parameters': {
                            'column_name': 'lastname'
                        }
                    }
                ]
            },
            {
                'id': 'ds3f51sf3q1df35qsf412qdsf15ds3ff454qg8r4qr',
                'dataSetId': '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7',
                'author': 'anonymousUser',
                'lastModificationDate': 1437487330692,
                'steps': [
                    '87e38cv438dth4yd6k84x3dr84htryj84xc3k21u'
                ],
                'actions': [
                    {
                        'action': 'cut',
                        'parameters': {
                            'pattern': '-',
                            'column_name': 'birth'
                        }
                    }
                ]
            },
            {
                'id': 'jghjjghjgh',
                'dataSetId': '3b21388c-f54a-4334-9bef-748912d0806f',
                'author': 'anonymousUser',
                'creationDate': 1437497330694,
                'steps': [
                    '3w8xt4hxt3fh125ydx8y6j4i8l4ds358g4zfbe3e'
                ],
                'actions': [
                    {
                        'action': 'cut',
                        'parameters': {
                            'pattern': '-',
                            'column_name': 'birth'
                        }
                    }
                ]
            }
        ];

        spyOn(FolderRestService, 'create').and.returnValue($q.when());
        spyOn(FolderRestService, 'children').and.returnValue($q.when());
        spyOn(FolderRestService, 'search').and.returnValue($q.when());
        spyOn(FolderRestService, 'rename').and.returnValue($q.when());
        spyOn(FolderRestService, 'remove').and.returnValue($q.when());
    }));

    describe('simple REST calls', function () {
        it('should call rest children', inject(function ($rootScope, FolderService, FolderRestService) {
            //when
            FolderService.children('/foo');

            //then
            expect(FolderRestService.children).toHaveBeenCalledWith('/foo');
        }));

        it('should call rest create', inject(function ($rootScope, FolderService, FolderRestService) {
            //when
            FolderService.create('/foo');

            //then
            expect(FolderRestService.create).toHaveBeenCalledWith('/foo');
        }));

        it('should call rest rename', inject(function ($rootScope, FolderService, FolderRestService) {
            //when
            FolderService.rename('foo', 'beer');

            //then
            expect(FolderRestService.rename).toHaveBeenCalledWith('foo', 'beer');
        }));

        it('should call rest remove', inject(function ($rootScope, FolderService, FolderRestService) {
            //when
            FolderService.remove('foo');

            //then
            expect(FolderRestService.remove).toHaveBeenCalledWith('foo');
        }));

        it('should call rest search', inject(function ($rootScope, FolderService, FolderRestService) {
            //when
            FolderService.search('path');

            //then
            expect(FolderRestService.search).toHaveBeenCalledWith('path');
        }));
    });

    describe('content', function () {
        var content;
        beforeEach(inject(function ($q, StateService, FolderRestService) {
            content = {
                data: {
                    folders: [{path: 'toto', name: 'toto'}],
                    datasets: [
                        {
                            'id': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
                            'name': 'customers_jso_light',
                            'author': 'anonymousUser',
                            'records': 15,
                            'nbLinesHeader': 1,
                            'nbLinesFooter': 0,
                            'created': '03-30-2015 08:06'
                        }]
                }
            };
            spyOn(FolderRestService, 'getContent').and.returnValue($q.when(content));
            spyOn(StateService, 'setFoldersStack').and.returnValue();
            spyOn(StateService, 'setCurrentFolder').and.returnValue();
            spyOn(StateService, 'setCurrentFolderContent').and.returnValue();
        }));

        it('should get folder content', inject(function ($rootScope, FolderService, FolderRestService) {
            //when
            FolderService.getContent({path: 'toto', name: 'toto'});
            $rootScope.$digest();

            //then
            expect(FolderRestService.getContent).toHaveBeenCalledWith('toto', 'name', 'asc');
        }));

        it('should get content for root folder when there is no provided folder', inject(function ($rootScope, FolderService, FolderRestService) {
            //when
            FolderService.getContent();
            $rootScope.$digest();

            //then
            expect(FolderRestService.getContent).toHaveBeenCalledWith(undefined, 'name', 'asc');
        }));

        it('should populate state with folder content infos', inject(function ($rootScope, FolderService, StateService) {
            //when
            FolderService.getContent({path: 'titi/toto', name: 'toto'});
            $rootScope.$digest();

            //then
            expect(StateService.setCurrentFolder).toHaveBeenCalledWith({path: 'titi/toto', name: 'toto'});
            expect(StateService.setCurrentFolderContent).toHaveBeenCalledWith(content.data);
            expect(StateService.setFoldersStack).toHaveBeenCalledWith([
                {path: '', name: 'Home'},
                {path: 'titi', name: 'titi'},
                {path: 'titi/toto', name: 'toto'}
            ]);
        }));

        it('should refresh sort parameters', inject(function ($timeout, StorageService, StateService, FolderService) {
            //given
            spyOn(StorageService, 'getDatasetsSort').and.returnValue('date');
            spyOn(StateService, 'setDatasetsSort');

            //when
            FolderService.refreshDatasetsSort();

            //then
            expect(StateService.setDatasetsSort).toHaveBeenCalledWith({id: 'date', name: 'DATE_SORT', property: 'created'});
        }));

        it('should refresh sort order parameters', inject(function ($timeout, StorageService, StateService, FolderService) {
            //given
            spyOn(StorageService, 'getDatasetsOrder').and.returnValue('desc');
            spyOn(StateService, 'setDatasetsOrder');

            //when
            FolderService.refreshDatasetsOrder();

            //then
            expect(StateService.setDatasetsOrder).toHaveBeenCalledWith({id: 'desc', name: 'DESC_ORDER'});
        }));


    });

    describe('populate menu children', function () {
        beforeEach(inject(function ($q, StateService, FolderRestService) {
            var content = {
                data: {
                    folders: [{path: 'toto', name: 'toto'}]
                }
            };
            spyOn(FolderRestService, 'getContent').and.returnValue($q.when(content));
            spyOn(StateService, 'setMenuChildren').and.returnValue();
        }));

        it('should call populateMenuChildren REST service', inject(function (FolderService, FolderRestService) {
            //when
            FolderService.populateMenuChildren({path: 'toto', name: 'toto'});

            //then
            expect(FolderRestService.getContent).toHaveBeenCalledWith('toto');
        }));

        it('should call populateMenuChildren REST service without provided folder', inject(function (FolderService, FolderRestService) {
            //when
            FolderService.populateMenuChildren();

            //then
            expect(FolderRestService.getContent).toHaveBeenCalledWith(undefined);
        }));

        it('should set menu children state', inject(function ($rootScope, FolderService, StateService) {
            //when
            FolderService.populateMenuChildren({path: 'toto', name: 'toto'});
            $rootScope.$digest();

            //then
            expect(StateService.setMenuChildren).toHaveBeenCalledWith([{path: 'toto', name: 'toto'}]);
        }));
    });
});
