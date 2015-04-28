describe('Preparation Service', function () {
    'use strict';

    var $httpBackend;
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
            'dataSetId': '8ec053b1-7870-4bc6-af54-523be91dc774',
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

    var records = {
        'records': [{
            'firstname': 'Grover',
            'avgAmount': '82.4',
            'city': 'BOSTON',
            'birth': '01-09-1973',
            'registration': '17-02-2008',
            'id': '1',
            'state': 'AR',
            'nbCommands': '41',
            'lastname': 'Quincy'
        }, {
            'firstname': 'Warren',
            'avgAmount': '87.6',
            'city': 'NASHVILLE',
            'birth': '11-02-1960',
            'registration': '18-08-2007',
            'id': '2',
            'state': 'WA',
            'nbCommands': '17',
            'lastname': 'Johnson'
        }]
    };

    beforeEach(module('data-prep.services.preparation'));

    beforeEach(inject(function ($injector) {
        $httpBackend = $injector.get('$httpBackend');
    }));

    it('should get all preparations', inject(function($rootScope, RestURLs, PreparationService) {
        //given
        var preparations = null;
        $httpBackend
            .expectGET(RestURLs.preparationUrl)
            .respond(200, allPreparations);

        //when
        PreparationService.getPreparations()
            .then(function(response) {
                preparations = response.data;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(preparations).toEqual(allPreparations);
    }));

    it('should create a new preparation and set current preparation id', inject(function($rootScope, RestURLs, PreparationService) {
        //given
        var datasetId = '8ec053b1-7870-4bc6-af54-523be91dc774';
        var name = 'The new preparation';

        $httpBackend
            .expectPOST(RestURLs.preparationUrl, {dataSetId: datasetId, name: name})
            .respond(200, 'fbaa18e82e913e97e5f0e9d40f04413412be1126');
        expect(PreparationService.currentPreparation).toBeFalsy();

        //when
        PreparationService.create(datasetId, name);
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(PreparationService.currentPreparation).toBe('fbaa18e82e913e97e5f0e9d40f04413412be1126');
    }));

    it('should update preparation name', inject(function($rootScope, RestURLs, PreparationService) {
        //given
        var updateDone = false;
        var name = 'The new preparation name';
        PreparationService.currentPreparation = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        $httpBackend
            .expectPUT(RestURLs.preparationUrl + '/fbaa18e82e913e97e5f0e9d40f04413412be1126', {name: name})
            .respond(200);

        //when
        PreparationService.update(name)
            .then(function() {
                updateDone = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(updateDone).toBe(true);
    }));

    it('should create a new preparation and append a transformation step', inject(function($rootScope, RestURLs, PreparationService) {
        //given
        var done = false;
        var datasetId = '8ec053b1-7870-4bc6-af54-523be91dc774';
        var action = 'fillemptywithdefault';
        var parameters = {
            'default_value': 'N/A',
            'column_name': 'state'
        };
        expect(PreparationService.currentPreparation).toBeFalsy();

        //given : preparation creation request
        $httpBackend
            .expectPOST(RestURLs.preparationUrl, {dataSetId: datasetId, name: 'New preparation'})
            .respond(200, 'fbaa18e82e913e97e5f0e9d40f04413412be1126');
        //given : preparation step append request
        $httpBackend
            .expectPOST(RestURLs.preparationUrl + '/fbaa18e82e913e97e5f0e9d40f04413412be1126/actions', {
                actions: [{
                    action: action,
                    parameters: parameters
                }]
            })
            .respond(200);


        //when
        PreparationService.appendStep(datasetId, action, parameters)
            .then(function() {
                done = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(PreparationService.currentPreparation).toBe('fbaa18e82e913e97e5f0e9d40f04413412be1126');
        expect(done).toBe(true);
    }));

    it('should append a transformation step in the current preparation', inject(function($rootScope, RestURLs, PreparationService) {
        //given
        var done = false;
        var datasetId = '8ec053b1-7870-4bc6-af54-523be91dc774';
        var action = 'fillemptywithdefault';
        var parameters = {
            'default_value': 'N/A',
            'column_name': 'state'
        };

        PreparationService.currentPreparation = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        //given : preparation step append request
        $httpBackend
            .expectPOST(RestURLs.preparationUrl + '/fbaa18e82e913e97e5f0e9d40f04413412be1126/actions', {
                actions: [{
                    action: action,
                    parameters: parameters
                }]
            })
            .respond(200);


        //when
        PreparationService.appendStep(datasetId, action, parameters)
            .then(function() {
                done = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(done).toBe(true);
    }));

    it('should get the requested version of preparation content', inject(function($rootScope, RestURLs, PreparationService) {
        //given
        var content = null;
        PreparationService.currentPreparation = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';
        $httpBackend
            .expectGET(RestURLs.preparationUrl + '/fbaa18e82e913e97e5f0e9d40f04413412be1126/content?version=head')
            .respond(200, records);

        //when
        PreparationService.getContent('head')
            .then(function(response) {
                content = response.data;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(content).toEqual(records);
    }));

    it('should get the current preparation details', inject(function($rootScope, RestURLs, PreparationService) {
        //given
        var details = null;
        PreparationService.currentPreparation = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';
        $httpBackend
            .expectGET(RestURLs.preparationUrl + '/fbaa18e82e913e97e5f0e9d40f04413412be1126/details')
            .respond(200, allPreparations[1]);

        //when
        PreparationService.getDetails()
            .then(function(response) {
                details = response.data;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(details).toEqual(allPreparations[1]);
    }));

    it('should delete the provided preparation', inject(function($rootScope, RestURLs, PreparationService) {
        //given
        var deleted = false;
        var preparation = {id: 'fbaa18e82e913e97e5f0e9d40f04413412be1126'};
        $httpBackend
            .expectDELETE(RestURLs.preparationUrl + '/fbaa18e82e913e97e5f0e9d40f04413412be1126')
            .respond(200);

        //when
        PreparationService.delete(preparation)
            .then(function() {
                deleted = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(deleted).toBe(true);
    }));

    it('should update a transformation step in the current preparation', inject(function($rootScope, RestURLs, PreparationService) {
        //given
        var done = false;
        var action = 'fillemptywithdefault';
        var parameters = {
            'default_value': 'N/A',
            'column_name': 'state'
        };
        var stepId = '18046df82f0946af05ee766d0ac06f92f63e7047';

        PreparationService.currentPreparation = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        //given : preparation step update request
        $httpBackend
            .expectPUT(RestURLs.preparationUrl + '/fbaa18e82e913e97e5f0e9d40f04413412be1126/actions/18046df82f0946af05ee766d0ac06f92f63e7047', {
                actions: [{
                    action: action,
                    parameters: parameters
                }]
            })
            .respond(200);


        //when
        PreparationService.updateStep(stepId, action, parameters)
            .then(function() {
                done = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(done).toBe(true);
    }));
});