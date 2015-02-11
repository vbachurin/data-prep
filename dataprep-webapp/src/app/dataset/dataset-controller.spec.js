describe('Dataset controller', function() {
   'use strict';
    var scope, createController;
    var datasetDetails = [{
        metadata: {
            id: '12ce6c32-bf80-41c8-92e5-66d70f22ec1f',
            name: 'US States',
            author: 'anonymousUser',
            created: '02-03-2015 14:52'
        },
        columns: [
            {
                id: 'Postal',
                quality: {
                    empty: 5,
                    invalid: 10,
                    valid: 72
                },
                type: 'string'
            },
            {
                id: 'State',
                quality: {
                    empty: 5,
                    invalid: 10,
                    valid: 72
                },
                type: 'string'
            },
            {
                id: 'Capital',
                quality: {
                    empty: 5,
                    invalid: 10,
                    valid: 72
                },
                type: 'string'
            },
            {
                id: 'MostPopulousCity',
                quality: {
                    empty: 5,
                    invalid: 10,
                    valid: 72
                },
                type: 'string'
            }
        ],
        records: [
            {
                Postal: 'AL',
                State: 'Alabama',
                Capital: 'Montgomery',
                MostPopulousCity: 'Birmingham city'
            },
            {
                Postal: 'AK',
                State: 'Alaska',
                Capital: 'Juneau',
                MostPopulousCity: 'Anchorage'
            }
        ]
    }];
    
    beforeEach(module('data-prep'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function() {
            var ctrl =  $controller('DatasetCtrl', {
                $scope: scope,
                datasetDetails: datasetDetails
            });
            return ctrl;
        };
    }));
    
    it('should init controller variables with route resolve result', function() {
        //given
        
        //when
        var ctrl = createController();
        
        //then
        expect(ctrl.metadata).toEqual(datasetDetails.metadata);
        expect(ctrl.data.columns).toEqual(datasetDetails.columns);
        expect(ctrl.data.records).toEqual(datasetDetails.records);
    });
});