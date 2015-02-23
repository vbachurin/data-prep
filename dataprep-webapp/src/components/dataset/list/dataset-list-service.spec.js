describe('Dataset List Service', function () {
    'use strict';

    var datasets = [{name: 'my dataset'}, {name: 'my second dataset'}, {name: 'my second dataset (1)'}, {name: 'my second dataset (2)'}];

    beforeEach(module('data-prep-dataset'));

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
});