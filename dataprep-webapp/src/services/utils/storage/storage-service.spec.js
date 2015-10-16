describe('Storage service', function() {
    'use strict';

    beforeEach(module('data-prep.services.utils'));

    afterEach(inject(function($window) {
        $window.localStorage.clear();
    }));

    describe('aggregation', function() {
        it('should add aggregation in local storage', inject(function($window, StorageService) {
            //given
            var datasetId = '87a646f763bd545b684';
            var preparationId = '72515d3212cf565b624';
            var columnId = '0001';
            var aggregation = {
                aggregation: 'MAX',
                aggregationColumnId: '0002'
            };

            var expectedKey = 'org.talend.dataprep.aggregation.87a646f763bd545b684.72515d3212cf565b624.0001';
            expect($window.localStorage.getItem(expectedKey)).toBeFalsy();

            //when
            StorageService.setAggregation(datasetId, preparationId, columnId, aggregation);

            //then
            expect($window.localStorage.getItem(expectedKey)).toBe(JSON.stringify(aggregation));
        }));

        it('should generate key without dataset id', inject(function($window, StorageService) {
            //given
            var preparationId = '72515d3212cf565b624';
            var columnId = '0001';
            var aggregation = {
                aggregation: 'MAX',
                aggregationColumnId: '0002'
            };

            var expectedKey = 'org.talend.dataprep.aggregation..72515d3212cf565b624.0001';
            expect($window.localStorage.getItem(expectedKey)).toBeFalsy();

            //when
            StorageService.setAggregation(null, preparationId, columnId, aggregation);

            //then
            expect($window.localStorage.getItem(expectedKey)).toBe(JSON.stringify(aggregation));
        }));

        it('should generate key without preparation id', inject(function($window, StorageService) {
            //given
            var datasetId = '87a646f763bd545b684';
            var columnId = '0001';
            var aggregation = {
                aggregation: 'MAX',
                aggregationColumnId: '0002'
            };

            var expectedKey = 'org.talend.dataprep.aggregation.87a646f763bd545b684..0001';
            expect($window.localStorage.getItem(expectedKey)).toBeFalsy();

            //when
            StorageService.setAggregation(datasetId, null, columnId, aggregation);

            //then
            expect($window.localStorage.getItem(expectedKey)).toBe(JSON.stringify(aggregation));
        }));

        it('should get aggregation from local storage', inject(function($window, StorageService) {
            //given
            var datasetId = '87a646f763bd545b684';
            var preparationId = '72515d3212cf565b624';
            var columnId = '0001';
            var aggregation = {
                aggregation: 'MAX',
                aggregationColumnId: '0002'
            };

            var expectedKey = 'org.talend.dataprep.aggregation.87a646f763bd545b684.72515d3212cf565b624.0001';
            $window.localStorage.setItem(expectedKey, JSON.stringify(aggregation));

            //when
            var aggregationFromStorage = StorageService.getAggregation(datasetId, preparationId, columnId);

            //then
            expect(aggregationFromStorage).toEqual(aggregation);
        }));

        it('should remove aggregation from local storage', inject(function($window, StorageService) {
            //given
            var datasetId = '87a646f763bd545b684';
            var preparationId = '72515d3212cf565b624';
            var columnId = '0001';
            var aggregation = {
                aggregation: 'MAX',
                aggregationColumnId: '0002'
            };

            var expectedKey = 'org.talend.dataprep.aggregation.87a646f763bd545b684.72515d3212cf565b624.0001';
            $window.localStorage.setItem(expectedKey, JSON.stringify(aggregation));

            //when
            StorageService.removeAggregation(datasetId, preparationId, columnId);

            //then
            expect($window.localStorage.getItem(expectedKey)).toBeFalsy();
        }));
    });
});