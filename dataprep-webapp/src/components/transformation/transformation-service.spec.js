describe('Transformation Service', function() {
    'use strict';

    var $httpBackend;

    beforeEach(module('data-prep-transformation'));

    beforeEach(inject(function ($injector) {
        $httpBackend = $injector.get('$httpBackend');
    }));

    it('should adapt param and call dataset transform rest service', inject(function ($rootScope, TransformationService, RestURLs) {
        //given
        var datasetId = '44f5e4ef-96e9-4041-b86a-0bee3d50b18b';
        var action = 'uppercase';
        var parameters = {'column_name': 'city'};

        var result = {'records':
            [{'firstname':'Grover','avgAmount':'82.4','city':'BOSTON','birth':'01-09-1973','registration':'17-02-2008','id':'1','state':'AR','nbCommands':'41','lastname':'Quincy'},{'firstname':'Warren','avgAmount':'87.6','city':'NASHVILLE','birth':'11-02-1960','registration':'18-08-2007','id':'2','state':'WA','nbCommands':'17','lastname':'Johnson'}]
        };
        var httpParam = {'actions':
            [{
                'action': 'uppercase',
                'parameters': {'column_name': 'city'}
            }]
        };
        $httpBackend
            .expectPOST(RestURLs.transformUrl + '/' + datasetId, httpParam)
            .respond(200, result);

        //when
        TransformationService.transform(datasetId, action, parameters).then(function (data) {
            result = data;
        });
        $httpBackend.flush();

        //then
        expect(result).toBe(result);
    }));
});