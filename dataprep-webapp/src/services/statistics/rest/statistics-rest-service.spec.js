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
        var mockResponseNumber = [
            { 'data': 'Lansing', 'occurrences': 15 },
            { 'data': 'Helena', 'occurrences': 5 },
            { 'data': 'Baton Rouge', 'occurrences': 64 },
            { 'data': 'Annapolis', 'occurrences': 4 },
            { 'data': 'Pierre', 'occurrences': 104 }
        ];

        $httpBackend
            .expectPOST(RestURLs.datasetUrl + '/aggregation/column', JSON.stringify(column))
            .respond(200, mockResponseNumber);

        //when
        StatisticsRestService.getAggregations(JSON.stringify(column))
            .then(function(response) {
                types = response;
            });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(types).toEqual(mockResponseNumber);

    }));

});
