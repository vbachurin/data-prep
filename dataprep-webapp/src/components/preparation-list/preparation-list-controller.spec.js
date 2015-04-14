describe('Preparation list controller', function() {
    'use strict';

    var createController, scope;
    var allPreparations = [
        {
            'id': 'ab136cbf0923a7f11bea713adb74ecf919e05cfa',
            'dataSetId': 'ddb74c89-6d23-4528-9f37-7a9860bb468e',
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
        }
    ];

    beforeEach(module('data-prep.preparation-list'));

    beforeEach(inject(function($q, $rootScope, $controller, PreparationService, PreparationListService, PlaygroundService, DatasetListService, MessageService) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('PreparationListCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn(DatasetListService, 'getDatasetsPromise').and.returnValue($q.when([]));
        spyOn(PreparationService, 'getPreparations').and.returnValue($q.when({data: allPreparations}));
        spyOn(PreparationService, 'delete').and.returnValue($q.when(true));
        spyOn(PreparationListService, 'refreshPreparations').and.callThrough();
        spyOn(PlaygroundService, 'load').and.returnValue($q.when(true));
        spyOn(PlaygroundService, 'show').and.callThrough();
        spyOn(MessageService, 'success').and.returnValue(null);
        spyOn(MessageService, 'error').and.returnValue(null);
    }));

    afterEach(inject(function($stateParams) {
        $stateParams.prepid = null;
    }));

    it('should init preparations', inject(function() {
        //given

        //when
        var ctrl = createController();
        scope.$digest();

        //then
        expect(ctrl.preparations).toBe(allPreparations);
    }));

    it('should load preparation if requested in url', inject(function($stateParams, PlaygroundService) {
        //given
        $stateParams.prepid = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        //when
        createController();
        scope.$digest();

        //then
        expect(PlaygroundService.load).toHaveBeenCalledWith(allPreparations[1]);
        expect(PlaygroundService.show).toHaveBeenCalled();
    }));

    it('should show error message if requested preparation is not in preparation list', inject(function($stateParams, PlaygroundService, MessageService) {
        //given
        $stateParams.prepid = 'azerty';

        //when
        createController();
        scope.$digest();

        //then
        expect(PlaygroundService.load).not.toHaveBeenCalled();
        expect(PlaygroundService.show).not.toHaveBeenCalled();
        expect(MessageService.error).toHaveBeenCalledWith('PLAYGROUND_FILE_NOT_FOUND_TITLE', 'PLAYGROUND_FILE_NOT_FOUND', {type: 'preparation'});
    }));

    it('should load preparation and show playground', inject(function(PlaygroundService) {
        //given
        var ctrl = createController();
        var preparation = {
            id: 'de618c62ef97b3a95b5c171bc077ffe22e1d6f79',
            dataSetId: 'dacd45cf-5bd0-4768-a9b7-f6c199581efc',
            author: 'anonymousUser',
            creationDate: 1427460984585,
            steps: [
                '228c16230de53de5992eb44c7aba362ac714ab1c'
            ],
            actions: []
        };
        expect(PlaygroundService.load).not.toHaveBeenCalled();
           
        //when
        ctrl.load(preparation);
        scope.$digest();

        //then
        expect(PlaygroundService.load).toHaveBeenCalledWith(preparation);
        expect(PlaygroundService.show).toHaveBeenCalled();
    }));

    it('should delete preparation, show success message, and refresh list on confirm', inject(function($q, TalendConfirmService, PreparationService, MessageService,PreparationListService) {
        //given
        spyOn(TalendConfirmService, 'confirm').and.returnValue($q.when(true));

        var ctrl = createController();
        var preparation = {
            id: 'de618c62ef97b3a95b5c171bc077ffe22e1d6f79',
            name: 'my preparation'
        };
        expect(PreparationListService.refreshPreparations.calls.count()).toBe(1);

        //when
        ctrl.delete(preparation);
        scope.$digest();

        //then
        expect(TalendConfirmService.confirm).toHaveBeenCalledWith({disableEnter: true}, ['DELETE_PERMANENTLY', 'NO_UNDONE_CONFIRM'], {type:'preparation', name: preparation.name});
        expect(PreparationService.delete).toHaveBeenCalledWith(preparation);
        expect(MessageService.success).toHaveBeenCalledWith('REMOVE_SUCCESS_TITLE', 'REMOVE_SUCCESS', {type:'preparation', name: preparation.name});
        expect(PreparationListService.refreshPreparations.calls.count()).toBe(2);
    }));

    it('should do nothing on delete dismiss', inject(function($q, TalendConfirmService, PreparationService, MessageService,PreparationListService) {
        //given
        spyOn(TalendConfirmService, 'confirm').and.returnValue($q.reject(null));

        var ctrl = createController();
        var preparation = {
            id: 'de618c62ef97b3a95b5c171bc077ffe22e1d6f79',
            name: 'my preparation'
        };
        expect(PreparationListService.refreshPreparations.calls.count()).toBe(1);

        //when
        ctrl.delete(preparation);
        scope.$digest();

        //then
        expect(TalendConfirmService.confirm).toHaveBeenCalledWith({disableEnter: true}, ['DELETE_PERMANENTLY', 'NO_UNDONE_CONFIRM'], {type:'preparation', name: preparation.name});
        expect(PreparationService.delete).not.toHaveBeenCalled();
        expect(MessageService.success).not.toHaveBeenCalled();
        expect(PreparationListService.refreshPreparations.calls.count()).toBe(1);
    }));
});
