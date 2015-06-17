describe('Datagrid header controller', function () {
    'use strict';

    var createController, createControllerFromColumn, scope;
    var column = {
        id: '8c083df4ef4509c',
        type: 'string'
    };

    var menusMock = function() {
        return [
            {
                'name':'uppercase',
                'category':'case',
                'items':null,
                'parameters':null
            },
            {
                'name':'rename',
                'category':'columns',
                'items':null,
                'parameters':null
            },
            {
                'name':'lowercase',
                'category':'case',
                'items':null,
                'parameters':null
            },
            {
                'name':'withParam',
                'category':'case',
                'items':null,
                'parameters':[
                    {
                        'name':'param',
                        'type':'string',
                        'default':'.',
                        'inputType':'text'
                    }
                ]
            },
            {
                'name':'split',
                'category':'columns',
                'parameters':null,
                'items':[
                    {
                        'name':'mode',
                        'values':[
                            {
                                'name':'noparam'
                            },
                            {
                                'name':'regex',
                                'parameters':[
                                    {
                                        'name':'regexp',
                                        'type':'string',
                                        'default':'.',
                                        'inputType':'text'
                                    }
                                ]
                            },
                            {
                                'name':'index',
                                'parameters':[
                                    {
                                        'name':'index',
                                        'type':'integer',
                                        'default':'5',
                                        'inputType':'number'
                                    }
                                ]
                            },
                            {
                                'name':'threeParams',
                                'parameters':[
                                    {
                                        'name':'index',
                                        'type':'numeric',
                                        'default':'5',
                                        'inputType':'number'
                                    },
                                    {
                                        'name':'index2',
                                        'type':'float',
                                        'default':'5',
                                        'inputType':'number'
                                    },
                                    {
                                        'name':'index3',
                                        'type':'double',
                                        'default':'5',
                                        'inputType':'number'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ];
    };

    beforeEach(module('data-prep.datagrid-header'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            return createControllerFromColumn(column);
        };

        createControllerFromColumn = function (col) {
            var ctrlFn = $controller('DatagridHeaderCtrl', {
                $scope: scope
            }, true);

            ctrlFn.instance.column = col;
            return ctrlFn();
        };
    }));

    describe('with transformation list success', function() {
        beforeEach(inject(function ($q, TransformationCacheService) {
            spyOn(TransformationCacheService, 'getTransformations').and.returnValue($q.when(menusMock()));
        }));

        it('should filter and init only "columns" category', inject(function($rootScope, TransformationCacheService) {
            //given
            var ctrl = createController();

            //when
            ctrl.initTransformations();
            $rootScope.$digest();

            //then
            expect(TransformationCacheService.getTransformations).toHaveBeenCalledWith(column);
            expect(ctrl.transformations.length).toBe(2);
            expect(ctrl.transformations[0].name).toBe('rename');
            expect(ctrl.transformations[1].name).toBe('split');
        }));

        it('should not get transformations if transformations are already initiated', inject(function($rootScope, TransformationCacheService) {
            //given
            var ctrl = createController();
            ctrl.initTransformations();
            $rootScope.$digest();

            //when
            ctrl.initTransformations();
            $rootScope.$digest();

            //then
            expect(TransformationCacheService.getTransformations.calls.count()).toBe(1);
        }));
    });

    describe('with transformation list error', function() {
        beforeEach(inject(function ($q, TransformationCacheService) {
            spyOn(TransformationCacheService, 'getTransformations').and.returnValue($q.reject('server error'));
        }));

        it('should change inProgress and error flags', inject(function($rootScope) {
            //given
            var ctrl = createController();
            expect(ctrl.transformationsRetrieveError).toBeFalsy();
            expect(ctrl.initTransformationsInProgress).toBeFalsy();

            ctrl.transformationsRetrieveError = true;

            //when
            ctrl.initTransformations();
            expect(ctrl.initTransformationsInProgress).toBeTruthy();
            expect(ctrl.transformationsRetrieveError).toBeFalsy();
            $rootScope.$digest();

            //then
            expect(ctrl.transformationsRetrieveError).toBeTruthy();
            expect(ctrl.initTransformationsInProgress).toBeFalsy();
        }));
    });
});
