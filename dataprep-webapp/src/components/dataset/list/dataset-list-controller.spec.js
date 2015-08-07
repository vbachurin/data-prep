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

        spyOn(DatasetService, 'refreshDatasets').and.returnValue($q.when(true));
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


    it('should refresh dataset when sort is changed without localstorage - step 1', inject(function ($window, DatasetService) {

        // Remove LocalStorage.
        $window.localStorage.removeItem('dataprep.dataset.sortSelected');
        $window.localStorage.removeItem('dataprep.dataset.sortOrderSelected');

        //given
        var ctrl = createController();
        var newSort =  {id: 'name', name: 'NAME_SORT'};

        //when
        ctrl.updateSortBy(newSort);

        //then
        expect(DatasetService.refreshDatasets).toHaveBeenCalledWith('name','desc');
    }));


    it('should refresh dataset when order is changed without localstorage - step 2', inject(function ($window, DatasetService) {

        // Remove LocalStorage.
        $window.localStorage.removeItem('dataprep.dataset.sortSelected');
        $window.localStorage.removeItem('dataprep.dataset.sortOrderSelected');

        //given
        var ctrl = createController();
        var newSortOrder =  {id: 'asc', name: 'ASC_ORDER'};

        //when
        ctrl.updateSortOrder(newSortOrder);

        //then
        expect(DatasetService.refreshDatasets).toHaveBeenCalledWith('date','asc');
    }));


    it('should refresh dataset when sort is changed using localstorage of previous tests - step 3', inject(function (DatasetService) {

        //given
        var ctrl = createController();
        var newSort =  {id: 'name', name: 'NAME_SORT'};

        //when
        ctrl.updateSortBy(newSort);

        //then
        expect(DatasetService.refreshDatasets).toHaveBeenCalledWith('name','asc');
    }));


    it('should refresh dataset when order is changed using localstorage of previous tests - step 4', inject(function (DatasetService) {

        //given
        var ctrl = createController();
        var newSortOrder =  {id: 'desc', name: 'ASC_ORDER'};

        //when
        ctrl.updateSortOrder(newSortOrder);

        //then
        expect(DatasetService.refreshDatasets).toHaveBeenCalledWith('name','desc');
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
            spyOn(MessageService, 'success').and.callThrough();
            spyOn(DatasetSheetPreviewService, 'loadPreview').and.returnValue($q.when(true));
            spyOn(DatasetSheetPreviewService, 'display').and.returnValue($q.when(true));
            spyOn(DatasetService, 'toggleFavorite').and.returnValue($q.when(true));
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

        it('should toogle dataset favorite flag', inject(function ($rootScope, DatasetService) {
            //given
            var dataset = {name: 'Customers (50 lines)', id: 'aA2bc348e933bc2', favorite: false};

            //when
            ctrl.toggleFavorite(dataset);
            $rootScope.$apply();

            //then
            expect(DatasetService.toggleFavorite).toHaveBeenCalledWith(dataset);
        }));
    });
});
