describe('Lookup Grid state service', function () {
	'use strict';

	var data = {
		columns: [
			{ id: '0000', 'name':'identif'},
			{ id: '0001', 'name':'code'},
			{ id: '0002', 'name':'firstname'},
			{ id: '0003', 'name':'lastname'}
		],
		records: [
			{tdpId: 0, firstname: 'Tata'},
			{tdpId: 1, firstname: 'Tetggggge'},
			{tdpId: 2, firstname: 'Titi'},
			{tdpId: 3, firstname: 'Toto'},
			{tdpId: 4, name: 'AMC Gremlin'},
			{tdpId: 5, firstname: 'Tyty'},
			{tdpId: 6, firstname: 'Papa'},
			{tdpId: 7, firstname: 'Pepe'},
			{tdpId: 8, firstname: 'Pipi'},
			{tdpId: 9, firstname: 'Popo'},
			{tdpId: 10, firstname: 'Pupu'},
			{tdpId: 11, firstname: 'Pypy'}
		]
	};

	var addedToLookup = [
		{'id':'0000' , 'name':'identif' , isAdded: false},
		{'id':'0001' , 'name':'code' , isAdded: false},
		{'id':'0002' , 'name':'firstname' , isAdded: false},
		{'id':'0003' , 'name':'lastname' , isAdded: false}
	];

	var addedToLookupWithSelected = [
		{'id':'0000','name':'identif',isAdded: false},
		{'id':'0001','name':'code',isAdded: false},
		{'id':'0002','name':'firstname',isAdded: true},
		{'id':'0003','name':'lastname',isAdded: true}
	];

	var lookupDatasets = [
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
					'default': 'http://172.17.0.211:8080/datasets/9ee2eac7/content?metadata=true'
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
	];
	var lookupDataset = lookupDatasets[0];

	beforeEach(module('data-prep.services.state'));

	beforeEach(inject(function(gridState) {
		gridState.dataView = new DataViewMock();
	}));

	describe('data', function() {
		it('should set data to DataView', inject(function (lookupState, LookupStateService) {
			//given
			expect(lookupState.dataView.getItems()).not.toBe(data.records);

			//when
			LookupStateService.setData(data);

			//then
			expect(lookupState.dataView.getItems()).toBe(data.records);
			expect(lookupState.addedToLookup).toEqual(addedToLookup);
		}));

		it('should create and set to false all the columns that will be added to the lookup', inject(function (lookupState, LookupStateService) {
			//given
			expect(lookupState.dataView.getItems()).toBe(data.records);
			lookupState.addedToLookup = [];

			//when
			LookupStateService.setData(data);

			//then
			expect(lookupState.addedToLookup).toEqual(addedToLookup);
			expect(lookupState.lookupColumnsToAdd).toEqual([]);
		}));

		it('should update column selection metadata with the new metadata corresponding to the selected id', inject(function (lookupState, LookupStateService) {
			//given
			var oldMetadata = {id: '0001'};
			lookupState.selectedColumn = oldMetadata;

			//when
			LookupStateService.setData(data);

			//then
			expect(lookupState.selectedColumn).not.toBe(oldMetadata);
			expect(lookupState.selectedColumn).toBe(data.columns[1]);
		}));

		it('should update column selection metadata with the 1st column when actual selected column is not in the new columns', inject(function (lookupState, LookupStateService) {
			//given
			var oldMetadata = {id: '0018'};
			lookupState.selectedColumn = oldMetadata;

			//when
			LookupStateService.setData(data);

			//then
			expect(lookupState.selectedColumn).not.toBe(oldMetadata);
			expect(lookupState.selectedColumn).toBe(data.columns[0]);
		}));

		it('should update column selection metadata with the first column metadata when there is no selected column yet', inject(function (lookupState, LookupStateService) {
			//given
			lookupState.selectedColumn = null;

			//when
			LookupStateService.setData(data);

			//then
			expect(lookupState.selectedColumn).toBe(data.columns[0]);
		}));

	});

	describe('grid event result state', function() {
		it('should set focused columns', inject(function (lookupState, LookupStateService) {
			//given
			expect(lookupState.columnFocus).toBeFalsy();

			//when
			LookupStateService.setColumnFocus('0001');

			//then
			expect(lookupState.columnFocus).toBe('0001');
		}));

		it('should set grid selection', inject(function (lookupState, LookupStateService) {
			//given
			lookupState.selectedColumn = null;
			lookupState.selectedLine = null;

			//when
			LookupStateService.setGridSelection('0001');

			//then
			expect(lookupState.selectedColumn).toBe('0001');
		}));

		it('should set columns list to be added', inject(function (lookupState, LookupStateService) {
			//given
			lookupState.addedToLookup = addedToLookupWithSelected;
			lookupState.lookupColumnsToAdd = null;

			//when
			LookupStateService.setLookupColumnsToAdd();

			//then
			expect(lookupState.lookupColumnsToAdd).toEqual([ { id: '0002', name: 'firstname' }, { id: '0003', name: 'lastname' } ] );
		}));

		it('should collect the columns Ids to be added ', inject(function (lookupState, LookupStateService) {
			//given
			lookupState.addedToLookup = addedToLookupWithSelected;

			//when
			LookupStateService.setGridSelection('0001');

			//then
			expect(lookupState.lookupColumnsToAdd).toEqual([ { id: '0002', name: 'firstname' }, { id: '0003', name: 'lastname' } ]);
		}));

		it('should not collect the columns Ids to be added ', inject(function (lookupState, LookupStateService) {
			//given
			lookupState.addedToLookup = addedToLookupWithSelected;
			var addedCols = ['0018'];
			lookupState.lookupColumnsToAdd = addedCols;

			//when
			LookupStateService.setGridSelection(null);

			//then
			expect(lookupState.lookupColumnsToAdd).toBe(addedCols);
		}));
	});

	describe('reset', function() {
		it('should reset event result state', inject(function(lookupState, LookupStateService) {
			//given
			lookupState.columnFocus = '0001';
			lookupState.selectedColumn = '0001';
			lookupState.selectedLine = 2;
			lookupState.lookupColumnsToAdd = ['0000'];
			lookupState.addedToLookup = [{id:'0001', isAdded: true, name:'vfvf'}];
			lookupState.datasets = [{}, {}];

			//when
			LookupStateService.reset();

			//then
			expect(lookupState.columnFocus).toBe(null);
			expect(lookupState.selectedColumn).toBe(null);
			expect(lookupState.selectedLine).toBe(null);
			expect(lookupState.lookupColumnsToAdd).toEqual([]);
			expect(lookupState.addedToLookup).toEqual([]);
			expect(lookupState.datasets).toEqual([]);
		}));
	});

	describe('Lookup Dataset', function() {
		it('should set lookup dataset', inject(function(lookupState, LookupStateService) {
			//given
			lookupState.dataset = null;

			//when
			LookupStateService.setDataset(lookupDataset);

			//then
			expect(lookupState.dataset).toBe(lookupDataset);
		}));

		it('should set lookup datasets', inject(function(lookupState, LookupStateService) {
			//given
			lookupState.datasets = [];

			//when
			LookupStateService.setPotentialDatasets(lookupDatasets);

			//then
			expect(lookupState.datasets).toBe(lookupDatasets);
		}));
	});
});
