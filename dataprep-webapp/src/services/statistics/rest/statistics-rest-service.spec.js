describe('Statistics REST service', function () {
    'use strict';

    var $httpBackend;

    beforeEach(module('data-prep.services.statistics'));

    beforeEach(inject(function ($injector) {
        $httpBackend = $injector.get('$httpBackend');
    }));


    it('should get aggregation column data', inject(function($rootScope, RestURLs, StatisticsRestService) {
        //given
        var types = null;
        var column = {'id': 'firstname'};
        var mockResponseNumber ={};
        mockResponseNumber.data = [
            {
                'range': {
                    'min': 1,
                    'max': 13.375
                },
                'occurrences': 456
            },
            {
                'range': {
                    'min': 13.375,
                    'max': 25.75
                },
                'occurrences': 12
            },
            {
                'range': {
                    'min': 25.75,
                    'max': 38.125
                },
                'occurrences': 10
            },
            {
                'range': {
                    'min': 38.125,
                    'max': 50.5
                },
                'occurrences': 0
            },
            {
                'range': {
                    'min': 50.5,
                    'max': 62.875
                },
                'occurrences': 250
            }];

        $httpBackend
            .expectPOST(RestURLs.datasetUrl + '/aggregation/column', JSON.stringify(column))
            .respond(200, mockResponseNumber);

        //when
        StatisticsRestService.getAggregations(JSON.stringify(column))
            .then(function(response) {
                types = response.data;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(types).toEqual(mockResponseNumber);

    }));

});
