describe('Dataset Service', function () {
    'use strict';

    var datasets = [{name: 'my dataset'}, {name: 'my second dataset'}, {name: 'my second dataset (1)'}, {name: 'my second dataset (2)'}];

    beforeEach(module('data-prep.services.dataset'));

    beforeEach(inject(function(DatasetListService) {
        DatasetListService.datasets = datasets;
    }));

    it('should adapt infos to dataset object for upload', inject(function (DatasetService) {
        //given
        var file = {
            path: '/path/to/file'
        };
        var name = 'myDataset';
        var id = 'e85afAa78556d5425bc2';

        //when
        var dataset = DatasetService.fileToDataset(file, name, id);

        //then
        expect(dataset.name).toBe(name);
        expect(dataset.progress).toBe(0);
        expect(dataset.file).toBe(file);
        expect(dataset.error).toBe(false);
        expect(dataset.id).toBe(id);
    }));

    it('should get unique dataset name', inject(function (DatasetService) {
        //given
        var name = 'my dataset';

        //when
        var uniqueName = DatasetService.getUniqueName(name);

        //then
        expect(uniqueName).toBe('my dataset (1)');
    }));

    it('should get unique dataset name with a number in it', inject(function (DatasetService) {
        //given
        var name = 'my second dataset (2)';

        //when
        var uniqueName = DatasetService.getUniqueName(name);

        //then
        expect(uniqueName).toBe('my second dataset (3)');
    }));
});