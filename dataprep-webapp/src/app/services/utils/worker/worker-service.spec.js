/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Worker service', () => {
    'use strict';

    var originalParallel;

    beforeEach(angular.mock.module('data-prep.services.utils'));

    beforeEach(inject(($window) => {
        originalParallel = $window.Parallel;
        $window.Parallel = ParallelMock;
    }));

    afterEach(inject(($window) => {
        $window.Parallel = originalParallel;
    }));

    it('should create a Parallel worker wrapper', inject((WorkerService) => {
        //given
        const parameters = {a: 5, b: 8};
        const options = {};

        //when
        const workerWrapper = WorkerService.create(parameters, options);

        //then
        expect(workerWrapper.operation).toBeDefined();
        expect(workerWrapper.operation.parameters).toBe(parameters);
        expect(workerWrapper.operation.options).toBe(options);
    }));

    describe('importScripts', () => {
        it('should add external script to Parallel operation', inject((WorkerService) => {
            //given
            const parameters = {a: 5, b: 8};
            const options = {};
            const workerWrapper = WorkerService.create(parameters, options);
            spyOn(workerWrapper.operation, 'require').and.returnValue();

            const scriptsUrl = 'my scripts';

            //when
            workerWrapper.importScripts(scriptsUrl);

            //then
            expect(workerWrapper.operation.require).toHaveBeenCalledWith(scriptsUrl);
        }));

        it('should return worker wrapper to allow fluid api', inject((WorkerService) => {
            //given
            const parameters = {a: 5, b: 8};
            const options = {};
            const workerWrapper = WorkerService.create(parameters, options);

            const scriptsUrl = 'my scripts';

            //when
            const result = workerWrapper.importScripts(scriptsUrl);

            //then
            expect(result).toBe(workerWrapper);
        }));
    });

    describe('require', () => {
        it('should add local function to Parallel operation', inject((WorkerService) => {
            //given
            const parameters = {a: 5, b: 8};
            const options = {};
            const workerWrapper = WorkerService.create(parameters, options);
            spyOn(workerWrapper.operation, 'require').and.returnValue();

            const fn = () => {};

            //when
            workerWrapper.require(fn);

            //then
            expect(workerWrapper.operation.require).toHaveBeenCalledWith(fn);
        }));

        it('should return worker wrapper to allow fluid api', inject((WorkerService) => {
            //given
            const parameters = {a: 5, b: 8};
            const options = {};
            const workerWrapper = WorkerService.create(parameters, options);

            const fn = () => {};

            //when
            const result = workerWrapper.require(fn);

            //then
            expect(result).toBe(workerWrapper);
        }));
    });

    describe('run', () => {
        it('should run main function and resolve promise', inject(($rootScope, $q, WorkerService) => {
            //given
            const parameters = {a: 5, b: 8};
            const options = {};
            const workerWrapper = WorkerService.create(parameters, options);
            spyOn(workerWrapper.operation, 'spawn').and.callFake((mainFn) => $q.when(mainFn(parameters)));

            const fn = (params) => params.a + params.b;
            let result = null;

            //when
            workerWrapper.run(fn)
                .then((res) => result = res);
            $rootScope.$digest();

            //then
            expect(result).toBe(13);
        }));
    });

    describe('cancel', () => {
        it('should cancel worker wrapper promise', inject(($rootScope, $q, WorkerService) => {
            //given
            const parameters = {a: 5, b: 8};
            const options = {};
            const workerWrapper = WorkerService.create(parameters, options);
            spyOn(workerWrapper.operation, 'spawn').and.callFake((mainFn) => $q.when(mainFn(parameters)));

            const fn = (params) => params.a + params.b;
            let result = null;

            //when
            workerWrapper.run(fn)
                .then((res) => result = res)
                .catch((err) => result = err);
            workerWrapper.cancel();
            $rootScope.$digest();

            //then
            expect(result).toBe('user cancel');
        }));
    });
});