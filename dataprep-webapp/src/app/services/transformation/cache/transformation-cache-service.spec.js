/*  ============================================================================

 Copyright (C) 2006-2016 Talend Inc. - www.talend.com

 This source code is available under agreement available at
 https://github.com/Talend/data-prep/blob/master/LICENSE

 You should have received a copy of the agreement
 along with this program; if not, write to Talend SA
 9 rue Pages 92150 Suresnes, France

 ============================================================================*/

describe('Transformation cache service', () => {
    'use strict';

    const transformationsMock = () => {
        return [
            {
                'name': 'uppercase',
                'category': 'case',
                'items': null,
                'parameters': null
            },
            {
                'name': 'rename',
                'category': 'columns',
                'items': null,
                'parameters': null
            },
            {
                'name': 'lowercase',
                'category': 'case',
                'items': null,
                'parameters': null
            },
            {
                'name': 'withParam',
                'category': 'case',
                'items': null,
                'parameters': [
                    {
                        'name': 'param',
                        'type': 'string',
                        'default': '.',
                        'inputType': 'text'
                    }
                ]
            },
            {
                'name': 'split',
                'category': 'columns',
                'parameters': null,
                'items': [
                    {
                        'name': 'mode',
                        'values': [
                            {
                                'name': 'noparam'
                            },
                            {
                                'name': 'regex',
                                'parameters': [
                                    {
                                        'name': 'regexp',
                                        'type': 'string',
                                        'default': '.',
                                        'inputType': 'text'
                                    }
                                ]
                            },
                            {
                                'name': 'index',
                                'parameters': [
                                    {
                                        'name': 'index',
                                        'type': 'integer',
                                        'default': '5',
                                        'inputType': 'number'
                                    }
                                ]
                            },
                            {
                                'name': 'threeParams',
                                'parameters': [
                                    {
                                        'name': 'index',
                                        'type': 'numeric',
                                        'default': '5',
                                        'inputType': 'number'
                                    },
                                    {
                                        'name': 'index2',
                                        'type': 'float',
                                        'default': '5',
                                        'inputType': 'number'
                                    },
                                    {
                                        'name': 'index3',
                                        'type': 'double',
                                        'default': '5',
                                        'inputType': 'number'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ];
    };
    const column = {id: '0002', name: 'Firstname'};

    beforeEach(angular.mock.module('data-prep.services.transformation'));

    describe('transformations', () => {
        beforeEach(inject(($q, TransformationService) => {
            spyOn(TransformationService, 'getColumnTransformations').and.returnValue($q.when(transformationsMock()));
            spyOn(TransformationService, 'getLineTransformations').and.returnValue($q.when(transformationsMock()));
        }));

        describe('column', () => {
            it('should call TransformationService when column is not in cache', inject(($rootScope, TransformationCacheService, TransformationService) => {
                //given
                let result = null;

                //when
                TransformationCacheService.getColumnTransformations(column)
                    .then((transformations) => result = transformations);
                $rootScope.$digest();

                //then
                expect(TransformationService.getColumnTransformations).toHaveBeenCalledWith(column);
                expect(result).toEqual(transformationsMock());
            }));

            it('should return the same result from cache', inject(($rootScope, TransformationCacheService, TransformationService) => {
                //given
                let oldResult = null;
                let newResult = null;
                TransformationCacheService.getColumnTransformations(column)
                    .then((transformations) => oldResult = transformations);

                expect(TransformationService.getColumnTransformations.calls.count()).toBe(1);

                //when
                TransformationCacheService.getColumnTransformations(column)
                    .then((transformations) => newResult = transformations);
                $rootScope.$digest();

                //then
                expect(newResult).toBe(oldResult);
                expect(TransformationService.getColumnTransformations.calls.count()).toBe(1);
            }));

            it('should remove all cache entries', inject(($rootScope, TransformationCacheService, TransformationService) => {
                //given
                TransformationCacheService.getColumnTransformations(column);
                $rootScope.$digest();

                expect(TransformationService.getColumnTransformations.calls.count()).toBe(1);

                //when
                TransformationCacheService.invalidateCache();

                TransformationCacheService.getColumnTransformations(column);
                $rootScope.$digest();

                //then
                expect(TransformationService.getColumnTransformations.calls.count()).toBe(2);
            }));
        });

        describe('line', () => {
            it('should call TransformationService when line is not in cache', inject(($rootScope, TransformationCacheService, TransformationService) => {
                //given
                let result = null;

                //when
                TransformationCacheService.getLineTransformations()
                    .then((transformations) => result = transformations);
                $rootScope.$digest();

                //then
                expect(TransformationService.getLineTransformations).toHaveBeenCalledWith();
                expect(result).toEqual(transformationsMock());
            }));

            it('should return the same result from cache', inject(($rootScope, TransformationCacheService, TransformationService) => {
                //given
                let oldResult = null;
                let newResult = null;
                TransformationCacheService.getLineTransformations()
                    .then((transformations) => oldResult = transformations);

                expect(TransformationService.getLineTransformations.calls.count()).toBe(1);

                //when
                TransformationCacheService.getLineTransformations()
                    .then((transformations) => newResult = transformations);
                $rootScope.$digest();

                //then
                expect(newResult).toBe(oldResult);
                expect(TransformationService.getLineTransformations.calls.count()).toBe(1);
            }));

            it('should remove all cache entries', inject(($rootScope, TransformationCacheService, TransformationService) => {
                //given
                TransformationCacheService.getLineTransformations(column);
                $rootScope.$digest();

                expect(TransformationService.getLineTransformations.calls.count()).toBe(1);

                //when
                TransformationCacheService.invalidateCache();

                TransformationCacheService.getLineTransformations(column);
                $rootScope.$digest();

                //then
                expect(TransformationService.getLineTransformations.calls.count()).toBe(2);
            }));
        });
    });

    describe('suggestions', () => {
        beforeEach(inject(($q, TransformationService) => {
            spyOn(TransformationService, 'getColumnSuggestions').and.returnValue($q.when(transformationsMock()));
        }));

        it('should call TransformationService when column is not in cache', inject(($rootScope, TransformationCacheService, TransformationService) => {
            //given
            let result = null;

            //when
            TransformationCacheService.getColumnSuggestions(column)
                .then((transformations) => result = transformations);
            $rootScope.$digest();

            //then
            expect(TransformationService.getColumnSuggestions).toHaveBeenCalledWith(column);
            expect(result).toEqual(transformationsMock());
        }));

        it('should return the same result from cache', inject(($rootScope, TransformationCacheService, TransformationService) => {
            //given
            let oldResult = null;
            let newResult = null;
            TransformationCacheService.getColumnSuggestions(column)
                .then((transformations) => oldResult = transformations);

            expect(TransformationService.getColumnSuggestions.calls.count()).toBe(1);

            //when
            TransformationCacheService.getColumnSuggestions(column)
                .then((transformations) => newResult = transformations);
            $rootScope.$digest();

            //then
            expect(newResult).toBe(oldResult);
            expect(TransformationService.getColumnSuggestions.calls.count()).toBe(1);
        }));

        it('should remove all cache entries', inject(($rootScope, TransformationCacheService, TransformationService) => {
            //given
            TransformationCacheService.getColumnSuggestions(column);
            $rootScope.$digest();

            expect(TransformationService.getColumnSuggestions.calls.count()).toBe(1);

            //when
            TransformationCacheService.invalidateCache();

            TransformationCacheService.getColumnSuggestions(column);
            $rootScope.$digest();

            //then
            expect(TransformationService.getColumnSuggestions.calls.count()).toBe(2);
        }));
    });

    it('should remove entry from cache when GET returns an error', inject(($rootScope, $q, TransformationService, TransformationCacheService) => {
        //given
        let callCount = 0;
        spyOn(TransformationService, 'getColumnTransformations').and.callFake(() => {
            return callCount++ < 1 ? $q.reject() : $q.when(transformationsMock());
        });

        TransformationCacheService.getColumnTransformations(column); //first call rejected
        $rootScope.$digest();

        expect(TransformationService.getColumnTransformations.calls.count()).toBe(1);

        //when
        TransformationCacheService.getColumnTransformations(column); //second call pass
        $rootScope.$digest();

        //then
        expect(TransformationService.getColumnTransformations.calls.count()).toBe(2);
    }));
});