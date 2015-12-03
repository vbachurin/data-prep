describe('Folder services', function () {
    'use strict';

    var preparations, datasets;

    datasets = [
        {
            'id': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
            'name': 'customers_jso_light',
            'author': 'anonymousUser',
            'records': 15,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 08:06'
        },
        {
            'id': '3b21388c-f54a-4334-9bef-748912d0806f',
            'name': 'customers_jso',
            'author': 'anonymousUser',
            'records': 1000,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 07:35'
        },
        {
            'id': '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7',
            'name': 'first_interactions',
            'author': 'anonymousUser',
            'records': 29379,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 08:05'
        },
        {
            'id': '5e95be9e-88cd-4765-9ecc-ee48cc28b6d5',
            'name': 'first_interactions_400',
            'author': 'anonymousUser',
            'records': 400,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 08:06'
        }
    ];

    preparations = [
        {
            'id': 'ab136cbf0923a7f11bea713adb74ecf919e05cfa',
            'dataSetId': datasets[0].id,
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
            'dataSetId': datasets[2].id,
            'author': 'anonymousUser',
            'creationDate': 1427447330693,
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
            'dataSetId': datasets[2].id,
            'author': 'anonymousUser',
            'creationDate': 1437487330692,
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
            'dataSetId': datasets[1].id,
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

    var datasetresult = {
        'id': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
        'name': 'customers_jso_light',
        'author': 'anonymousUser',
        'records': 15,
        'nbLinesHeader': 1,
        'nbLinesFooter': 0,
        'created': '03-30-2015 08:06',
        'defaultPreparation': {
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
        }
    };

    var stateMock;

    beforeEach(module('data-prep.services.folder', function ($provide) {
        stateMock = {folder: {
            currentFolder: {id : 'toto', path: 'toto', name: 'toto'},
            currentFolderContent: {
                datasets: [datasets[0]]
            }
        }};
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(function($q, FolderRestService, StateService) {
        spyOn(FolderRestService, 'create').and.returnValue($q.when());
        spyOn(FolderRestService, 'renameFolder').and.returnValue($q.when());
        spyOn(StateService, 'setFoldersStack').and.returnValue();
        spyOn(FolderRestService, 'childs').and.returnValue($q.when());


    }));

    it('should call rest childs with a path', inject(function ($rootScope, FolderService, FolderRestService) {
        //when
        FolderService.childs('/foo');
        $rootScope.$digest();

        //then
        expect(FolderRestService.childs).toHaveBeenCalledWith('/foo');
    }));

    it('should call rest childs w/o  path', inject(function ($rootScope, FolderService, FolderRestService) {
        //when
        FolderService.childs();
        $rootScope.$digest();

        //then
        expect(FolderRestService.childs).toHaveBeenCalled();
    }));

    it('should call rest create', inject(function ($rootScope, FolderService, FolderRestService) {
        //when
        FolderService.create('/foo');
        $rootScope.$digest();

        //then
        expect(FolderRestService.create).toHaveBeenCalledWith('/foo');
    }));

    it('should call rest renameFolder', inject(function ($rootScope, FolderService, FolderRestService) {
        //when
        FolderService.renameFolder('foo', 'beer');
        $rootScope.$digest();

        //then
        expect(FolderRestService.renameFolder).toHaveBeenCalledWith('foo', 'beer');
    }));


    //it('should build stack from folder id', inject(function ($rootScope, FolderService, StateService) {
    //    //when
    //    FolderService.buildStackFromId('1/2');

    //    //then
    //    expect(StateService.setFoldersStack).toHaveBeenCalledWith([{id:'', path:'', name: 'HOME_FOLDER'},{id : '1', path: '1', name: '1'},{id : '1/2', path: '1/2', name: '2'}]);
    //}));

    //it('should build stack from root folder id', inject(function ($rootScope, FolderService, StateService) {
    //    //when
    //    FolderService.buildStackFromId('');

    //    //then
    //    expect(StateService.setFoldersStack).toHaveBeenCalledWith([{id:'', path:'', name: 'HOME_FOLDER'}]);
    //}));


    it('should populateMenuChilds', inject(function ($q, $rootScope, FolderService, StateService, FolderRestService) {
        //Given
        var content ={data: {
            folders: [{id : 'toto', path: 'toto', name: 'toto'}]
        }};
        spyOn(FolderRestService, 'getFolderContent').and.returnValue($q.when(content));
        spyOn(StateService, 'setMenuChilds').and.returnValue();

        //when
        FolderService.populateMenuChilds({id : 'toto', path: 'toto', name: 'toto'});
        $rootScope.$digest();

        //then
        expect(FolderRestService.getFolderContent).toHaveBeenCalledWith({id : 'toto', path: 'toto', name: 'toto'});
        expect(StateService.setMenuChilds).toHaveBeenCalledWith([{id : 'toto', path: 'toto', name: 'toto'}]);
    }));

    it('should getFolderContent', inject(function ($q, $rootScope, FolderService, StateService, FolderRestService, DatasetListSortService, PreparationListService) {
        //Given
        var content ={data: {
            folders: [{id : 'toto', path: 'toto', name: 'toto'}],
            datasets: [datasets[0]]
        }};

        spyOn(DatasetListSortService, 'getSort').and.returnValue('name');
        spyOn(DatasetListSortService, 'getOrder').and.returnValue('asc');

        spyOn(FolderRestService, 'getFolderContent').and.returnValue($q.when(content));
        spyOn(StateService, 'setCurrentFolder').and.returnValue();
        spyOn(StateService, 'setCurrentFolderContent').and.returnValue();
        spyOn(PreparationListService, 'getPreparationsPromise').and.returnValue($q.when(preparations));

        //when
        FolderService.getFolderContent({id : '1/2', path: 'toto', name: 'toto'});
        $rootScope.$digest();

        //then
        expect(FolderRestService.getFolderContent).toHaveBeenCalledWith({id : '1/2', path: 'toto', name: 'toto'}, 'name', 'asc');
        expect(StateService.setCurrentFolder).toHaveBeenCalledWith({id : '1/2', path: 'toto', name: 'toto'});
        expect(StateService.setCurrentFolderContent).toHaveBeenCalledWith({
            folders: [{id : 'toto', path: 'toto', name: 'toto'}],
            datasets: [datasetresult]
        });
        expect(StateService.setFoldersStack).toHaveBeenCalledWith([
            {id:'', path:'', name: 'HOME_FOLDER'},
            {id : '1', path: '1', name: '1'},
            {id : '1/2', path: '1/2', name: '2'}
        ]);
    }));

    it('should getFolderContent for root folder', inject(function ($q, $rootScope, FolderService, StateService, FolderRestService, DatasetListSortService, PreparationListService) {
        //Given
        var content ={data: {
            folders: [{id : 'toto', path: 'toto', name: 'toto'}],
            datasets: [datasets[0]]
        }};

        spyOn(DatasetListSortService, 'getSort').and.returnValue('name');
        spyOn(DatasetListSortService, 'getOrder').and.returnValue('asc');

        spyOn(FolderRestService, 'getFolderContent').and.returnValue($q.when(content));
        spyOn(StateService, 'setCurrentFolder').and.returnValue();
        spyOn(StateService, 'setCurrentFolderContent').and.returnValue();
        spyOn(PreparationListService, 'getPreparationsPromise').and.returnValue($q.when(preparations));

        //when
        FolderService.getFolderContent();
        $rootScope.$digest();

        //then
        expect(FolderRestService.getFolderContent).toHaveBeenCalledWith(undefined, 'name', 'asc');
        expect(StateService.setCurrentFolder).toHaveBeenCalledWith({id:'', path:'', name: 'HOME_FOLDER'});
        expect(StateService.setCurrentFolderContent).toHaveBeenCalledWith({
            folders: [{id : 'toto', path: 'toto', name: 'toto'}],
            datasets: [datasetresult]
        });
        expect(StateService.setFoldersStack).toHaveBeenCalledWith([{id:'', path:'', name: 'HOME_FOLDER'}]);
    }));


    it('should getFolderContent for root folder with defaultPreparation null', inject(function ($q, $rootScope, FolderService, StateService, FolderRestService, DatasetListSortService, PreparationListService) {
        //Given
        var datasetPreparationNull = {
            'id': 'de3cc32a-b624-484e-xxxx-dab9061a009c',
            'name': 'customers_jso_light',
            'author': 'anonymousUser',
            'records': 15,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 08:06'
        };

        var datasetResultPreparationNull = {
            'id': 'de3cc32a-b624-484e-xxxx-dab9061a009c',
            'name': 'customers_jso_light',
            'author': 'anonymousUser',
            'records': 15,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 08:06',
            defaultPreparation: null
        };

        var content ={data: {
            folders: [{id : 'toto', path: 'toto', name: 'toto'}],
            datasets: [datasetPreparationNull]
        }};

        spyOn(DatasetListSortService, 'getSort').and.returnValue('name');
        spyOn(DatasetListSortService, 'getOrder').and.returnValue('asc');

        spyOn(FolderRestService, 'getFolderContent').and.returnValue($q.when(content));
        spyOn(StateService, 'setCurrentFolder').and.returnValue();
        spyOn(StateService, 'setCurrentFolderContent').and.returnValue();
        spyOn(PreparationListService, 'getPreparationsPromise').and.returnValue($q.when(preparations));

        //when
        FolderService.getFolderContent();
        $rootScope.$digest();

        //then
        expect(FolderRestService.getFolderContent).toHaveBeenCalledWith(undefined, 'name', 'asc');
        expect(StateService.setCurrentFolder).toHaveBeenCalledWith({id:'', path:'', name: 'HOME_FOLDER'});
        expect(StateService.setCurrentFolderContent).toHaveBeenCalledWith({
            folders: [{id : 'toto', path: 'toto', name: 'toto'}],
            datasets: [datasetResultPreparationNull]
        });
        expect(StateService.setFoldersStack).toHaveBeenCalledWith([{id:'', path:'', name: 'HOME_FOLDER'}]);
    }));

    it('should refreshDefaultPreparationForCurrentFolder ', inject(function ($q, $rootScope, FolderService) {

        //when
        FolderService.refreshDefaultPreparationForCurrentFolder(preparations);

        //then
        expect(stateMock.folder.currentFolderContent.datasets[0].defaultPreparation.id).toBe(datasetresult.defaultPreparation.id);
    }));


    it('should refreshDefaultPreparationForCurrentFolder with default preparation null', inject(function ($q, $rootScope, FolderService) {

        //Given
        var datasetPreparationNull = {
            'id': 'de3cc32a-b624-484e-xxxx-dab9061a009c',
            'name': 'customers_jso_light',
            'author': 'anonymousUser',
            'records': 15,
            'nbLinesHeader': 1,
            'nbLinesFooter': 0,
            'created': '03-30-2015 08:06'
        };

        stateMock.folder.currentFolderContent.datasets = [datasetPreparationNull];

        //when
        FolderService.refreshDefaultPreparationForCurrentFolder(preparations);

        //then
        expect(stateMock.folder.currentFolderContent.datasets[0].defaultPreparation).toBe(null);
    }));

});
