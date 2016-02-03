/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

describe('Dataset List Service', function () {
    'use strict';

    var datasets, stateMock;

    function initDatasets() {
        datasets = [
            {
                'id': 'de3cc32a-b624-484e-b8e7-dab9061a009c',
                'name': 'customers_jso_light',
                'author': 'anonymousUser',
                'records': 15,
                'nbLinesHeader': 1,
                'nbLinesFooter': 0,
                'created': '03-30-2015 08:06'
            },
            {
                'id': '3b21388c-f54a-4334-9bef-748912d0806f',
                'name': 'customers_jso',
                'author': 'anonymousUser',
                'records': 1000,
                'nbLinesHeader': 1,
                'nbLinesFooter': 0,
                'created': '03-30-2015 07:35'
            },
            {
                'id': '4d0a2718-bec6-4614-ad6c-8b3b326ff6c7',
                'name': 'first_interactions',
                'author': 'anonymousUser',
                'records': 29379,
                'nbLinesHeader': 1,
                'nbLinesFooter': 0,
                'created': '03-30-2015 08:05'
            },
            {
                'id': '5e95be9e-88cd-4765-9ecc-ee48cc28b6d5',
                'name': 'first_interactions_400',
                'author': 'anonymousUser',
                'records': 400,
                'nbLinesHeader': 1,
                'nbLinesFooter': 0,
                'created': '03-30-2015 08:06'
            }
        ];
    }

    var sortList = [
        {id: 'name', name: 'NAME_SORT', property: 'name'},
        {id: 'date', name: 'DATE_SORT', property: 'created'}
    ];

    var orderList = [
        {id: 'asc', name: 'ASC_ORDER'},
        {id: 'desc', name: 'DESC_ORDER'}
    ];

    beforeEach(angular.mock.module('data-prep.services.dataset', function ($provide) {
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

    beforeEach(inject(function ($q, DatasetRestService, StateService) {
        initDatasets();

        spyOn(DatasetRestService, 'getDatasets').and.returnValue($q.when({data: datasets.slice(0)}));
        spyOn(DatasetRestService, 'create').and.returnValue($q.when(true));
        spyOn(DatasetRestService, 'import').and.returnValue($q.when(true));
        spyOn(DatasetRestService, 'update').and.returnValue($q.when(true));
        spyOn(DatasetRestService, 'delete').and.returnValue($q.when(true));
        spyOn(DatasetRestService, 'clone').and.returnValue($q.when(true));
        spyOn(DatasetRestService, 'processCertification').and.returnValue($q.when(true));

        spyOn(StateService, 'setDatasets').and.returnValue();
        spyOn(StateService, 'removeDataset').and.returnValue();
    }));

    it('should refresh dataset list', inject(function ($rootScope, DatasetListService, StateService) {
        //given
        stateMock.inventory.datasets = [{name: 'my dataset'}, {name: 'my second dataset'}];

        //when
        DatasetListService.refreshDatasets();
        $rootScope.$apply();

        //then
        expect(StateService.setDatasets).toHaveBeenCalledWith(datasets);
    }));

    it('should trigger another refresh when one is already pending with different sort condition', inject(function ($rootScope, DatasetListService, DatasetRestService, StateService) {
        //given
        stateMock.inventory.datasets = [{name: 'my dataset'}, {name: 'my second dataset'}];
        DatasetListService.refreshDatasets();

        //when
        DatasetListService.refreshDatasets();
        $rootScope.$apply();

        //then
        expect(StateService.setDatasets).toHaveBeenCalledWith(datasets);
        expect(DatasetRestService.getDatasets.calls.count()).toBe(2);
    }));

    it('should create dataset', inject(function ($rootScope, DatasetListService, DatasetRestService) {
        //given
        var dataset = {name: 'my dataset'};
        var folder = {id : '', path: '', name: 'Home'};

        //when
        DatasetListService.create(dataset, folder);
        $rootScope.$apply();

        //then
        expect(DatasetRestService.create).toHaveBeenCalledWith(dataset, folder);
    }));

    it('should import remote dataset', inject(function ($rootScope, DatasetListService, DatasetRestService) {
        //given
        var importParameters = {
            type: 'http',
            name: 'great remote dataset',
            url: 'moc.dnelat//:ptth'
        };
        var folder = {id : '', path: '', name: 'Home'};

        //when
        DatasetListService.importRemoteDataset(importParameters, folder);
        $rootScope.$apply();

        //then
        expect(DatasetRestService.import).toHaveBeenCalledWith(importParameters, folder);
    }));

    it('should refresh datasets list on dataset creation', inject(function ($rootScope, DatasetListService, DatasetRestService) {
        //given
        var dataset = {name: 'my dataset'};

        //when
        DatasetListService.create(dataset);
        $rootScope.$apply();

        //then
        expect(DatasetRestService.getDatasets).toHaveBeenCalled();
    }));

    it('should update dataset', inject(function ($rootScope, DatasetListService, DatasetRestService) {
        //given
        var dataset = {name: 'my dataset'};

        //when
        DatasetListService.update(dataset);
        $rootScope.$apply();

        //then
        expect(DatasetRestService.update).toHaveBeenCalledWith(dataset);
    }));

    it('should refresh datasets list on dataset update', inject(function ($rootScope, DatasetListService, DatasetRestService) {
        //given
        var dataset = {name: 'my dataset'};

        //when
        DatasetListService.update(dataset);
        $rootScope.$apply();

        //then
        expect(DatasetRestService.getDatasets).toHaveBeenCalled();
    }));

    it('should process certification on dataset', inject(function ($rootScope, DatasetListService, DatasetRestService) {
        //given
        var dataset = {id: '6a543545de46512bf8651c'};

        //when
        DatasetListService.processCertification(dataset);
        $rootScope.$apply();

        //then
        expect(DatasetRestService.processCertification).toHaveBeenCalledWith('6a543545de46512bf8651c');
    }));

    it('should refresh datasets list on dataset certification', inject(function ($rootScope, DatasetListService, DatasetRestService) {
        //given
        var dataset = {id: '6a543545de46512bf8651c'};

        //when
        DatasetListService.processCertification(dataset);
        $rootScope.$apply();

        //then
        expect(DatasetRestService.getDatasets).toHaveBeenCalled();
    }));

    it('should delete dataset', inject(function ($rootScope, DatasetListService, DatasetRestService) {
        //given
        stateMock.inventory.datasets = datasets.slice(0);

        //when
        DatasetListService.delete(datasets[0]);
        $rootScope.$apply();

        //then
        expect(DatasetRestService.delete).toHaveBeenCalledWith(datasets[0]);
    }));

    it('should remove dataset from its internal list', inject(function ($rootScope, DatasetListService, StateService) {
        //given
        stateMock.inventory.datasets = datasets.slice(0);

        //when
        DatasetListService.delete(datasets[0]);
        $rootScope.$apply();

        //then
        expect(StateService.removeDataset).toHaveBeenCalledWith(datasets[0]);
    }));

    it('should call refreshDatasets when datasetsPromise is false', inject(function (DatasetRestService, DatasetListService) {
        //when
        DatasetListService.getDatasetsPromise();

        //then
        expect(DatasetRestService.getDatasets).toHaveBeenCalled();
    }));

    it('should return datasetsPromise when datasetsPromise is not false', inject(function (DatasetRestService, DatasetListService) {

        //given
        stateMock.inventory.datasets = null;

        //when
        DatasetListService.getDatasetsPromise();
        DatasetListService.getDatasetsPromise();

        //then
        expect(DatasetRestService.getDatasets.calls.count()).toBe(1);
    }));

    it('should return datasetsPromise', inject(function (DatasetRestService, DatasetListService) {
        //when
        var datasetsPromise = DatasetListService.hasDatasetsPromise();

        //then
        expect(datasetsPromise).toBeFalsy();
    }));

    it('should call rest service clone', inject(function (DatasetRestService, DatasetListService) {
        var folder = {id: 'foo'};

        //when
        DatasetListService.clone(datasets[0], folder, 'beer');

        //then
        expect(DatasetRestService.clone).toHaveBeenCalledWith(datasets[0], folder, 'beer');
    }));
});