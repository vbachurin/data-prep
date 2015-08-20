describe('Statistics REST service', function () {
    'use strict';

    var $httpBackend;

    beforeEach(module('data-prep.services.statistics'));

    beforeEach(inject(function ($injector) {
        $httpBackend = $injector.get('$httpBackend');
    }));


    it('should get aggregation column data', inject(function ($rootScope, RestURLs, StatisticsRestService) {
        //given
        var data = null;
        var params = {};
        var response = {
            data: [
                {'data': 'Lansing', 'occurrences': 15},
                {'data': 'Helena', 'occurrences': 5},
                {'data': 'Baton Rouge', 'occurrences': 64},
                {'data': 'Annapolis', 'occurrences': 4},
                {'data': 'Pierre', 'occurrences': 104}
            ]
        };

        $httpBackend
            .expectPOST(RestURLs.aggregationUrl, params)
            .respond(200, response);

        //when
        StatisticsRestService.getAggregations(params)
            .then(function(response) {
                data = response.data;
            });
        $httpBackend.flush();

        //then
        expect(data).toEqual(response.data);
    }));

});
