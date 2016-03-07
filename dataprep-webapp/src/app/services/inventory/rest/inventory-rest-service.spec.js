/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Inventory Rest Service', function () {
    'use strict';

    var $httpBackend;

    beforeEach(angular.mock.module('data-prep.services.inventory'));

    beforeEach(inject(function ($rootScope, $injector, RestURLs) {
        RestURLs.setServerUrl('');
        $httpBackend = $injector.get('$httpBackend');

        spyOn($rootScope, '$emit').and.returnValue();
    }));
    it('should call inventory search rest service ', inject(function ($rootScope, InventoryRestService, RestURLs, $q) {
        //given
        var result = null;
        var expectedResult = {
            folders: [],
            preparations: [],
            datasets: []
        };
        $httpBackend
            .expectGET(RestURLs.inventoryUrl + '/search?name=test')
            .respond(200, expectedResult);

        //when
        InventoryRestService.search('test', $q.defer()).then(function (response) {
            result = response.data;
        });

        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(result).toEqual(expectedResult);
    }));

});