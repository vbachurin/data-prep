describe('lookup service', function() {
	'use strict';

	var stateMock;
	var dsLookupUrl = 'http://172.17.0.6:8080/datasets/9e739b88-5ec9-4b58-84b5-2127a7e2eac7/content?metadata=true';

	var dsActions = {data : [
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
	]};
	var lookupDataset = dsActions.data[0];
	var dsLookupContent = {
		data:{
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
		}
	};

	beforeEach(module('data-prep.services.lookup', function ($provide) {
		stateMock = {playground: {lookupGrid : {dataset: lookupDataset}}};
		$provide.constant('state', stateMock);
	}));

	beforeEach(inject(function(LookupRestService, StateService, $q) {
		spyOn(LookupRestService, 'getLookupContent').and.returnValue($q.when(dsLookupContent));
		spyOn(LookupRestService, 'getLookupActions').and.returnValue($q.when(dsActions));
		spyOn(StateService, 'setCurrentLookupData').and.returnValue();
		spyOn(StateService, 'setLookupDatasets').and.returnValue();
	}));

	it('should load lookup dataset content', inject(function(LookupRestService, $rootScope, LookupService, StateService) {
		//when
		LookupService.loadLookupContent();
		$rootScope.$digest();

		//then
		expect(LookupRestService.getLookupContent).toHaveBeenCalledWith(dsLookupUrl);
		expect(StateService.setCurrentLookupData).toHaveBeenCalledWith(dsLookupContent.data);
	}));

	it('should load all datasets with which a lookup is possible', inject(function(LookupRestService, $rootScope, LookupService, StateService) {
		//when
		LookupService.getLookupPossibleActions('9e739b88-5ec9-4b58-84b5-2127a7e2eac7');
		$rootScope.$digest();

		//then
		expect(LookupRestService.getLookupActions).toHaveBeenCalledWith('9e739b88-5ec9-4b58-84b5-2127a7e2eac7');
		expect(StateService.setLookupDatasets).toHaveBeenCalledWith(dsActions.data);
	}));
});