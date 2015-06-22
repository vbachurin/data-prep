describe('Dataset list controller', function () {
    'use strict';

    var createController, scope;
    var datasets = [
        {id: 'ec4834d9bc2af8', name: 'Customers (50 lines)'},
        {id: 'ab45f893d8e923', name: 'Us states'},
        {id: 'cf98d83dcb9437', name: 'Customers (1K lines)'}
    ];
    var refreshedDatasets = [
        {id: 'ec4834d9bc2af8', name: 'Customers (50 lines)'},
        {id: 'ab45f893d8e923', name: 'Us states'}
    ];

    beforeEach(module('data-prep.dataset-list'));

    beforeEach(inject(function ($rootScope, $controller, $q, $state, DatasetService, PlaygroundService, MessageService) {
        var datasetsValues = [datasets, refreshedDatasets];
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('DatasetListCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        spyOn(DatasetService, 'processCertification').and.returnValue($q.when(true));
        spyOn(DatasetService, 'getDatasets').and.callFake(function () {
            return $q.when(datasetsValues.shift());
        });

        spyOn(PlaygroundService, 'initPlayground').and.returnValue($q.when(true));
        spyOn(PlaygroundService, 'show').and.callThrough();
        spyOn(MessageService, 'error').and.returnValue(null);
        spyOn($state, 'go').and.returnValue(null);
    }));

    afterEach(inject(function($stateParams) {
        $stateParams.datasetid = null;
    }));

    it('should get dataset on creation', inject(function (DatasetService) {
        //when
        createController();
        scope.$digest();

        //then
        expect(DatasetService.getDatasets).toHaveBeenCalled();
    }));

    it('should init playground with the provided datasetId from url', inject(function ($stateParams, PlaygroundService) {
        //given
        $stateParams.datasetid = 'ab45f893d8e923';

        //when
        createController();
        scope.$digest();

        //then
        expect(PlaygroundService.initPlayground).toHaveBeenCalledWith(datasets[1]);
        expect(PlaygroundService.show).toHaveBeenCalled();
    }));

    it('should show error message when dataset id is not in users dataset', inject(function ($stateParams, PlaygroundService, MessageService) {
        //given
        $stateParams.datasetid = 'azerty';

        //when
        createController();
        scope.$digest();

        //then
        expect(PlaygroundService.initPlayground).not.toHaveBeenCalled();
        expect(PlaygroundService.show).not.toHaveBeenCalled();
        expect(MessageService.error).toHaveBeenCalledWith('PLAYGROUND_FILE_NOT_FOUND_TITLE', 'PLAYGROUND_FILE_NOT_FOUND', {type: 'dataset'});
    }));

    describe('already created', function () {
        var ctrl;

        beforeEach(inject(function ($rootScope, $q, MessageService, DatasetService, DatasetSheetPreviewService) {
            ctrl = createController();
            scope.$digest();

            spyOn(DatasetService, 'delete').and.returnValue($q.when(true));
            spyOn(DatasetService, 'refreshDatasets').and.returnValue($q.when(true));
            spyOn(MessageService, 'success').and.callThrough();
            spyOn(DatasetSheetPreviewService, 'loadPreview').and.returnValue($q.when(true));
            spyOn(DatasetSheetPreviewService, 'display').and.returnValue($q.when(true));
        }));

        it('should delete dataset and show toast', inject(function ($q, MessageService, DatasetService, TalendConfirmService) {
            //given
            var dataset = datasets[0];
            spyOn(TalendConfirmService, 'confirm').and.returnValue($q.when(true));

            //when
            ctrl.delete(dataset);
            scope.$digest();

            //then
            expect(TalendConfirmService.confirm).toHaveBeenCalledWith({disableEnter: true}, [ 'DELETE_PERMANENTLY', 'NO_UNDONE_CONFIRM' ], {type: 'dataset', name: 'Customers (50 lines)' });
            expect(DatasetService.delete).toHaveBeenCalledWith(dataset);
            expect(MessageService.success).toHaveBeenCalledWith('REMOVE_SUCCESS_TITLE', 'REMOVE_SUCCESS', {type: 'dataset', name: 'Customers (50 lines)'});
        }));

        it('should redirect to dataset playground when dataset is not a draft', inject(function ($rootScope, $state) {
            //given
            var dataset = {name: 'Customers (50 lines)', id: 'aA2bc348e933bc2'};

            //when
            ctrl.openDataset(dataset);
            $rootScope.$apply();

            //then
            expect($state.go).toHaveBeenCalledWith('nav.home.datasets', {datasetid: dataset.id});
        }));

        it('should redirect load sheet preview when dataset is a draft', inject(function ($rootScope, DatasetSheetPreviewService) {
            //given
            var dataset = {name: 'Customers (50 lines)', id: 'aA2bc348e933bc2', type: 'application/vnd.ms-excel', draft: true};

            //when
            ctrl.openDataset(dataset);
            $rootScope.$apply();

            //then
            expect(DatasetSheetPreviewService.loadPreview).toHaveBeenCalledWith(dataset);
            expect(DatasetSheetPreviewService.display).toHaveBeenCalled();
        }));

        it('should bind datasets getter to DatasetService.datasetsList()', inject(function (DatasetService) {
            //given
            spyOn(DatasetService, 'datasetsList').and.returnValue(refreshedDatasets);

            //then
            expect(ctrl.datasets).toBe(refreshedDatasets);
        }));

        it('should process certification on dataset', inject(function (DatasetService) {
            //when
            ctrl.processCertification(datasets[0]);

            //then
            expect(DatasetService.processCertification).toHaveBeenCalledWith(datasets[0]);
        }));

        it('should load excel draft preview and display it', inject(function ($rootScope, DatasetSheetPreviewService) {
            //given
            var draft = {type: 'application/vnd.ms-excel'};

            //when
            ctrl.openDraft(draft);
            $rootScope.$digest();

            //then
            expect(DatasetSheetPreviewService.loadPreview).toHaveBeenCalledWith(draft);
            expect(DatasetSheetPreviewService.display).toHaveBeenCalled();
        }));

        it('should display error message with unknown draft type', inject(function (DatasetSheetPreviewService, MessageService) {
            //given
            var draft = {type: 'application/myCustomType'};

            //when
            ctrl.openDraft(draft);

            //then
            expect(DatasetSheetPreviewService.loadPreview).not.toHaveBeenCalled();
            expect(DatasetSheetPreviewService.display).not.toHaveBeenCalled();
            expect(MessageService.error).toHaveBeenCalledWith('PREVIEW_NOT_IMPLEMENTED_FOR_TYPE_TITLE', 'PREVIEW_NOT_IMPLEMENTED_FOR_TYPE_TITLE');
        }));

        it('should refresh dataset list and display error when draft has no type yet', inject(function (DatasetSheetPreviewService, DatasetService, MessageService) {
            //given
            var draft = {};

            //when
            ctrl.openDraft(draft);

            //then
            expect(DatasetSheetPreviewService.loadPreview).not.toHaveBeenCalled();
            expect(DatasetSheetPreviewService.display).not.toHaveBeenCalled();
            expect(MessageService.error).toHaveBeenCalledWith('FILE_FORMAT_ANALYSIS_NOT_READY_TITLE', 'FILE_FORMAT_ANALYSIS_NOT_READY_CONTENT');
            expect(DatasetService.refreshDatasets).toHaveBeenCalled();
        }));
    });


    describe('event "talend.dataset.open" received for a finished dataset', function () {
        var ctrl;
        var dataset = {id: 'ec4834d9bc2af8', name: 'Customers (50 lines)', draft: false};

        beforeEach(inject(function ($q, DatasetService) {
            ctrl = createController();
            scope.$digest();
            spyOn(DatasetService, 'getDatasetById').and.returnValue($q.when(dataset));
        }));

        it('should open dataset when "talend.dataset.open" event is received for a finished dataset', inject(function ($rootScope, $state, DatasetService) {


            //given
            createController();
            //scope.$digest();

            //when
            $rootScope.$emit('talend.dataset.open', dataset.id);
            $rootScope.$digest();

            //then
            expect(DatasetService.getDatasetById).toHaveBeenCalledWith(dataset.id);
            expect($state.go).toHaveBeenCalledWith('nav.home.datasets', {datasetid: dataset.id});
        }));
    });


    describe('event "talend.dataset.open" received for a draft dataset', function () {
        var ctrl;
        var dataset = {id: 'ec4834d9bc2af8', name: 'Customers (50 lines)', draft: true, type: 'application/vnd.ms-excel'};

        beforeEach(inject(function ($q, DatasetService, DatasetSheetPreviewService) {
            ctrl = createController();
            scope.$digest();
            spyOn(DatasetService, 'getDatasetById').and.returnValue($q.when(dataset));
            spyOn(DatasetSheetPreviewService, 'loadPreview').and.returnValue($q.when({}));
        }));

        it('should open dataset when "talend.dataset.open" event is received for a draft dataset', inject(function ($rootScope, DatasetService, DatasetSheetPreviewService) {


            //given
            createController();
            //scope.$digest();

            //when
            $rootScope.$emit('talend.dataset.open', dataset.id);
            $rootScope.$digest();

            //then
            expect(DatasetService.getDatasetById).toHaveBeenCalledWith(dataset.id);
            expect(DatasetSheetPreviewService.loadPreview).toHaveBeenCalledWith(dataset);
        }));
    });

});
