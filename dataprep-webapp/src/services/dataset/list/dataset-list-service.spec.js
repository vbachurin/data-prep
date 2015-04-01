describe('Dataset List Service', function () {
    'use strict';

    var datasets = [{name: 'my dataset'}, {name: 'my second dataset'}, {name: 'my second dataset (1)'}, {name: 'my second dataset (2)'}];

    beforeEach(module('data-prep.services.dataset'));

    beforeEach(inject(function ($q, DatasetService) {
        spyOn(DatasetService, 'getDatasets').and.returnValue($q.when({data: datasets}));
    }));

    it('should get unique dataset name', inject(function (DatasetListService) {
        //given
        DatasetListService.datasets = datasets;
        var name = 'my dataset';

        //when
        var uniqueName = DatasetListService.getUniqueName(name);

        //then
        expect(uniqueName).toBe('my dataset (1)');
    }));

    it('should get unique dataset name with a number in it', inject(function (DatasetListService) {
        //given
        DatasetListService.datasets = datasets;
        var name = 'my second dataset (2)';

        //when
        var uniqueName = DatasetListService.getUniqueName(name);

        //then
        expect(uniqueName).toBe('my second dataset (3)');
    }));

    it('should refresh dataset list', inject(function ($rootScope, DatasetListService) {
        //given
        var oldDatasets = [{name: 'my dataset'}, {name: 'my second dataset'}];
        DatasetListService.datasets = oldDatasets;

        //when
        DatasetListService.refreshDatasets();
        $rootScope.$apply();

        //then
        expect(DatasetListService.datasets).toBe(datasets);
    }));

    it('should not trigger another refresh when one is already pending', inject(function ($rootScope, DatasetListService, DatasetService) {
        //given
        var oldDatasets = [{name: 'my dataset'}, {name: 'my second dataset'}];
        DatasetListService.datasets = oldDatasets;

        var firstCall = DatasetListService.refreshDatasets();

        //when
        var secondCall = DatasetListService.refreshDatasets();
        expect(secondCall).toBe(firstCall);
        $rootScope.$apply();

        //then
        expect(DatasetListService.datasets).toBe(datasets);
        expect(DatasetService.getDatasets.calls.count()).toBe(1);
    }));

    it('should call refresh when datasets is not initialized yet', inject(function ($rootScope, DatasetListService, DatasetService) {
        //given
        DatasetListService.datasets = [];
        var resolvedDatasets = [];

        //when
        DatasetListService.getDatasetsPromise()
            .then(function(datasetsResponse) {
                resolvedDatasets = datasetsResponse;
            });
        $rootScope.$apply();

        //then
        expect(DatasetListService.datasets).toBe(datasets);
        expect(resolvedDatasets).toBe(datasets);
        expect(DatasetService.getDatasets).toHaveBeenCalled();
    }));

    it('should not refresh and return resolved promise when datasets is initialized', inject(function ($rootScope, DatasetListService, DatasetService) {
        //given
        DatasetListService.datasets = datasets;
        var resolvedDatasets = [];

        //when
        DatasetListService.getDatasetsPromise()
            .then(function(datasetsResponse) {
                resolvedDatasets = datasetsResponse;
            });
        $rootScope.$apply();

        //then
        expect(DatasetListService.datasets).toBe(datasets);
        expect(resolvedDatasets).toBe(datasets);
        expect(DatasetService.getDatasets).not.toHaveBeenCalled();
    }));
});