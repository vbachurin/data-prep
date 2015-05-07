describe('Preparation Service', function () {
    'use strict';

    var gridRangeIndex = {top: 0, bottom: 5};
    var data = {
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
        ]
    };
    //diff result corresponding to gridRangeIndex
    var diff = {
        data: {
            records: [
                {firstname: 'Tata'},
                {__tdpRowDiff: 'new', firstname: 'Tata Bis'}, //insert new row
                {firstname: 'Tete'},
                {firstname: 'Titi'},
                {__tdpRowDiff: 'delete', firstname: 'Papa'}, //row is deleted in preview
                {firstname: 'Pepe', __tdpDiff: {firstname: 'update'}}, //firstname is updated in preview
                {firstname: 'Pupu'}
            ]
        }
    };
    //diff inserted at ids [0,1,2,6,7,10]
    var modifiedData = {
        records: [
            {firstname: 'Tata'},
            {__tdpRowDiff: 'new', firstname: 'Tata Bis'}, //insert new row
            {firstname: 'Tete'},
            {tdpId : 3, firstname: 'Toto'},
            {tdpId : 4, firstname: 'Tutu'},
            {tdpId : 5, firstname: 'Tyty'},
            {firstname: 'Titi'},
            {__tdpRowDiff: 'delete', firstname: 'Papa'}, //row is deleted in preview
            {tdpId : 8, firstname: 'Pipi'},
            {tdpId : 9, firstname: 'Popo'},
            {firstname: 'Pepe', __tdpDiff: {firstname: 'update'}}, //firstname is updated in preview
            {firstname: 'Pupu'}
        ]
    };
    //diff inserted at ids [0,1,2,6,7,10], with 'Tata Bis' filtered
    var filteredModifiedData = {
        records: [
            {firstname: 'Tata'},
            {firstname: 'Tete'},
            {firstname: 'Titi'},
            {tdpId : 3, firstname: 'Toto'},
            {tdpId : 4, firstname: 'Tutu'},
            {tdpId : 5, firstname: 'Tyty'},
            {__tdpRowDiff: 'delete', firstname: 'Papa'}, //row is deleted in preview
            {firstname: 'Pepe', __tdpDiff: {firstname: 'update'}}, //firstname is updated in preview
            {tdpId : 8, firstname: 'Pipi'},
            {tdpId : 9, firstname: 'Popo'},
            {firstname: 'Pupu'},
            { tdpId: 11, firstname: 'Pypy' }
        ]
    };

    beforeEach(module('data-prep.services.playground'));

    beforeEach(inject(function ($q, PreviewService, DatagridService, PreparationService) {
        DatagridService.data = data;
        PreviewService.gridRangeIndex = gridRangeIndex;
        
        spyOn(PreparationService, 'getPreviewDiff').and.returnValue($q.when(diff));
        spyOn(PreparationService, 'getPreviewUpdate').and.returnValue($q.when(diff));
        spyOn(DatagridService, 'updateRecords').and.returnValue(null);

        //simulate datagrid get item to have displayedTdpIds = [0,1,2,6,7,10]
        spyOn(DatagridService.dataView, 'getItem').and.callFake(function(id) {
            switch(id) {
                case 0:
                    return data.records[0];
                case 1:
                    return data.records[1];
                case 2:
                    return data.records[2];
                case 3:
                    return data.records[6];
                case 4:
                    return data.records[7];
                case 5:
                    return data.records[10];
            }
            return null;
        });
    }));

    it('should call and display a diff preview', inject(function($rootScope, PreviewService, PreparationService, DatagridService) {
        //given
        var currentStep = {transformation: { stepId: '1'}};
        var previewStep = {transformation: { stepId: '2'}};
        var displayedTdpIds = [0,1,2,6,7,10];

        //when
        PreviewService.getPreviewDiffRecords(currentStep, previewStep);
        $rootScope.$digest();

        //then
        expect(PreparationService.getPreviewDiff).toHaveBeenCalled();

        var previewArgs = PreparationService.getPreviewDiff.calls.mostRecent().args;
        expect(previewArgs[0]).toBe(currentStep);
        expect(previewArgs[1]).toBe(previewStep);
        expect(previewArgs[2]).toEqual(displayedTdpIds);

        expect(DatagridService.updateRecords).toHaveBeenCalledWith(modifiedData.records);
    }));

    it('should filter preview records according to active filters', inject(function($rootScope, PreviewService, PreparationService, DatagridService) {
        //given
        var currentStep = {transformation: { stepId: '1'}};
        var previewStep = {transformation: { stepId: '2'}};

        DatagridService.addFilter(function(item) {
            return item.firstname !== 'Tata Bis';
        });

        //when
        PreviewService.getPreviewDiffRecords(currentStep, previewStep);
        $rootScope.$digest();

        //then
        expect(DatagridService.updateRecords).toHaveBeenCalledWith(filteredModifiedData.records);

        //finally
        DatagridService.resetFilters();
    }));

    it('should call and display a update preview', inject(function($rootScope, PreviewService, PreparationService, DatagridService) {
        //given
        var currentStep = {transformation: { stepId: '1'}};
        var previewStep = {transformation: { stepId: '2'}};
        var newParams = {value: '--'};
        var displayedTdpIds = [0,1,2,6,7,10];

        //when
        PreviewService.getPreviewUpdateRecords(currentStep, previewStep, newParams);
        $rootScope.$digest();

        //then
        expect(PreparationService.getPreviewUpdate).toHaveBeenCalled();

        var previewArgs = PreparationService.getPreviewUpdate.calls.mostRecent().args;
        expect(previewArgs[0]).toBe(currentStep);
        expect(previewArgs[1]).toBe(previewStep);
        expect(previewArgs[2]).toBe(newParams);
        expect(previewArgs[3]).toEqual(displayedTdpIds);

        expect(DatagridService.updateRecords).toHaveBeenCalledWith(modifiedData.records);
    }));

    it('should resolve preview canceler to cancel the pending request', inject(function(PreviewService, PreparationService) {
        //given
        var currentStep = {transformation: { stepId: '1'}};
        var previewStep = {transformation: { stepId: '2'}};

        PreviewService.getPreviewDiffRecords(currentStep, previewStep); //pending request as we do not call $digest

        //when
        PreviewService.cancelPreview();

        //then
        var previewArgs = PreparationService.getPreviewDiff.calls.mostRecent().args;
        expect(previewArgs[3].promise.$$state.status).toBe(1);
        expect(previewArgs[3].promise.$$state.value).toBe('user cancel');
    }));

    it('should reinit datagrid with original values', inject(function($rootScope, PreviewService, DatagridService) {
        //given
        var currentStep = {transformation: { stepId: '1'}};
        var previewStep = {transformation: { stepId: '2'}};

        PreviewService.getPreviewDiffRecords(currentStep, previewStep);
        $rootScope.$digest();
        expect(DatagridService.updateRecords.calls.count()).toBe(1);

        //when
        PreviewService.cancelPreview();

        //then
        expect(DatagridService.updateRecords.calls.count()).toBe(2);
        expect(DatagridService.updateRecords.calls.argsFor(1)[0]).toBe(data.records);
    }));
});
