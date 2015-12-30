describe('lookup service', function () {
    'use strict';
    var stateMock;
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
            'encoding': 'UTF-8',
            columns : [{id : '0000'}]
        }
    };
    /*jshint camelcase: false */
    var step = {
        column:{
            id:'0000',
            name:'id'
        },
        row:{
            id:'11'
        },
        transformation:{
            stepId:'72fe267d489b06890da69368f4760530b076ec59',
            name:'lookup',
            label:'Lookup',
            description:'Blends columns from another dataset into this one',
            parameters:[],
            dynamic:false
        },
        actionParameters:{
            action:'lookup',
            parameters:{
                column_id:'0000',
                filter:'',
                lookup_ds_id:'9e739b88-5ec9-4b58-84b5-2127a7e2eac7',
                lookup_join_on:'0000',
                scope:'dataset',
                column_name:'id',
                lookup_selected_cols:[
                    {
                        id:'0001',
                        name:'uglystate',
                    }
                ],
                row_id:'11',
                lookup_ds_url:'dsLookupUrl',
                lookup_join_on_name:'id',
                lookup_ds_name:'cluster_dataset'
            }
        },
        diff:{
            createdColumns:[
                '0009'
            ]
        },
        filters:(void 0)
    };
    beforeEach(module('data-prep.services.lookup', function($provide) {
        stateMock = {
            playground: {
                data: {metadata : {columns: [{id : '0000'}]}},
                dataset: {id : 'abcd'},
                lookup: {visibility: false},
                grid: {
                    selectedColumn: {'id': '0000'},
                    selectedLine: {'0001': '1'}
                }
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(function (TransformationRestService, DatasetRestService, StateService, $q) {
        spyOn(DatasetRestService, 'getContentFromUrl').and.returnValue($q.when(dsLookupContent));
        spyOn(TransformationRestService, 'getDatasetTransformations').and.returnValue($q.when(dsActions));
        spyOn(StateService, 'setCurrentLookupData').and.returnValue();
        spyOn(StateService, 'setLookupActions').and.returnValue();
        spyOn(StateService, 'setLookupDataset').and.returnValue();
        spyOn(StateService, 'setLookupUpdateMode').and.returnValue();
        spyOn(StateService, 'setLookupStep').and.returnValue();
        spyOn(StateService, 'setLookupVisibility').and.returnValue();
        spyOn(StateService, 'setLookupSelectedColumn').and.returnValue();
        spyOn(StateService, 'updateLookupColumnsToAdd').and.returnValue();
        spyOn(StateService, 'setGridSelection').and.returnValue();
        spyOn(StateService, 'setUpdatingLookupStep').and.returnValue();
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

    it('should set update mode when lookup panel is hidden', inject(function ($rootScope, LookupService, StateService) {
        //when
        LookupService.setUpdateMode(step);
        $rootScope.$digest();

        //then
        expect(StateService.setLookupUpdateMode).toHaveBeenCalled();
        expect(StateService.setLookupStep).toHaveBeenCalledWith(step);
    }));

    it('should set update mode when lookup panel is displayed', inject(function ($rootScope, LookupService, TransformationRestService, StateService, DatasetRestService) {
        //given
        stateMock.playground.lookup.visibility = true;
        stateMock.playground.lookup.actions = [];
        //when
        LookupService.setUpdateMode(step);
        $rootScope.$digest();

        //then
        expect(TransformationRestService.getDatasetTransformations).toHaveBeenCalled();
        expect(StateService.setLookupActions).toHaveBeenCalledWith(dsActions.data);
        expect(DatasetRestService.getContentFromUrl).toHaveBeenCalledWith(dsLookupUrl);
    }));


    it('should updating a lookup step', inject(function ($rootScope, LookupService, TransformationRestService, StateService) {
        //given
        stateMock.playground.lookup.visibility = true;
        stateMock.playground.lookup.actions = [];
        stateMock.playground.lookup.step = step;
        stateMock.playground.lookup.isUpdatingLookupStep = true;

        stateMock.playground.lookup.columnCheckboxes = [{id: '0000'}, {id: '0001'}];

        //when
        LookupService.setUpdateMode(step);
        $rootScope.$digest();

        //then
        expect(TransformationRestService.getDatasetTransformations).toHaveBeenCalled();
        expect(StateService.setLookupActions).toHaveBeenCalledWith(dsActions.data);
        expect(StateService.setLookupSelectedColumn).toHaveBeenCalledWith({id : '0000'});
        expect(StateService.updateLookupColumnsToAdd).toHaveBeenCalled();
        expect(StateService.setGridSelection).toHaveBeenCalledWith({id : '0000'});
        expect(stateMock.playground.lookup.columnCheckboxes[1].isAdded).toBeTruthy();
    }));

    it('should toogle panel when lookup panel is closed a step does not exist in state', inject(function ($rootScope, LookupService, StateService) {
        //when
        LookupService.setUpdateMode(step, true);
        $rootScope.$digest();

        //then
        expect(StateService.setLookupVisibility).toHaveBeenCalledWith(!stateMock.playground.lookup.visibility);
    }));

    it('should toogle panel when step exists in state but different from the updating step', inject(function ($rootScope, LookupService, StateService) {
        //given
        stateMock.playground.lookup.step = {transformation : {stepId : '0000'}};

        //when
        LookupService.setUpdateMode(step, true);
        $rootScope.$digest();

        //then
        expect(StateService.setLookupVisibility).toHaveBeenCalledWith(true);
    }));

    it('should toogle panel when step exists in state and is updating', inject(function ($rootScope, LookupService, StateService) {
        //given
        stateMock.playground.lookup.step = {transformation : {stepId : '72fe267d489b06890da69368f4760530b076ec59'}};

        //when
        LookupService.setUpdateMode(step, true);
        $rootScope.$digest();

        //then
        expect(StateService.setLookupVisibility).toHaveBeenCalledWith(!stateMock.playground.lookup.visibility);
    }));

    it('should load lookup panel in update mode', inject(function ($rootScope, LookupService, StateService, RecipeService) {
        //given
        spyOn(RecipeService, 'getRecipe').and.returnValue([step]);
        spyOn(LookupService, 'setUpdateMode').and.returnValue();

        //when
        LookupService.loadLookupPanel(false);
        $rootScope.$digest();

        //then
        expect(StateService.setLookupUpdateMode).toHaveBeenCalled();
        expect(StateService.setLookupStep).toHaveBeenCalledWith(step);
    }));

    it('should load lookup panel in add mode', inject(function ($rootScope, LookupService, StateService, RecipeService) {
        //given
        stateMock.playground.grid.selectedColumn.id = '0002';
        spyOn(RecipeService, 'getRecipe').and.returnValue([step]);
        spyOn(LookupService, 'setAddMode').and.returnValue();

        //when
        LookupService.loadLookupPanel(false);
        $rootScope.$digest();

        //then
        expect(StateService.setUpdatingLookupStep).toHaveBeenCalledWith(false);
        expect(StateService.setLookupStep).toHaveBeenCalledWith(null);
    }));

    it('should load lookup panel in add mode when panel is displayed', inject(function ($rootScope, LookupService, StateService, TransformationRestService) {
        //given
        stateMock.playground.lookup.visibility = true;
        stateMock.playground.lookup.actions = [];

        //when
        LookupService.setAddMode(false);
        $rootScope.$digest();

        //then
        expect(TransformationRestService.getDatasetTransformations).toHaveBeenCalled();
        expect(StateService.setLookupActions).toHaveBeenCalledWith(dsActions.data);
    }));

    it('should load lookup panel in add mode when panel is displayed and actions exist in state service', inject(function ($rootScope, LookupService, StateService, TransformationRestService) {
        //given
        stateMock.playground.lookup.visibility = true;
        stateMock.playground.lookup.actions = dsActions.data;

        //when
        LookupService.setAddMode(false);
        $rootScope.$digest();

        //then
        expect(TransformationRestService.getDatasetTransformations).not.toHaveBeenCalled();
    }));

    it('should toogle panel in add mode', inject(function ($rootScope, LookupService, StateService) {
        //when
        LookupService.setAddMode(true);
        $rootScope.$digest();

        //then
        expect(StateService.setLookupVisibility).toHaveBeenCalledWith(!stateMock.playground.lookup.visibility);
    }));
});