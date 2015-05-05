describe('DatasetColumnHeader controller', function () {
    'use strict';

    var createController, scope;
    var metadata = {
        id: 'ef4509c8c083df4'
    };
    var column = {
        id: '8c083df4ef4509c'
    };
    var menusMock = function() {
        return [
            {
                'name': 'uppercase',
                'category': 'case',
                items: [],
                parameters: [
                    {name: 'column_name', type: 'string'}
                ]
            },
            {
                'name': 'lowercase',
                'category': 'case',
                items: [],
                parameters: [
                    {name: 'column_name', type: 'string'}
                ]
            },
            {
                'name': 'withParam',
                'category': 'case',
                items: [],
                'parameters': [
                    {
                        'name': 'param',
                        'type': 'string',
                        'default': '.'
                    }
                ]
            },
            {
                'name': 'split',
                'category': 'split',
                parameters: [
                    {name: 'column_name', type: 'string'}
                ],
                'items': [{
                    name: 'mode',
                    values: [
                        {
                            name: 'noparam'
                        },
                        {
                            name: 'regex',
                            'parameters': [
                                {
                                    'name': 'regexp',
                                    'type': 'string',
                                    'default': '.'
                                }
                            ]
                        },
                        {
                            name: 'index',
                            'parameters': [
                                {
                                    'name': 'index',
                                    'type': 'integer',
                                    'default': '5'
                                }
                            ]
                        },
                        {
                            name: 'threeParams',
                            'parameters': [
                                {
                                    'name': 'index',
                                    'type': 'numeric',
                                    'default': '5'
                                },
                                {
                                    'name': 'index2',
                                    'type': 'float',
                                    'default': '5'
                                },
                                {
                                    'name': 'index3',
                                    'type': 'double',
                                    'default': '5'
                                }
                            ]
                        }
                    ]
                }]
            }
        ];
    };

    beforeEach(module('data-prep.datagrid-header'));

    beforeEach(inject(function($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('DatagridHeaderCtrl', {
                $scope: scope
            });
            ctrl.metadata = metadata;
            ctrl.column = column;
            return ctrl;
        };
    }));

    describe('with transformation list success', function() {
        beforeEach(inject(function ($q, TransformationRestService) {
            spyOn(TransformationRestService, 'getTransformations').and.returnValue($q.when({data: menusMock()}));
        }));

        it('should init grouped and divided transformation menu', inject(function($rootScope, TransformationRestService) {
            //given
            var ctrl = createController();

            //when
            ctrl.initTransformations();
            $rootScope.$digest();

            //then
            expect(TransformationRestService.getTransformations).toHaveBeenCalledWith(metadata.id, column.id);
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

        it('should not get transformations is transformations are already initiated', inject(function($rootScope, TransformationRestService) {
            //given
            var ctrl = createController();
            ctrl.initTransformations();
            $rootScope.$digest();

            //when
            ctrl.initTransformations();
            $rootScope.$digest();

            //then
            expect(TransformationRestService.getTransformations.calls.count()).toBe(1);
        }));
    });

    describe('with transformation list error', function() {
        beforeEach(inject(function ($q, TransformationRestService) {
            spyOn(TransformationRestService, 'getTransformations').and.returnValue($q.reject('server error'));
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
