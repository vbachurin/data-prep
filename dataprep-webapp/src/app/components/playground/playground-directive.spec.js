/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

describe('Playground directive', function () {
    'use strict';

    var scope, createElement, element;
    var stateMock;

    var metadata = {
        'id': '12ce6c32-bf80-41c8-92e5-66d70f22ec1f',
        'name': 'US States',
        'author': 'anonymousUser',
        'created': '02-03-2015 14:52',
        'records': '3'
    };

    var $httpBackend;
    var datasetActions = [
        {
            'category': 'data_blending',
            'name': 'lookup',
            'parameters': [
                {
                    'name': 'column_id',
                    'type': 'string',
                    'default': ''
                },
                {
                    'name': 'filter',
                    'type': 'filter',
                    'default': ''
                },
                {
                    'name': 'lookup_ds_name',
                    'type': 'string',
                    'default': 'lookup_2'
                },
                {
                    'name': 'lookup_ds_id',
                    'type': 'string',
                    'default': '9e739b88-5ec9-4b58-84b5-2127a7e2eac7'
                },
                {
                    'name': 'lookup_ds_url',
                    'type': 'string',
                    'default': 'http://172.17.0.211:8080/datasets/9ee2eac7/content?metadata=true'
                },
                {
                    'name': 'lookup_join_on',
                    'type': 'string',
                    'default': ''
                },
                {
                    'name': 'lookup_join_on_name',
                    'type': 'string',
                    'default': ''
                },
                {
                    'name': 'lookup_selected_cols',
                    'type': 'list',
                    'default': ''
                }
            ]
        }
    ];

    var sortList = [
        {id: 'name', name: 'NAME_SORT', property: 'name'},
        {id: 'date', name: 'DATE_SORT', property: 'created'}
    ];

    var orderList = [
        {id: 'asc', name: 'ASC_ORDER'},
        {id: 'desc', name: 'DESC_ORDER'}
    ];

    beforeEach(angular.mock.module('data-prep.playground', function($provide) {
        stateMock = {
            playground: {
                visible: true,
                filter: {gridFilters: []},
                lookup: {
                    visibility: false,
                    datasets: [],
                    sortList: sortList,
                    orderList: orderList
                },
                grid: {
                    selectedColumn: {'id': '0001'},
                    selectedLine: {'0001': '1'}
                }
            },
            inventory: {
                datasets: [],
                sortList: sortList,
                orderList: orderList
            }
        };
        $provide.constant('state', stateMock);
    }));
    beforeEach(angular.mock.module('htmlTemplates'));

    beforeEach(inject(function ($rootScope, $compile, $q, $timeout, PreparationListService, PlaygroundService, ExportService) {
        scope = $rootScope.$new();

        createElement = function () {
            element = angular.element('<playground></playground>');
            $compile(element)(scope);
            scope.$digest();
        };
        spyOn(PlaygroundService, 'createOrUpdatePreparation').and.returnValue($q.when(true));
        spyOn(ExportService, 'refreshTypes').and.returnValue($q.when([]));
        spyOn(ExportService, 'getParameters').and.returnValue({});

    }));

    beforeEach(inject(function ($injector, RestURLs) {
        $httpBackend = $injector.get('$httpBackend');
        $httpBackend
            .expectGET(RestURLs.datasetActionsUrl+ '/' + metadata.id +'/actions')
            .respond(200, datasetActions);
    }));

    afterEach(() => {
        scope.$destroy();
        element.remove();
    });

    describe('suggestions', function() {
        it('should render right slidable panel', function () {
            //given
            stateMock.playground.dataset = metadata;

            //when
            createElement();

            //then: check right slidable is displayed transformations with right slide action
            expect(element.find('.playground-suggestions').eq(0).hasClass('slide-hide')).toBe(false);
            expect(element.find('.playground-suggestions').eq(0).find('.action').eq(0).hasClass('right')).toBe(true);
        });
    });

    describe('recipe header', function () {
        beforeEach(inject(function(StateService) {
            stateMock.playground.nameEditionMode = true;
            spyOn(StateService, 'setNameEditionMode').and.callFake(function(value) {
                stateMock.playground.nameEditionMode = value;
            });
        }));

        it('should render left slidable panel', function () {
            //given
            stateMock.playground.dataset = metadata;

            //when
            createElement();

            //then : check left slidable is hidden recipe with left slide action
            expect(element.find('.playground-recipe').eq(0).hasClass('slide-hide')).toBe(true);
            expect(element.find('.playground-recipe').eq(0).find('.action').eq(0).hasClass('right')).toBe(false);
        });

        it('should render editable text on preparation title', function () {
            //given
            stateMock.playground.preparation = {id: '3e41168465e15d4'};
            stateMock.playground.dataset = metadata;

            //when
            createElement();

            //then
            var title = element.find('.steps-header').eq(0).find('talend-editable-text');
            expect(title.length).toBe(1);

        });

        it('should toggle recipe on click on the On/Off switch', inject(function (RecipeBulletService, RecipeService) {
            //given
            stateMock.playground.recipe = {visible : true};
            var step = {
                inactive: false,
                transformation: {
                    stepId: '92771a304130e9',
                    name: 'propercase',
                    parameters: [],
                    items: [],
                    dynamic: false
                }
            };
            RecipeService.getRecipe().push(step);

            spyOn(RecipeBulletService, 'toggleRecipe').and.returnValue();

            createElement();
            var chkboxOnOff = element.find('.label-switch > input[type="checkbox"]');

            //when
            chkboxOnOff.trigger('click');

            //then
            expect(RecipeBulletService.toggleRecipe).toHaveBeenCalled();
        }));

        it('should switch OFF the On/Off switch when the 1st step is INACTIVE', inject(function (RecipeService) {
            //given
            stateMock.playground.dataset = metadata;
            var step = {
                inactive: false,
                transformation: {
                    stepId: '92771a304130e9',
                    name: 'propercase',
                    parameters: [],
                    items: [],
                    dynamic: false
                }
            };
            RecipeService.getRecipe().push(step);
            createElement();

            var chkboxOnOff = element.find('.label-switch > input[type="checkbox"]');
            expect(chkboxOnOff.prop('checked')).toBe(true);

            //when
            step.inactive = true;
            scope.$digest();

            //then
            expect(chkboxOnOff.prop('checked')).toBe(false);
        }));

        it('should switch ON the On/Off switch when the 1st step is ACTIVE', inject(function (RecipeService) {
            //given
            stateMock.playground.dataset = metadata;
            var step = {
                inactive: true,
                transformation: {
                    stepId: '92771a304130e9',
                    name: 'propercase',
                    parameters: [],
                    items: [],
                    dynamic: false
                }
            };
            RecipeService.getRecipe().push(step);
            createElement();

            var chkboxOnOff = element.find('.label-switch > input[type="checkbox"]');
            expect(chkboxOnOff.prop('checked')).toBe(false);

            //when
            step.inactive = false;
            scope.$digest();

            //then
            expect(chkboxOnOff.prop('checked')).toBe(true);
        }));
    });

    describe('dataset parameters', function() {
        it('should render dataset parameters', function () {
            //given
            stateMock.playground.dataset = metadata;

            //when
            createElement();

            //then : check dataset parameters is present
            var playground = element.find('.playground').eq(0);
            expect(playground.find('.dataset-parameters').length).toBe(1);
        });
    });

    describe('datagrid', function() {
        it('should render datagrid with filters', function () {
            //given
            stateMock.playground.dataset = metadata;

            //when
            createElement();

            //then : check datagrid and filters are present
            var playground = element.find('.playground').eq(0);
            expect(playground.eq(0).find('filter-bar').length).toBe(1);
            expect(playground.eq(0).find('filter-bar').find('#filter-search').length).toBe(1);
            expect(playground.eq(0).find('datagrid').length).toBe(1);
        });
    });
});