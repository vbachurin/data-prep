describe('Dataset List Service', function () {
    'use strict';

    var datasets = [{name: 'my dataset'}, {name: 'my second dataset'}, {name: 'my second dataset (1)'}, {name: 'my second dataset (2)'}];

    beforeEach(module('data-prep.services.dataset'));

    beforeEach(inject(function ($q, DatasetRestService) {
        spyOn(DatasetRestService, 'getDatasets').and.returnValue($q.when({data: datasets}));
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

    it('should not trigger another refresh when one is already pending', inject(function ($rootScope, DatasetListService, DatasetRestService) {
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
        expect(DatasetRestService.getDatasets.calls.count()).toBe(1);
    }));

    it('should call refresh when datasets is not initialized yet', inject(function ($rootScope, DatasetListService, DatasetRestService) {
        //given
        DatasetListService.datasets = null;
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
        expect(DatasetRestService.getDatasets).toHaveBeenCalled();
    }));

    it('should not refresh and return resolved promise when datasets is initialized', inject(function ($rootScope, DatasetListService, DatasetRestService) {
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
        expect(DatasetRestService.getDatasets).not.toHaveBeenCalled();
    }));
});