/*jshint camelcase: false */
describe('Preview Service', function () {
    'use strict';

    var gridRangeIndex = {top: 1, bottom: 5};
    var displayedTdpIds = [1,3,6,7,8];
    var originalData = {
        records: [
            {tdpId : 0, firstname: 'Tata'},
            {tdpId : 1, firstname: 'Tete'},
            {tdpId : 2, firstname: 'Titi'},
            {tdpId : 3, firstname: 'Toto'},
            {tdpId : 4, firstname: 'Tutu'},
            {tdpId : 5, firstname: 'Tyty'},
            {tdpId : 6, firstname: 'Papa'},
            {tdpId : 7, firstname: 'Pepe'},
            {tdpId : 8, firstname: 'Pipi'},
            {tdpId : 9, firstname: 'Popo'},
            {tdpId : 10, firstname: 'Pupu'},
            {tdpId : 11, firstname: 'Pypy'}
        ],
        columns: [{id: '0000', name: 'lastname'}, {id: '0001', name: 'firstname'}]
    };

    //diff result corresponding to gridRangeIndex
    var diff = {
        data: {
            records: [
                {tdpId: 1, firstname: 'Tete'},
                {tdpId: 2, firstname: 'Titi Bis', __tdpRowDiff: 'new'}, //insert new row
                {tdpId: 3, firstname: 'Toto', __tdpRowDiff: 'delete'}, //row is deleted in preview
                {tdpId: 6, firstname: 'Papa'},
                {tdpId: 7, firstname: 'Pepe 2', __tdpDiff: {firstname: 'update'}}, //firstname is updated in preview
                {tdpId: 8, firstname: 'Pipi'}
            ],
            columns: [{id: '0000', name: 'lastname'}, {id: '0001', name: 'firstname'}]
        }
    };

    //result of the diff insertion in the original data
    var modifiedData = {
        records: [
            {tdpId: 0, firstname: 'Tata'},
            {tdpId: 1, firstname: 'Tete'},
            {tdpId: 2, firstname: 'Titi Bis', __tdpRowDiff: 'new'}, //insert new row
            {tdpId: 3, firstname: 'Toto', __tdpRowDiff: 'delete'}, //row is deleted in preview
            {tdpId: 6, firstname: 'Papa'},
            {tdpId: 7, firstname: 'Pepe 2', __tdpDiff: {firstname: 'update'}}, //firstname is updated in preview
            {tdpId: 8, firstname: 'Pipi'},
            {tdpId: 9, firstname: 'Popo'},
            {tdpId: 10, firstname: 'Pupu'},
            {tdpId: 11, firstname: 'Pypy'}
        ],
        columns: [{id: '0000', name: 'lastname'}, {id: '0001', name: 'firstname'}],
        preview: true
    };

    var previewExecutor = {};
    var reverterExecutor = {};

    beforeEach(module('data-prep.services.playground'));

    beforeEach(inject(function ($q, PreviewService, DatagridService, PreparationService) {
        DatagridService.data = originalData;
        PreviewService.gridRangeIndex = gridRangeIndex;

        //simulate datagrid get item to have displayedTdpIds = [1,3,6,7,8]
        spyOn(DatagridService.dataView, 'getItem').and.callFake(function(id) {
            switch(id) {
                case 1:
                    return originalData.records[1];
                case 2:
                    return originalData.records[3];
                case 3:
                    return originalData.records[6];
                case 4:
                    return originalData.records[7];
                case 5:
                    return originalData.records[8];
            }
            return null;
        });

        //simulate datagrid get array index by (tdp) id
        spyOn(DatagridService.dataView, 'getIdxById').and.callFake(function(id) {
            switch(id) {
                case 1:
                    return 1;
                case 8:
                    return 8;
            }
            return null;
        });

        spyOn(DatagridService, "previewDataExecutor").and.returnValue(previewExecutor);
        spyOn(DatagridService, "execute").and.returnValue(reverterExecutor);

        spyOn(PreparationService, 'getPreviewDiff').and.returnValue($q.when(diff));
        spyOn(PreparationService, 'getPreviewUpdate').and.returnValue($q.when(diff));
        spyOn(PreparationService, 'getPreviewAdd').and.returnValue($q.when(diff));
    }));

    describe('diff preview', function() {
        it('should call and display preview', inject(function($rootScope, PreviewService, PreparationService, DatagridService) {
            //given
            var currentStep = {
                column:{id:'0001'},
                transformation: { stepId: '1'}
            };
            var previewStep = {
                column:{id:'0000'},
                transformation: { stepId: '2'}
            };

            //when
            PreviewService.getPreviewDiffRecords(currentStep, previewStep, null);
            $rootScope.$digest();

            //then
            expect(PreparationService.getPreviewDiff).toHaveBeenCalled();

            var previewArgs = PreparationService.getPreviewDiff.calls.mostRecent().args;
            expect(previewArgs[0]).toBe(currentStep);
            expect(previewArgs[1]).toBe(previewStep);
            expect(previewArgs[2]).toEqual(displayedTdpIds);

            expect(DatagridService.execute).toHaveBeenCalledWith(undefined); //reverter but no preview to revert
            expect(DatagridService.execute).toHaveBeenCalledWith(previewExecutor); //preview diff
        }));

        it('should focus on provided column', inject(function($rootScope, PreviewService, DatagridService) {
            //given
            var currentStep = {
                column:{id:'0001'},
                transformation: { stepId: '1'}
            };
            var previewStep = {
                column:{id:'0000'},
                transformation: { stepId: '2'}
            };
            var focusColumnId = '0000';

            //when
            PreviewService.getPreviewDiffRecords(currentStep, previewStep, focusColumnId);
            $rootScope.$digest();

            //then
            expect(DatagridService.focusedColumn).toBe(focusColumnId);
        }));

        it('should cancel current pending preview', inject(function($rootScope, PreviewService, PreparationService) {
            //given
            var currentStep = {
                column:{id:'0001'},
                transformation: { stepId: '1'}
            };
            var previewStep = {
                column:{id:'0000'},
                transformation: { stepId: '2'}
            };

            PreviewService.getPreviewDiffRecords(currentStep, previewStep, null);
            var previewArgs = PreparationService.getPreviewDiff.calls.mostRecent().args;
            var previewCanceler = previewArgs[3];

            expect(previewCanceler.promise.$$state.status).toBe(0);

            //when
            PreviewService.getPreviewDiffRecords(currentStep, previewStep, null);

            //then
            expect(previewCanceler.promise.$$state.status).toBe(1);
        }));
    });

    describe('update preview', function() {
        it('should call and display preview', inject(function($rootScope, PreviewService, PreparationService, DatagridService) {
            //given
            var currentStep = {
                column:{id: '0001'},
                transformation: { stepId: '1'}
            };
            var updateStep = {
                column:{id: '0000'},
                transformation: { stepId: '2'}
            };
            var newParams = {value: '--'};

            //when
            PreviewService.getPreviewUpdateRecords(currentStep, updateStep, newParams);
            $rootScope.$digest();

            //then
            expect(PreparationService.getPreviewUpdate).toHaveBeenCalled();

            var previewArgs = PreparationService.getPreviewUpdate.calls.mostRecent().args;
            expect(previewArgs[0]).toBe(currentStep);
            expect(previewArgs[1]).toBe(updateStep);
            expect(previewArgs[2]).toBe(newParams);
            expect(previewArgs[3]).toEqual(displayedTdpIds);

            expect(DatagridService.execute).toHaveBeenCalledWith(undefined); //reverter but no preview to revert
            expect(DatagridService.execute).toHaveBeenCalledWith(previewExecutor); //preview diff
        }));

        it('should focus on update step column', inject(function($rootScope, PreviewService, DatagridService) {
            //given
            var currentStep = {
                column:{id:'0001'},
                transformation: { stepId: '1'}
            };
            var updateStep = {
                column:{id:'0000'},
                transformation: { stepId: '2'}
            };
            var newParams = {value: '--'};

            //when
            PreviewService.getPreviewUpdateRecords(currentStep, updateStep, newParams);
            $rootScope.$digest();

            //then
            expect(DatagridService.focusedColumn).toBe(updateStep.column.id);
        }));

        it('should cancel current pending preview', inject(function($rootScope, PreviewService, PreparationService) {
            //given
            var currentStep = {
                column:{id:'0001'},
                transformation: { stepId: '1'}
            };
            var updateStep = {
                column:{id:'0000'},
                transformation: { stepId: '2'}
            };
            var newParams = {value: '--'};

            PreviewService.getPreviewUpdateRecords(currentStep, updateStep, newParams);
            var previewArgs = PreparationService.getPreviewUpdate.calls.mostRecent().args;
            var previewCanceler = previewArgs[4];

            expect(previewCanceler.promise.$$state.status).toBe(0);

            //when
            PreviewService.getPreviewUpdateRecords(currentStep, updateStep, newParams);

            //then
            expect(previewCanceler.promise.$$state.status).toBe(1);
        }));

    });

    describe('add preview', function() {
        it('should call and display preview', inject(function($rootScope, PreviewService, PreparationService, DatagridService) {
            //given
            var datasetId = '46c541b683ef5151';
            var action = 'fillEmptyWithValue';
            var params = {
                scope: 'column',
                column_id: '0001',
                value: '--'
            };

            //when
            PreviewService.getPreviewAddRecords(datasetId, action, params);
            $rootScope.$digest();

            //then
            expect(PreparationService.getPreviewAdd).toHaveBeenCalled();

            var previewArgs = PreparationService.getPreviewAdd.calls.mostRecent().args;
            expect(previewArgs[0]).toBe(datasetId);
            expect(previewArgs[1]).toBe(action);
            expect(previewArgs[2]).toBe(params);
            expect(previewArgs[3]).toEqual(displayedTdpIds);

            expect(DatagridService.execute).toHaveBeenCalledWith(undefined); //reverter but no preview to revert
            expect(DatagridService.execute).toHaveBeenCalledWith(previewExecutor); //preview diff
        }));

        it('should focus on add step column', inject(function($rootScope, PreviewService, DatagridService) {
            //given
            var datasetId = '46c541b683ef5151';
            var action = 'fillEmptyWithValue';
            var params = {
                scope: 'column',
                column_id: '0001',
                value: '--'
            };

            //when
            PreviewService.getPreviewAddRecords(datasetId, action, params);
            $rootScope.$digest();

            //then
            expect(DatagridService.focusedColumn).toBe('0001');
        }));

        it('should cancel current pending preview', inject(function($rootScope, PreviewService, PreparationService) {
            //given
            var datasetId = '46c541b683ef5151';
            var action = 'fillEmptyWithValue';
            var params = {
                scope: 'column',
                column_id: '0001',
                value: '--'
            };

            PreviewService.getPreviewAddRecords(datasetId, action, params);
            var previewArgs = PreparationService.getPreviewAdd.calls.mostRecent().args;
            var previewCanceler = previewArgs[4];

            expect(previewCanceler.promise.$$state.status).toBe(0);

            //when
            PreviewService.getPreviewAddRecords(datasetId, action, params);

            //then
            expect(previewCanceler.promise.$$state.status).toBe(1);
        }));
    });

    describe('reset/cancel/stop preview', function() {
        beforeEach(inject(function($rootScope, PreviewService, DatagridService) {
            var currentStep = {
                column:{id:'0001'},
                transformation: { stepId: '1'}
            };
            var previewStep = {
                column:{id:'0000'},
                transformation: { stepId: '2'}
            };

            PreviewService.getPreviewDiffRecords(currentStep, previewStep, null);
        }));

        it('should stop pending preview', inject(function(PreviewService, PreparationService) {
            //given
            var previewArgs = PreparationService.getPreviewDiff.calls.mostRecent().args;
            var previewCanceler = previewArgs[3];

            expect(previewCanceler.promise.$$state.status).toBe(0);

            //when
            PreviewService.stopPendingPreview();

            //then
            expect(previewCanceler.promise.$$state.status).toBe(1);
        }));

        it('should restore original data on reset', inject(function($rootScope, PreviewService, DatagridService) {
            //given
            $rootScope.$digest();
            expect(DatagridService.execute.calls.count()).toBe(2);
            expect(DatagridService.execute).toHaveBeenCalledWith(undefined);
            expect(DatagridService.execute).toHaveBeenCalledWith(previewExecutor);

            //when
            PreviewService.reset(true);

            //then
            expect(DatagridService.execute.calls.count()).toBe(3);
            expect(DatagridService.execute).toHaveBeenCalledWith(reverterExecutor);
        }));

        it('should NOT restore original data on reset', inject(function(PreviewService, DatagridService) {
            //given
            expect(DatagridService.execute).not.toHaveBeenCalled();

            //when
            PreviewService.reset(false);

            //then
            expect(DatagridService.execute).not.toHaveBeenCalled();
        }));

        it('should stop pending preview on cancel call', inject(function(PreviewService, PreparationService) {
            //given
            var previewArgs = PreparationService.getPreviewDiff.calls.mostRecent().args;
            var previewCanceler = previewArgs[3];

            expect(previewCanceler.promise.$$state.status).toBe(0);

            //when
            PreviewService.cancelPreview();

            //then
            expect(previewCanceler.promise.$$state.status).toBe(1);
        }));

        it('should NOT set focused column nor restore original data on cancel call', inject(function(PreviewService, DatagridService) {
            //given
            expect(DatagridService.execute).not.toHaveBeenCalled();
            expect(DatagridService.focusedColumn).toBeFalsy();

            var partial = true; // do NOT restore original data
            var focusedColId = '0001';

            //when
            PreviewService.cancelPreview(partial, focusedColId);

            //then
            expect(DatagridService.execute).not.toHaveBeenCalled();
            expect(DatagridService.focusedColumn).toBeFalsy();
        }));

        it('should set focused column and restore original data on cancel call', inject(function($rootScope, PreviewService, DatagridService) {
            //given
            $rootScope.$digest();
            expect(DatagridService.execute.calls.count()).toBe(2);
            expect(DatagridService.execute).toHaveBeenCalledWith(undefined);
            expect(DatagridService.execute).toHaveBeenCalledWith(previewExecutor);
            expect(DatagridService.focusedColumn).toBeFalsy();

            var partial = false; // do restore original data
            var focusedColId = '0001';

            //when
            PreviewService.cancelPreview(partial, focusedColId);

            //then
            expect(DatagridService.execute.calls.count()).toBe(3);
            expect(DatagridService.execute).toHaveBeenCalledWith(reverterExecutor);
            expect(DatagridService.focusedColumn).toBe(focusedColId);
        }));
    });
});
