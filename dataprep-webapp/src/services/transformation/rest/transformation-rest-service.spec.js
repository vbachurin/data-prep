describe('Transformation Service', function() {
    'use strict';

    var $httpBackend;

    beforeEach(module('data-prep.services.transformation'));

    beforeEach(inject(function ($injector) {
        $httpBackend = $injector.get('$httpBackend');
    }));

    it('should call GET transform rest service', inject(function ($rootScope, TransformationRestService, RestURLs) {
        //given
        var datasetId = '44f5e4ef-96e9-4041-b86a-0bee3d50b18b';
        var columnId = 'firstname';
        var response = null;

        var result = [{'category':'case','items':[],'name':'uppercase','value':'','type':'OPERATION','parameters':[{'name':'column_name','type':'string','default':''}]},{'category':'case','items':[],'name':'lowercase','value':'','type':'OPERATION','parameters':[{'name':'column_name','type':'string','default':''}]}];
        $httpBackend
            .expectGET(RestURLs.datasetUrl + '/' + datasetId + '/' + columnId + '/actions')
            .respond(200, result);

        //when
        TransformationRestService.getTransformations(datasetId, columnId).then(function (resp) {
            response = resp.data;
        });
        $httpBackend.flush();

        //then
        expect(response).toEqual(result);
    }));
});