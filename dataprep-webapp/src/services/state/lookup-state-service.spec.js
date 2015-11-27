describe('Lookup state service', function () {
    'use strict';

    var data = {
        columns: [
            {id: '0000', 'name': 'identif'},
            {id: '0001', 'name': 'code'},
            {id: '0002', 'name': 'firstname'},
            {id: '0003', 'name': 'lastname'}
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

    var initialColumnCheckboxes = [
        {'id': '0000', 'name': 'identif', isAdded: false},
        {'id': '0001', 'name': 'code', isAdded: false},
        {'id': '0002', 'name': 'firstname', isAdded: false},
        {'id': '0003', 'name': 'lastname', isAdded: false}
    ];
    var columnCheckboxesWithSelection = [
        {'id': '0000', 'name': 'identif', isAdded: false},
        {'id': '0001', 'name': 'code', isAdded: false},
        {'id': '0002', 'name': 'firstname', isAdded: true},
        {'id': '0003', 'name': 'lastname', isAdded: true}
    ];

    var lookupActions = [
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
    var lookupDataset = lookupActions[0];

    beforeEach(module('data-prep.services.state'));

    beforeEach(inject(function (lookupState) {
        lookupState.dataView = new DataViewMock();
    }));

    describe('visibility', function () {
        it('should set visibility flag', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.visibility = false;

            //when
            LookupStateService.setVisibility(true);

            //then
            expect(lookupState.visibility).toBe(true);
        }));
    });

    describe('data', function () {
        it('should set data to DataView', inject(function (lookupState, LookupStateService) {
            //given
            expect(lookupState.dataView.getItems()).not.toBe(data.records);

            //when
            LookupStateService.setData(data);

            //then
            expect(lookupState.dataView.getItems()).toBe(data.records);
        }));

        it('should initialize the column checkboxes', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.columnCheckboxes = [];

            //when
            LookupStateService.setData(data);

            //then
            expect(lookupState.columnCheckboxes).toEqual(initialColumnCheckboxes);
        }));

        it('should reset columns to add', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.columnsToAdd = [{}];

            //when
            LookupStateService.setData(data);

            //then
            expect(lookupState.columnsToAdd).toEqual([]);
        }));

        it('should update selected column to the 1st column', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.selectedColumn = {id: '0001'};

            //when
            LookupStateService.setData(data);

            //then
            expect(lookupState.selectedColumn).toBe(data.columns[0]);
        }));
    });

    describe('dataset', function () {
        it('should set dataset', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.dataset = null;

            //when
            LookupStateService.setDataset(lookupDataset);

            //then
            expect(lookupState.dataset).toBe(lookupDataset);
        }));
    });

    describe('actions', function () {
        it('should set actions', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.actions = [];

            //when
            LookupStateService.setActions(lookupActions);

            //then
            expect(lookupState.actions).toBe(lookupActions);
        }));
    });

    describe('grid event', function () {
        it('should update the columns to add corresponding to the checkboxes model', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.columnCheckboxes = columnCheckboxesWithSelection;
            lookupState.columnsToAdd = null;

            //when
            LookupStateService.updateColumnsToAdd();

            //then
            expect(lookupState.columnsToAdd).toEqual([
                {id: '0002', name: 'firstname'},
                {id: '0003', name: 'lastname'}
            ]);
        }));

        it('should set selected column', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.selectedColumn = null;
            var selectedColumn = {id: '0001'};

            //when
            LookupStateService.setSelectedColumn(selectedColumn);

            //then
            expect(lookupState.selectedColumn).toBe(selectedColumn);
        }));

        it('should update the columns to add on new column selection', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.columnCheckboxes = columnCheckboxesWithSelection;
            var selectedColumn = {id: '0001'};

            //when
            LookupStateService.setSelectedColumn(selectedColumn);

            //then
            expect(lookupState.columnsToAdd).toEqual([
                {id: '0002', name: 'firstname'},
                {id: '0003', name: 'lastname'}]
            );
        }));

        it('should NOT update the columns to add when there is no selected column', inject(function (lookupState, LookupStateService) {
            //given
            var addedCols = ['0018'];
            lookupState.columnsToAdd = addedCols;
            lookupState.columnCheckboxes = columnCheckboxesWithSelection;

            //when
            LookupStateService.setSelectedColumn(null);

            //then
            expect(lookupState.columnsToAdd).toBe(addedCols);
        }));
    });

    describe('reset', function () {
        it('should reset state', inject(function (lookupState, LookupStateService) {
            //given
            lookupState.actions = [{}, {}];
            lookupState.columnsToAdd = ['0000'];
            lookupState.columnCheckboxes = [{id: '0001', isAdded: true, name: 'vfvf'}];
            lookupState.dataset = {};
            lookupState.selectedColumn = '0001';
            lookupState.visibility = true;

            //when
            LookupStateService.reset();

            //then
            expect(lookupState.actions).toEqual([]);
            expect(lookupState.columnsToAdd).toEqual([]);
            expect(lookupState.columnCheckboxes).toEqual([]);
            expect(lookupState.dataset).toBe(null);
            expect(lookupState.selectedColumn).toBe(null);
            expect(lookupState.visibility).toBe(false);
        }));
    });

});
