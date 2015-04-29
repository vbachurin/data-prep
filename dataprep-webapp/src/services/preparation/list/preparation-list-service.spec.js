describe('Preparation list service controller', function() {
    'use strict';

    var allDatasets = [
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

    var allPreparations = [
        {
            'id': 'ab136cbf0923a7f11bea713adb74ecf919e05cfa',
            'dataSetId': allDatasets[0].id,
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
            'dataSetId': allDatasets[2].id,
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
            'dataSetId': allDatasets[2].id,
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
            'id': '8v4z38u4n3ioç43f815c3w5v4by3h8u4w3fv4bgyds',
            'dataSetId': allDatasets[1].id,
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

    beforeEach(module('data-prep.services.preparation'));

    beforeEach(inject(function($q, PreparationService, DatasetListService) {
        DatasetListService.datasets = allDatasets;

        spyOn(PreparationService, 'getPreparations').and.returnValue($q.when({data: allPreparations}));
    }));

    it('should init preparations', inject(function($rootScope, PreparationListService) {
        //given
        expect(PreparationListService.preparations).toBeNull();

        //when
        PreparationListService.refreshPreparations();
        $rootScope.$digest();

        //then
        expect(PreparationListService.preparations).toBe(allPreparations);
        expect(PreparationListService.preparations[0].dataset).toBe(allDatasets[0]);
        expect(PreparationListService.preparations[1].dataset).toBe(allDatasets[2]);
    }));

    it('refresh preparation should set default preparation', inject(function($rootScope, PreparationListService) {
        //given
        expect(PreparationListService.preparations).toBeNull();

        //when
        PreparationListService.refreshPreparations();
        $rootScope.$digest();

        //then
        expect(allDatasets[0].defaultPreparation.id).toBe(allPreparations[0].id);
        expect(allDatasets[1].defaultPreparation.id).toBe(allPreparations[3].id);
        expect(allDatasets[2].defaultPreparation).toBeNull();
    }));


    it('should refresh preparations if preparation list is empty', inject(function($rootScope, PreparationListService, PreparationService) {
        //given
        expect(PreparationListService.preparations).toBeNull();

        //when
        PreparationListService.getPreparationsPromise();
        $rootScope.$digest();

        //then
        expect(PreparationService.getPreparations).toHaveBeenCalled();
        expect(PreparationListService.preparations).toBe(allPreparations);
        expect(PreparationListService.preparations[0].dataset).toBe(allDatasets[0]);
        expect(PreparationListService.preparations[1].dataset).toBe(allDatasets[2]);
    }));

    it('should just return existing preparations list if not empty', inject(function($rootScope, PreparationListService, PreparationService) {
        //given
        var returnedPreps = null;
        PreparationListService.preparations = allPreparations;

        //when
        PreparationListService.getPreparationsPromise()
            .then(function(preparations) {
                returnedPreps = preparations;
            });
        $rootScope.$digest();

        //then
        expect(PreparationService.getPreparations).not.toHaveBeenCalled();
        expect(returnedPreps).toBe(allPreparations);
    }));

});
