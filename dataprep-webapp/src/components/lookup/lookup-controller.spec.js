describe('Lookup controller', function () {
    'use strict';

    var createController, scope, stateMock;
    var dsActions = [
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

    var params = {
        'column_id': 'mainGridColId',
        'column_name': 'mainGridColName',
        'filter': '',
        'lookup_ds_name': 'lookup_2',
        'lookup_ds_id': '9e739b88-5ec9-4b58-84b5-2127a7e2eac7',
        'lookup_ds_url': 'http://172.17.0.211:8080/datasets/9ee2eac7/content?metadata=true',
        'lookup_join_on': 'lookupGridColId',
        'lookup_join_on_name': 'lookupGridColName',
        'lookup_selected_cols': ['0002','0003']
    };


    beforeEach(module('data-prep.lookup', function ($provide) {
        stateMock = {
            playground: {
                grid: {
                    selectedColumn: {
                        id:'mainGridColId',
                        name:'mainGridColName'
                    }
                },
                lookup:{
                    selectedColumn: {
                        id:'lookupGridColId',
                        name:'lookupGridColName'
                    },
                    columnsToAdd: ['0002','0003'],
                    datasets:dsActions,
                    dataset:dsActions[0]
                }
            }
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            return $controller('LookupCtrl', {
                $scope: scope
            });
        };
    }));

    beforeEach(function () {
        jasmine.clock().install();
    });

    afterEach(function () {
        jasmine.clock().uninstall();
    });

    describe('Confirm button interaction', function(){
        it('should trigger lookup preview', inject(function (EarlyPreviewService) {
            //given
            var ctrl = createController();
            spyOn(EarlyPreviewService, 'earlyPreview').and.returnValue(function(){});

            //when
            ctrl.hoverSubmitBtn();

            //then
            expect(EarlyPreviewService.earlyPreview).toHaveBeenCalledWith(stateMock.playground.lookup.dataset, 'dataset');
        }));

        it('should submit lookup action', inject(function ($q, TransformationApplicationService, EarlyPreviewService) {
            //given
            var ctrl = createController();
            spyOn(TransformationApplicationService, 'append').and.returnValue($q.when(true));
            spyOn(EarlyPreviewService, 'activatePreview').and.returnValue();
            spyOn(EarlyPreviewService, 'deactivatePreview').and.returnValue();
            spyOn(EarlyPreviewService, 'cancelPendingPreview').and.returnValue();

            //when
            ctrl.submitLookup();
            expect(EarlyPreviewService.activatePreview).not.toHaveBeenCalled();
            scope.$digest();
            jasmine.clock().tick(500);

            //then
            expect(TransformationApplicationService.append).toHaveBeenCalledWith(stateMock.playground.lookup.dataset, 'dataset', params);
            expect(EarlyPreviewService.deactivatePreview).toHaveBeenCalled();
            expect(EarlyPreviewService.cancelPendingPreview).toHaveBeenCalled();
            expect(EarlyPreviewService.activatePreview).toHaveBeenCalled();
        }));

        it('should return the action name', inject(function () {
            //given
            var ctrl = createController();

            //when
            var label = ctrl.getDsName(dsActions[0]);
            //then
            expect(label).toBe('lookup_2');
        }));
    });
});