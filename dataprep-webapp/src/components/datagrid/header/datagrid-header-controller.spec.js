describe('DatasetColumnHeader controller', function () {
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
                'category':'split',
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
                $scope: scope,
            }, true);

            ctrlFn.instance.column = col;
            return ctrlFn();
        };
    }));

    it('should calculate column quality', function() {
        //given
        var ctrl = createController();
        ctrl.column = {
            'id': 'MostPopulousCity',
            'quality': {
                'empty': 5,
                'invalid': 10,
                'valid': 72
            },
            'type': 'string'
        };

        //when
        ctrl.refreshQualityBar();

        //then
        expect(ctrl.column.total).toBe(87);
        expect(ctrl.column.quality.emptyPercent).toBe(6);
        expect(ctrl.column.quality.emptyPercentWidth).toBe(10);
        expect(ctrl.column.quality.invalidPercent).toBe(12);
        expect(ctrl.column.quality.invalidPercentWidth).toBe(12);
        expect(ctrl.column.quality.validPercent).toBe(82);
        expect(ctrl.column.quality.validPercentWidth).toBe(78);
    });

    it('should calculate column quality with 0 values', function() {
        //given
        var ctrl = createController();
        ctrl.column = {
            'id': 'MostPopulousCity',
            'quality': {
                'empty': 0,
                'invalid': 0,
                'valid': 100
            },
            'type': 'string'
        };

        //when
        ctrl.refreshQualityBar();

        //then
        expect(ctrl.column.total).toBe(100);
        expect(ctrl.column.quality.emptyPercent).toBe(0);
        expect(ctrl.column.quality.emptyPercentWidth).toBe(0);
        expect(ctrl.column.quality.invalidPercent).toBe(0);
        expect(ctrl.column.quality.invalidPercentWidth).toBe(0);
        expect(ctrl.column.quality.validPercent).toBe(100);
        expect(ctrl.column.quality.validPercentWidth).toBe(100);
    });

    describe('with transformation list success', function() {
        beforeEach(inject(function ($q, TransformationService) {
            spyOn(TransformationService, 'getTransformations').and.returnValue($q.when(menusMock()));
        }));

        it('should init grouped and divided transformation menu', inject(function($rootScope, TransformationService) {
            //given
            var ctrl = createController();

            //when
            ctrl.initTransformations();
            $rootScope.$digest();

            //then
            expect(TransformationService.getTransformations).toHaveBeenCalledWith(column);
            expect(ctrl.transformations.length).toBe(5);
            expect(ctrl.transformations[0].name).toBe('uppercase');
            expect(ctrl.transformations[1].name).toBe('lowercase');
            expect(ctrl.transformations[2].name).toBe('withParam');
            expect(ctrl.transformations[3].isDivider).toBe(true);
            expect(ctrl.transformations[4].name).toBe('split');
        }));

        it('should adapt params types to input type', inject(function($rootScope) {
            //given
            var ctrl = createController();

            //when
            ctrl.initTransformations();
            $rootScope.$digest();

            //then
            expect(ctrl.transformations[2].parameters[0].inputType).toBe('text');
            expect(ctrl.transformations[4].items[0].values[1].parameters[0].inputType).toBe('text');
            expect(ctrl.transformations[4].items[0].values[2].parameters[0].inputType).toBe('number');
            expect(ctrl.transformations[4].items[0].values[3].parameters[0].inputType).toBe('number');
            expect(ctrl.transformations[4].items[0].values[3].parameters[1].inputType).toBe('number');
            expect(ctrl.transformations[4].items[0].values[3].parameters[2].inputType).toBe('number');
        }));

        it('should not get transformations is transformations are already initiated', inject(function($rootScope, TransformationService) {
            //given
            var ctrl = createController();
            ctrl.initTransformations();
            $rootScope.$digest();

            //when
            ctrl.initTransformations();
            $rootScope.$digest();

            //then
            expect(TransformationService.getTransformations.calls.count()).toBe(1);
        }));
    });

    describe('with transformation list error', function() {
        beforeEach(inject(function ($q, TransformationService) {
            spyOn(TransformationService, 'getTransformations').and.returnValue($q.reject('server error'));
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

    it('should set the simplified column type name', function() {

        //given
        var stringColumn = {
            'name': 'bestColumnEver',
            'type': 'string'
        };

        //when
        var ctrl = createControllerFromColumn(stringColumn);

        //then
        expect(ctrl.column.simplifiedType).toBe('text');
    });
});
