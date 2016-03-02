/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Inventory Search controller', function () {
    'use strict';

    var component, scope;

    beforeEach(angular.mock.module('data-prep.inventory-search'));

    beforeEach(inject(function($rootScope, $componentController) {
        scope = $rootScope.$new();
        component = $componentController('inventorySearch', {$scope: scope});
    }));

    describe('search ', function() {
        beforeEach(inject(function (EasterEggsService) {
            spyOn(EasterEggsService, 'enableEasterEgg').and.returnValue();
        }));

        it('should call the easter eggs service', inject(function (EasterEggsService) {
            //when
            component.searchInput = 'barcelona';
            component.search();

            //then
            expect(EasterEggsService.enableEasterEgg).toHaveBeenCalledWith('barcelona');
        }));

    });
});
