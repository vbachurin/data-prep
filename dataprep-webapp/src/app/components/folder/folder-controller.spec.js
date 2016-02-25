/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

describe('Folder controller', function () {
    'use strict';

    var createController, scope, stateMock;

    var sortList = [
        {id: 'name', name: 'NAME_SORT', property: 'name'},
        {id: 'date', name: 'DATE_SORT', property: 'created'}
    ];

    var orderList = [
        {id: 'asc', name: 'ASC_ORDER'},
        {id: 'desc', name: 'DESC_ORDER'}
    ];

    beforeEach(angular.mock.module('data-prep.folder', function($provide){
        stateMock = {
            inventory: {
                datasets: [],
                sortList: sortList,
                orderList: orderList,
                sort: sortList[1],
                order: orderList[1]
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(angular.mock.module('data-prep.folder'));

    beforeEach(inject(function($rootScope, $controller, $q, $state, FolderService, StateService) {
        scope = $rootScope.$new();

        createController = function () {
            return $controller('FolderCtrl', {
                $scope: scope
            });
        };
        spyOn($state, 'go');
        spyOn(FolderService, 'populateMenuChildren').and.returnValue($q.when(true));
        spyOn(StateService, 'setMenuChildren').and.returnValue();
    }));

    it('should call goToFolder service', inject(function ($state) {
        //when
        var ctrl = createController();
        ctrl.goToFolder({path: '', name: 'HOME'});
        scope.$digest();

        //then
        expect($state.go).toHaveBeenCalledWith('nav.index.datasets',{ folderPath: '' });
    }));

    it('should call populateMenuChildren service', inject(function (FolderService, StateService) {

        //when
        var ctrl = createController();
        ctrl.initMenuChildren();
        scope.$digest();

        //then
        expect(FolderService.populateMenuChildren).toHaveBeenCalled();
        expect(StateService.setMenuChildren).toHaveBeenCalledWith([]);
    }));
});
