describe('Dataset grid service', function() {
    'use strict';

    beforeEach(module('data-prep-dataset'));

    it('should init visibility flag', inject(function(DatasetGridService) {
        //then
        expect(DatasetGridService.visible).toBe(false);
    }));

    it('should set visibility flag to true', inject(function(DatasetGridService) {
        //when
        DatasetGridService.show();

        //then
        expect(DatasetGridService.visible).toBe(true);
    }));

    it('should set visibility flag to false', inject(function(DatasetGridService) {
        //given
        DatasetGridService.visible = true;

        //when
        DatasetGridService.hide();

        //then
        expect(DatasetGridService.visible).toBe(false);
    }));

    it('should set metadata and data', inject(function(DatasetGridService) {
        //given
        var metadata = {name: 'my dataset'};
        var data = {column: [], records: []};

        //when
        DatasetGridService.setDataset(metadata, data);

        //then
        expect(DatasetGridService.metadata).toBe(metadata);
        expect(DatasetGridService.data).toBe(data);
    }));

    it('should update data records', inject(function(DatasetGridService) {
        //given
        DatasetGridService.metadata = {name: 'my dataset'};
        DatasetGridService.data = {column: [], records: []};

        var records = [{col: 'value'}];

        //when
        DatasetGridService.updateRecords(records);

        //then
        expect(DatasetGridService.data.records).toBe(records);
    }));
});