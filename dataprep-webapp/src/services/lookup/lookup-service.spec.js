describe('lookup service', function () {
    'use strict';

    var dsLookupUrl = 'http://172.17.0.6:8080/datasets/9e739b88-5ec9-4b58-84b5-2127a7e2eac7/content?metadata=true';
    var dsActions = {
        data: [
            {
                'category': 'data_blending',
                'name': 'lookup',
                'parameters': [
                    {
                        'name': 'column_id',
                        'type': 'string',
                        'default': ''
                    },
                    {
                        'name': 'filter',
                        'type': 'filter',
                        'default': ''
                    },
                    {
                        'name': 'lookup_ds_name',
                        'type': 'string',
                        'default': 'lookup_2'
                    },
                    {
                        'name': 'lookup_ds_id',
                        'type': 'string',
                        'default': '9e739b88-5ec9-4b58-84b5-2127a7e2eac7'
                    },
                    {
                        'name': 'lookup_ds_url',
                        'type': 'string',
                        'default': dsLookupUrl
                    },
                    {
                        'name': 'lookup_join_on',
                        'type': 'string',
                        'default': ''
                    },
                    {
                        'name': 'lookup_join_on_name',
                        'type': 'string',
                        'default': ''
                    },
                    {
                        'name': 'lookup_selected_cols',
                        'type': 'list',
                        'default': ''
                    }
                ]
            }
        ]
    };
    var lookupDataset = dsActions.data[0];
    var dsLookupContent = {
        'metadata': {
            'id': '9e739b88-5ec9-4b58-84b5-2127a7e2eac7',
            'records': 3,
            'certificationStep': 'NONE',
            'location': {
                'type': 'local'
            },
            'name': 'lookup_2',
            'author': 'anonymous',
            'created': 1447689742940,
            'encoding': 'UTF-8'
        }
    };

    beforeEach(module('data-prep.services.lookup'));

    beforeEach(inject(function (TransformationRestService, DatasetRestService, StateService, $q) {
        spyOn(DatasetRestService, 'getContentFromUrl').and.returnValue($q.when(dsLookupContent));
        spyOn(TransformationRestService, 'getDatasetTransformations').and.returnValue($q.when(dsActions));
        spyOn(StateService, 'setCurrentLookupData').and.returnValue();
        spyOn(StateService, 'setLookupActions').and.returnValue();
        spyOn(StateService, 'setLookupDataset').and.returnValue();
    }));

    it('should load lookup dataset content', inject(function ($rootScope, LookupService, DatasetRestService) {
        //when
        LookupService.loadContent(lookupDataset);

        //then
        expect(DatasetRestService.getContentFromUrl).toHaveBeenCalledWith(dsLookupUrl);
    }));

    it('should set lookup dataset content in state', inject(function ($rootScope, LookupService, StateService) {
        //when
        LookupService.loadContent(lookupDataset);
        $rootScope.$digest();

        //then
        expect(StateService.setCurrentLookupData).toHaveBeenCalledWith(dsLookupContent);
        expect(StateService.setLookupDataset).toHaveBeenCalledWith(lookupDataset);
    }));

    it('should load all possible lookup actions', inject(function ($rootScope, TransformationRestService, LookupService) {
        //when
        LookupService.getActions('9e739b88-5ec9-4b58-84b5-2127a7e2eac7');

        //then
        expect(TransformationRestService.getDatasetTransformations).toHaveBeenCalledWith('9e739b88-5ec9-4b58-84b5-2127a7e2eac7');
    }));

    it('should set possible lookup actions in state', inject(function ($rootScope, LookupService, StateService) {
        //when
        LookupService.getActions('9e739b88-5ec9-4b58-84b5-2127a7e2eac7');
        $rootScope.$digest();

        //then
        expect(StateService.setLookupActions).toHaveBeenCalledWith(dsActions.data);
    }));
});