describe('Dataset Service', function () {
    'use strict';

    var $httpBackend;

    beforeEach(module('data-prep.services.dataset'));

    beforeEach(inject(function ($injector) {
        $httpBackend = $injector.get('$httpBackend');
    }));

    it('should adapt infos to dataset object for upload', inject(function (DatasetRestService) {
        //given
        var file = {
            path: '/path/to/file'
        };
        var name = 'myDataset';
        var id = 'e85afAa78556d5425bc2';

        //when
        var dataset = DatasetRestService.fileToDataset(file, name, id);

        //then
        expect(dataset.name).toBe(name);
        expect(dataset.progress).toBe(0);
        expect(dataset.file).toBe(file);
        expect(dataset.error).toBe(false);
        expect(dataset.id).toBe(id);
    }));

    it('should call dataset list rest service', inject(function ($rootScope, DatasetRestService, RestURLs) {
        //given
        var result = null;
        var datasets = [
            {name: 'Customers (50 lines)'},
            {name: 'Us states'},
            {name: 'Customers (1K lines)'}
        ];
        $httpBackend
            .expectGET(RestURLs.datasetUrl)
            .respond(200, datasets);

        //when
        DatasetRestService.getDatasets().then(function (response) {
            result = response.data;
        });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(result).toEqual(datasets);
    }));

    it('should call dataset creation rest service', inject(function ($rootScope, DatasetRestService, RestURLs) {
        //given
        var datasetId = null;
        var dataset = {name: 'my dataset', file: {path: '/path/to/file'}, error: false};

        $httpBackend
            .expectPOST(RestURLs.datasetUrl + '?name=my%20dataset')
            .respond(200, 'e85afAa78556d5425bc2');

        //when
        DatasetRestService.createDataset(dataset).then(function (res) {
            datasetId = res.data;
        });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(datasetId).toBe('e85afAa78556d5425bc2');
    }));

    it('should call dataset update rest service', inject(function ($rootScope, DatasetRestService, RestURLs) {
        //given
        var dataset = {name: 'my dataset', file: {path: '/path/to/file'}, error: false, id: 'e85afAa78556d5425bc2'};

        $httpBackend
            .expectPUT(RestURLs.datasetUrl + '/e85afAa78556d5425bc2?name=my%20dataset')
            .respond(200);

        //when
        DatasetRestService.updateDataset(dataset);
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        //expect PUT not to throw any exception
    }));

    it('should call dataset delete rest service', inject(function ($rootScope, DatasetRestService, RestURLs) {
        //given
        var dataset = {name: 'my dataset', file: {path: '/path/to/file'}, error: false, id: 'e85afAa78556d5425bc2'};

        $httpBackend
            .expectDELETE(RestURLs.datasetUrl + '/e85afAa78556d5425bc2')
            .respond(200);

        //when
        DatasetRestService.deleteDataset(dataset);
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        //expect DELETE not to throw any exception
    }));

    it('should call dataset get rest service', inject(function ($rootScope, DatasetRestService, RestURLs) {
        //given
        var result = null;
        var datasetId = 'e85afAa78556d5425bc2';
        var data = [{column: [], records: []}];

        $httpBackend
            .expectGET(RestURLs.datasetUrl + '/e85afAa78556d5425bc2?metadata=false')
            .respond(200, data);

        //when
        DatasetRestService.getDataFromId(datasetId, false).then(function (data) {
            result = data;
        });
        $httpBackend.flush();
        $rootScope.$digest();

        //then
        expect(result).toEqual(data);
    }));
});