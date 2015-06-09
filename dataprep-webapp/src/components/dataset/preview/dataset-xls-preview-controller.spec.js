describe('Dataset xls preview controller', function () {
    'use strict';

    var createController, scope, gridElement;

    beforeEach(module('data-prep.dataset-xls-preview'));

    beforeEach(inject(function ($rootScope, $controller, $q, DatasetSheetPreviewService, DatasetService) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('DatasetXlsPreviewCtrl', {
                $scope: scope
            });
            return ctrl;
        };

        //insert grid entry point
        gridElement = angular.element('<div id="datasetSheetPreviewGrid"></div>');
        angular.element('body').append(gridElement);

        spyOn(DatasetSheetPreviewService, 'loadSheet').and.returnValue();
        spyOn(DatasetSheetPreviewService, 'setDatasetSheet').and.returnValue($q.when(true));
        spyOn(DatasetService, 'refreshDatasets').and.returnValue($q.when(true));
    }));

    afterEach(function() {
        gridElement.remove();
    });

    it('should bind modal state getter to DatasetSheetPreviewService.showModal', inject(function (DatasetSheetPreviewService) {
        //given
        var ctrl = createController();
        expect(ctrl.state).toBeFalsy();

        //when
        DatasetSheetPreviewService.showModal = true;

        //then
        expect(ctrl.state).toBe(true);
    }));

    it('should bind modal state setter to DatasetSheetPreviewService.showModal', inject(function (DatasetSheetPreviewService) {
        //given
        var ctrl = createController();
        DatasetSheetPreviewService.showModal = true;

        //when
        ctrl.state = false;

        //then
        expect(DatasetSheetPreviewService.showModal).toBe(false);
    }));

    it('should bind metadata getter to DatasetSheetPreviewService.currentMetadata', inject(function (DatasetSheetPreviewService) {
        //given
        var ctrl = createController();
        expect(ctrl.metadata).toBeFalsy();

        //when
        DatasetSheetPreviewService.currentMetadata = true;

        //then
        expect(ctrl.metadata).toBe(true);
    }));

    it('should bind selectedSheetName getter to DatasetSheetPreviewService.selectedSheetName', inject(function (DatasetSheetPreviewService) {
        //given
        var ctrl = createController();
        expect(ctrl.selectedSheetName).toBeFalsy();

        //when
        DatasetSheetPreviewService.selectedSheetName = 'my sheet';

        //then
        expect(ctrl.selectedSheetName).toBe('my sheet');
    }));

    it('should bind selectedSheetName setter to DatasetSheetPreviewService.selectedSheetName', inject(function (DatasetSheetPreviewService) {
        //given
        var ctrl = createController();
        DatasetSheetPreviewService.selectedSheetName = 'my sheet';

        //when
        ctrl.selectedSheetName = 'my new sheet';

        //then
        expect(DatasetSheetPreviewService.selectedSheetName).toBe('my new sheet');
    }));

    it('should set grid in service', inject(function ($timeout, DatasetSheetPreviewService) {
        //given
        expect(DatasetSheetPreviewService.grid).toBeFalsy();

        //when
        createController();
        $timeout.flush();

        //then
        expect(DatasetSheetPreviewService.grid).toBeTruthy();
    }));

    it('should call service to load the selected sheet', inject(function ($timeout, DatasetSheetPreviewService) {
        //given
        var ctrl = createController();
        $timeout.flush();
        ctrl.selectedSheetName = 'my sheet';

        //when
        ctrl.selectSheet();

        //then
        expect(DatasetSheetPreviewService.loadSheet).toHaveBeenCalledWith('my sheet');
    }));

    it('should set the selected sheet to the dataset', inject(function ($timeout, DatasetSheetPreviewService) {
        //given
        var ctrl = createController();
        $timeout.flush();
        ctrl.selectedSheetName = 'my sheet';

        //when
        ctrl.setDatasetSheet();

        //then
        expect(DatasetSheetPreviewService.setDatasetSheet).toHaveBeenCalledWith('my sheet');
    }));

    it('should refresh datasets list on selected sheet set', inject(function ($rootScope, $timeout, DatasetService) {
        //given
        var ctrl = createController();
        $timeout.flush();
        ctrl.selectedSheetName = 'my sheet';

        //when
        ctrl.setDatasetSheet();
        $rootScope.$digest();

        //then
        expect(DatasetService.refreshDatasets).toHaveBeenCalled();
    }));

    it('should hide modal on selected sheet set', inject(function ($rootScope, $timeout) {
        //given
        var ctrl = createController();
        $timeout.flush();
        ctrl.selectedSheetName = 'my sheet';
        ctrl.state = true;

        //when
        ctrl.setDatasetSheet();
        $rootScope.$digest();

        //then
        expect(ctrl.state).toBe(false);
    }));
});
