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
        beforeEach(inject(function ($q, InventoryService) {
            spyOn(InventoryService, 'search').and.returnValue($q.when({}));
        }));

        it('should call the easter eggs service', inject(function (InventoryService) {
            //when
            component.searchInput = 'barcelona';
            component.search();
            scope.$digest();

            //then
            expect(InventoryService.search).toHaveBeenCalledWith('barcelona');
            expect(component.results).toEqual({});
        }));

        it('should go to folder', inject(function ($state) {
            //given
            spyOn($state, 'go').and.returnValue();

            //when
            component.goToFolder({path: '1/2', name: '2'});

            //then
            expect($state.go).toHaveBeenCalledWith('nav.index.datasets', {folderPath : '1/2'});

        }));

        it('should load preparation and show playground', inject(function ($stateParams, $q, $state, $timeout, StateService) {
            //given
            spyOn($state, 'go').and.returnValue();
            spyOn(StateService, 'setPreviousState').and.returnValue();
            spyOn(StateService, 'setPreviousStateOptions').and.returnValue();

            var preparation = {
                id: 'de618c62ef97b3a95b5c171bc077ffe22e1d6f79',
                dataSetId: 'dacd45cf-5bd0-4768-a9b7-f6c199581efc',
                author: 'anonymousUser'
            };

            $stateParams.folderPath = 'test/';

            //when
            component.openPreparation(preparation);
            scope.$digest();
            $timeout.flush();

            //then
            expect(StateService.setPreviousState).toHaveBeenCalledWith('nav.index.datasets');
            expect(StateService.setPreviousStateOptions).toHaveBeenCalledWith({folderPath: 'test/'});
            expect($state.go).toHaveBeenCalledWith('playground.preparation', {prepid: preparation.id});
        }));

    });
});
