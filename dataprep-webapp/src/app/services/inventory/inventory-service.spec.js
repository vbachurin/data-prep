/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Inventory Service', function () {
    'use strict';

    var results = {
        data: {
            folders: [{name : 'folder test', lastModificationDate: 1}],
            preparations: [{name : 'prep test', lastModificationDate: 2}],
            datasets: [{name : 'dataset test', lastModificationDate: 3}]

        }
    };

    beforeEach(angular.mock.module('data-prep.services.inventory'));

    beforeEach(inject(($q, InventoryRestService) => {
        spyOn(InventoryRestService, 'search').and.returnValue($q.when(results));
    }));

    it('should call inventory search rest service and process data', inject(function ($rootScope, InventoryService) {
        //given
        var result= null;
        var expectedResult = [
            {name : 'dataset <span class="highlighted">test</span>', lastModificationDate: 3, inventoryType: 'dataset'},
            {name : 'prep <span class="highlighted">test</span>', lastModificationDate: 2, inventoryType: 'preparation'},
            {name : 'folder <span class="highlighted">test</span>', lastModificationDate: 1, inventoryType: 'folder'}
        ];
        //when
        InventoryService.search('test').then(function (response) {
            result = response;
        });

        $rootScope.$digest();

        //then
        expect(result).toEqual(expectedResult);
    }));
});