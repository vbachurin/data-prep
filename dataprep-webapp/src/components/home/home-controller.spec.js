describe('Home controller', function() {
    'use strict';

    var ctrl, createController, scope, $httpBackend;

    beforeEach(module('data-prep.home'));

    beforeEach(inject(function ($injector, MessageService) {
        $httpBackend = $injector.get('$httpBackend');
        $httpBackend.when('GET', 'i18n/en.json').respond({});
        $httpBackend.when('GET', 'i18n/fr.json').respond({});

        spyOn(MessageService, 'success').and.callThrough();
        spyOn(MessageService, 'error').and.callThrough();
    }));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function() {
            return $controller('HomeCtrl', {
                $scope: scope
            });
        };
    }));

    it('should init upload list to empty array', function() {
        //when
        ctrl = createController();

        //then
        expect(ctrl.uploadingDatasets).toEqual([]);
    });

    it('should init displayed page', function() {
        //when
        ctrl = createController();

        //then
        expect(ctrl.selectedMenu).toBe(0);
    });

    describe('with created controller', function() {
        var uploadDefer;

        beforeEach(inject(function($q, DatasetService, DatasetListService) {
            ctrl = createController();
            ctrl.datasetFile = [{name: 'my dataset.csv'}];
            ctrl.datasetName = 'my cool dataset';

            uploadDefer = $q.defer();
            uploadDefer.promise.progress = function(callback) {
                uploadDefer.progressCb = callback;
                return uploadDefer.promise;
            };

            spyOn(DatasetService, 'fileToDataset').and.callThrough();
            spyOn(DatasetService, 'createDataset').and.returnValue(uploadDefer.promise);
            spyOn(DatasetService, 'updateDataset').and.returnValue(uploadDefer.promise);
            spyOn(DatasetListService, 'refreshDatasets').and.returnValue(null);
        }));

        it('should toggle right panel flag', function() {
            //given
            expect(ctrl.showRightPanel).toBe(true);

            //when
            ctrl.toggleRightPanel();

            //then
            expect(ctrl.showRightPanel).toBe(false);

            //when
            ctrl.toggleRightPanel();

            //then
            expect(ctrl.showRightPanel).toBe(true);
        });

        it('should remove file extension for name init and display name modal on step 1', function() {
            //given
            expect(ctrl.datasetNameModal).toBeFalsy();

            //when
            ctrl.uploadDatasetFile();

            //then
            expect(ctrl.datasetName).toBeTruthy('my dataset');
            expect(ctrl.datasetNameModal).toBeTruthy();
        });

        describe('step 2 with unique name', function() {
            beforeEach(inject(function(DatasetListService) {
                spyOn(DatasetListService, 'getDatasetByName').and.returnValue(null);
            }));

            it('should create dataset if name is unique', inject(function(MessageService, DatasetService, DatasetListService) {
                //given
                expect(ctrl.uploadingDatasets.length).toBe(0);
                ctrl.uploadDatasetName();
                expect(ctrl.uploadingDatasets.length).toBe(1);

                //when
                uploadDefer.resolve();
                scope.$digest();

                //then
                expect(DatasetService.createDataset).toHaveBeenCalled();
                expect(ctrl.uploadingDatasets.length).toBe(0);
                expect(DatasetListService.refreshDatasets).toHaveBeenCalled();
                expect(MessageService.success).toHaveBeenCalledWith('DATASET_CREATE_SUCCESS_TITLE', 'DATASET_CREATE_SUCCESS', {dataset: 'my cool dataset'});
            }));

            it('should update progress on create', inject(function(DatasetService) {
                //given
                ctrl.uploadDatasetName();
                expect(ctrl.uploadingDatasets[0].progress).toBeFalsy();

                var event = {
                    loaded: 140,
                    total: 200
                };

                //when
                uploadDefer.progressCb(event);
                scope.$digest();

                //then
                expect(DatasetService.createDataset).toHaveBeenCalled();
                expect(ctrl.uploadingDatasets[0].progress).toBe(70);
            }));

            it('should set error flag and show error toast', inject(function(DatasetService, MessageService) {
                //given
                ctrl.uploadDatasetName();
                expect(ctrl.uploadingDatasets[0].error).toBeFalsy();

                //when
                uploadDefer.reject();
                scope.$digest();

                //then
                expect(DatasetService.createDataset).toHaveBeenCalled();
                expect(ctrl.uploadingDatasets[0].error).toBeTruthy();
                expect(MessageService.error).toHaveBeenCalledWith('UPLOAD_ERROR_TITLE', 'UPLOAD_ERROR');
            }));
        });

        describe('step 2 with existing name', function() {
            var dataset = {
                name: 'my cool dataset'
            };
            var confirmDefer;

            beforeEach(inject(function ($q, DatasetListService, TalendConfirmService) {
                confirmDefer = $q.defer();

                spyOn(DatasetListService, 'getDatasetByName').and.returnValue(dataset);
                spyOn(TalendConfirmService, 'confirm').and.returnValue(confirmDefer.promise);
                spyOn(DatasetListService, 'getUniqueName').and.returnValue('my cool dataset (1)');
            }));

            it('should do nothing on confirm modal dismiss', inject(function (TalendConfirmService, DatasetService, DatasetListService) {
                //given
                ctrl.uploadDatasetName();

                //when
                confirmDefer.reject('dismiss');
                scope.$digest();

                //then
                expect(DatasetListService.getDatasetByName).toHaveBeenCalledWith(ctrl.datasetName);
                expect(TalendConfirmService.confirm).toHaveBeenCalledWith(null, [ 'UPDATE_EXISTING_DATASET' ], { dataset: 'my cool dataset' });
                expect(DatasetService.createDataset).not.toHaveBeenCalled();
                expect(DatasetService.updateDataset).not.toHaveBeenCalled();
            }));

            it('should create dataset with modified name', inject(function (MessageService, TalendConfirmService, DatasetService, DatasetListService) {
                //given
                ctrl.uploadDatasetName();

                //when
                confirmDefer.reject();
                scope.$digest();
                uploadDefer.resolve();
                scope.$digest();

                //then
                expect(DatasetService.fileToDataset).toHaveBeenCalledWith(ctrl.datasetFile[0], 'my cool dataset (1)');
                expect(DatasetListService.refreshDatasets).toHaveBeenCalled();
                expect(MessageService.success).toHaveBeenCalledWith('DATASET_CREATE_SUCCESS_TITLE', 'DATASET_CREATE_SUCCESS', {dataset : 'my cool dataset (1)'});
            }));

            it('should update existing dataset', inject(function (MessageService, TalendConfirmService, DatasetService, DatasetListService) {
                //given
                ctrl.uploadDatasetName();

                //when
                confirmDefer.resolve();
                scope.$digest();
                expect(ctrl.uploadingDatasets.length).toBe(1);
                uploadDefer.resolve();
                scope.$digest();

                //then
                expect(DatasetService.updateDataset).toHaveBeenCalled();
                expect(ctrl.uploadingDatasets.length).toBe(0);
                expect(DatasetListService.refreshDatasets).toHaveBeenCalled();
                expect(MessageService.success).toHaveBeenCalledWith('DATASET_UPDATE_SUCCESS_TITLE', 'DATASET_UPDATE_SUCCESS', {dataset : 'my cool dataset'});
            }));

            it('should set error flag and show error toast on update error', inject(function (MessageService, TalendConfirmService, DatasetService) {
                //given
                ctrl.uploadDatasetName();

                //when
                confirmDefer.resolve();
                scope.$digest();
                expect(ctrl.uploadingDatasets.length).toBe(1);
                uploadDefer.reject();
                scope.$digest();

                //then
                expect(DatasetService.updateDataset).toHaveBeenCalled();
                expect(ctrl.uploadingDatasets[0].error).toBeTruthy();
                expect(MessageService.error).toHaveBeenCalledWith('UPLOAD_ERROR_TITLE', 'UPLOAD_ERROR');
            }));

            it('should update progress on update', inject(function (TalendConfirmService, DatasetService) {
                //given
                ctrl.uploadDatasetName();
                confirmDefer.resolve();
                scope.$digest();
                expect(ctrl.uploadingDatasets[0].progress).toBeFalsy();

                var event = {
                    loaded: 140,
                    total: 200
                };

                //when
                uploadDefer.progressCb(event);
                scope.$digest();

                //then
                expect(DatasetService.updateDataset).toHaveBeenCalled();
                expect(ctrl.uploadingDatasets[0].progress).toBe(70);
            }));
        });
    });

});
