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

    it('should get all preparations', inject(function($rootScope, RestURLs, PreparationRestService) {
        //given
        var preparations = null;
        $httpBackend
            .expectGET(RestURLs.preparationUrl)
            .respond(200, allPreparations);

        //when
        PreparationRestService.getPreparations()
            .then(function(response) {
                preparations = response.data;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(preparations).toEqual(allPreparations);
    }));

    it('should create a new preparation', inject(function($rootScope, RestURLs, PreparationRestService) {
        //given
        var datasetId = '8ec053b1-7870-4bc6-af54-523be91dc774';
        var name = 'The new preparation';

        $httpBackend
            .expectPOST(RestURLs.preparationUrl, {dataSetId: datasetId, name: name})
            .respond(200, 'fbaa18e82e913e97e5f0e9d40f04413412be1126');
        expect(PreparationRestService.currentPreparation).toBeFalsy();

        //when
        PreparationRestService.create(datasetId, name);
        $httpBackend.flush();
        $rootScope.$digest();

        //then

    }));

    it('should update preparation name', inject(function($rootScope, RestURLs, PreparationRestService) {
        //given
        var updateDone = false;
        var name = 'The new preparation name';
        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        $httpBackend
            .expectPUT(RestURLs.preparationUrl + '/fbaa18e82e913e97e5f0e9d40f04413412be1126', {name: name})
            .respond(200);

        //when
        PreparationRestService.update(preparationId, name)
            .then(function() {
                updateDone = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(updateDone).toBe(true);
    }));

    it('should append a transformation step in the current preparation', inject(function($rootScope, RestURLs, PreparationRestService) {
        //given
        var done = false;
        var action = 'fillemptywithdefault';
        var parameters = {
            'default_value': 'N/A',
            'column_name': 'state'
        };

        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        //given : preparation step append request
        $httpBackend
            .expectPOST(RestURLs.preparationUrl + '/' + preparationId + '/actions', {
                actions: [{
                    action: action,
                    parameters: parameters
                }]
            })
            .respond(200);


        //when
        PreparationRestService.appendStep(preparationId, action, parameters)
            .then(function() {
                done = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(done).toBe(true);
    }));

    it('should get the requested version of preparation content', inject(function($rootScope, RestURLs, PreparationRestService) {
        //given
        var content = null;
        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';
        $httpBackend
            .expectGET(RestURLs.preparationUrl + '/' + preparationId + '/content?version=head')
            .respond(200, records);

        //when
        PreparationRestService.getContent(preparationId, 'head')
            .then(function(response) {
                content = response.data;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(content).toEqual(records);
    }));

    it('should get the requested version of preparation content with the given sample size', inject(function($rootScope, RestURLs, PreparationRestService) {
        //given
        var content = null;
        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';
        $httpBackend
            .expectGET(RestURLs.preparationUrl + '/' + preparationId + '/content?version=head&sample=50')
            .respond(200, records);

        //when
        PreparationRestService.getContent(preparationId, 'head', 50)
            .then(function(response) {
                content = response.data;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(content).toEqual(records);
    }));

    it('should get the current preparation details', inject(function($rootScope, RestURLs, PreparationRestService) {
        //given
        var details = null;
        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';
        $httpBackend
            .expectGET(RestURLs.preparationUrl + '/' + preparationId + '/details')
            .respond(200, allPreparations[1]);

        //when
        PreparationRestService.getDetails(preparationId)
            .then(function(response) {
                details = response.data;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(details).toEqual(allPreparations[1]);
    }));

    it('should delete the provided preparation', inject(function($rootScope, RestURLs, PreparationRestService) {
        //given
        var deleted = false;
        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';
        $httpBackend
            .expectDELETE(RestURLs.preparationUrl + '/' + preparationId)
            .respond(200);

        //when
        PreparationRestService.delete(preparationId)
            .then(function() {
                deleted = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(deleted).toBe(true);
    }));

    it('should update a transformation step in the current preparation', inject(function($rootScope, RestURLs, PreparationRestService) {
        //given
        var done = false;
        var action = 'fillemptywithdefault';
        var parameters = {
            'default_value': 'N/A',
            'column_name': 'state'
        };
        var stepId = '18046df82f0946af05ee766d0ac06f92f63e7047';

        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        //given : preparation step update request
        $httpBackend
            .expectPUT(RestURLs.preparationUrl + '/' + preparationId + '/actions/' + stepId, {
                actions: [{
                    action: action,
                    parameters: parameters
                }]
            })
            .respond(200);


        //when
        PreparationRestService.updateStep(preparationId, stepId, action, parameters)
            .then(function() {
                done = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(done).toBe(true);
    }));

    it('should call preview POST request', inject(function($rootScope, $q, RestURLs, PreparationRestService) {
        //given
        var done = false;
        var canceled = false;
        var currentStep = {transformation: {stepId: '18046df82f0946af05ee766d0ac06f92f63e7047'}};
        var previewStep = {transformation: {stepId: '856980bacf0890c89bc318856980bacf0890c89b'}};
        var recordsTdpId = [1,2,3,4,5];
        var canceler = $q.defer();

        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        var expectedParams = {
            tdpIds: [1,2,3,4,5],
            currentStepId: '18046df82f0946af05ee766d0ac06f92f63e7047',
            previewStepId: '856980bacf0890c89bc318856980bacf0890c89b',
            preparationId: 'fbaa18e82e913e97e5f0e9d40f04413412be1126'
        };

        $httpBackend
            .expectPOST(RestURLs.previewUrl + '/diff', expectedParams)
            .respond(200);


        //when
        PreparationRestService.getPreviewDiff(preparationId, currentStep, previewStep, recordsTdpId, canceler)
            .then(function() {
                done = true;
            })
            .catch(function() {
                canceled = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(done).toBe(true);
        expect(canceled).toBe(false);
    }));

    it('should cancel POST preview by resolving the given promise', inject(function($rootScope, $q, RestURLs, PreparationRestService) {
        //given
        var done = false;
        var canceled = false;
        var currentStep = {transformation: {stepId: '18046df82f0946af05ee766d0ac06f92f63e7047'}};
        var previewStep = {transformation: {stepId: '856980bacf0890c89bc318856980bacf0890c89b'}};
        var recordsTdpId = [1,2,3,4,5];
        var canceler = $q.defer();

        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        var expectedParams = {
            tdpIds: [1,2,3,4,5],
            currentStepId: '18046df82f0946af05ee766d0ac06f92f63e7047',
            previewStepId: '856980bacf0890c89bc318856980bacf0890c89b',
            preparationId: 'fbaa18e82e913e97e5f0e9d40f04413412be1126'
        };

        $httpBackend
            .expectPOST(RestURLs.previewUrl + '/diff', expectedParams)
            .respond(200);

        //when
        PreparationRestService.getPreviewDiff(preparationId, currentStep, previewStep, recordsTdpId, canceler)
            .then(function() {
                done = true;
            })
            .catch(function() {
                canceled = true;
            });
        canceler.resolve(true);
        $rootScope.$digest();

        //then
        expect(done).toBe(false);
        expect(canceled).toBe(true);
    }));

    it('should call update preview POST request', inject(function($rootScope, $q, RestURLs, PreparationRestService) {
        //given
        var done = false;
        var canceled = false;
        var currentStep = {transformation: {stepId: '18046df82f0946af05ee766d0ac06f92f63e7047'}};
        var updateStep = {transformation: {stepId: '856980bacf0890c89bc318856980bacf0890c89b'}, actionParameters: {action: 'cut'}};
        var newParams = {value: 'toto'};
        var recordsTdpId = [1,2,3,4,5];
        var canceler = $q.defer();

        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        var expectedParams = {
            action : {
                action: 'cut',
                parameters: newParams
            },
            tdpIds: [1,2,3,4,5],
            currentStepId: '18046df82f0946af05ee766d0ac06f92f63e7047',
            updateStepId: '856980bacf0890c89bc318856980bacf0890c89b',
            preparationId: 'fbaa18e82e913e97e5f0e9d40f04413412be1126'
        };

        $httpBackend
            .expectPOST(RestURLs.previewUrl + '/update', expectedParams)
            .respond(200);


        //when
        PreparationRestService.getPreviewUpdate(preparationId, currentStep, updateStep, newParams, recordsTdpId, canceler)
            .then(function() {
                done = true;
            })
            .catch(function() {
                canceled = true;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(done).toBe(true);
        expect(canceled).toBe(false);
    }));

    it('should cancel POST update preview by resolving the given promise', inject(function($rootScope, $q, RestURLs, PreparationRestService) {
        //given
        var done = false;
        var canceled = false;
        var currentStep = {transformation: {stepId: '18046df82f0946af05ee766d0ac06f92f63e7047'}};
        var updateStep = {transformation: {stepId: '856980bacf0890c89bc318856980bacf0890c89b'}, actionParameters: {action: 'cut'}};
        var newParams = {value: 'toto'};
        var recordsTdpId = [1,2,3,4,5];
        var canceler = $q.defer();

        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';

        var expectedParams = {
            action : {
                action: 'cut',
                parameters: newParams
            },
            tdpIds: [1,2,3,4,5],
            currentStepId: '18046df82f0946af05ee766d0ac06f92f63e7047',
            updateStepId: '856980bacf0890c89bc318856980bacf0890c89b',
            preparationId: 'fbaa18e82e913e97e5f0e9d40f04413412be1126'
        };

        $httpBackend
            .expectPOST(RestURLs.previewUrl + '/update', expectedParams)
            .respond(200);


        //when
        PreparationRestService.getPreviewUpdate(preparationId, currentStep, updateStep, newParams, recordsTdpId, canceler)
            .then(function() {
                done = true;
            })
            .catch(function() {
                canceled = true;
            });
        canceler.resolve(true);
        $rootScope.$digest();

        //then
        expect(done).toBe(false);
        expect(canceled).toBe(true);
    }));

    it('should call DELETE request', inject(function($rootScope, $q, RestURLs, PreparationRestService) {
        //given
        var preparationId = 'fbaa18e82e913e97e5f0e9d40f04413412be1126';
        var stepId = '856980bacf0890c89bc318856980bacf0890c89b';

        $httpBackend
            .expectDELETE(RestURLs.preparationUrl + '/' + preparationId + '/actions/' + stepId)
            .respond(200);

        //when
        PreparationRestService.removeStep(preparationId, stepId);
        $httpBackend.flush();
        $rootScope.$digest();

        //then : delete request to have been called
    }));
});