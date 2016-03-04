describe('Transformation Application Service', function () {
    'use strict';
    var stateMock;

    beforeEach(angular.mock.module('data-prep.services.transformation', ($provide) => {
        stateMock = {playground: {grid: {}}};
        stateMock.playground.filter = {
            applyTransformationOnFilters: true,
            gridFilters: [88]
        };
        $provide.constant('state', stateMock);
    }));

    beforeEach(inject(($q, PlaygroundService, FilterAdapterService) => {
        spyOn(PlaygroundService, 'appendStep').and.returnValue();
        spyOn(FilterAdapterService, 'toTree').and.returnValue({
            filter: {
                eq: {
                    field: '0001',
                    value: 'john'
                }
            }
        });
    }));

    describe('Append Step', () => {
        it('should call appendStep with column', inject((TransformationApplicationService, PlaygroundService) => {
            //given
            var transformation = {name: 'tolowercase'};
            var scope = 'column';
            var params = {param: 'value'};
            stateMock.playground.grid.selectedColumn = {id: '0001', name: 'firstname'};
            stateMock.playground.filter.applyTransformationOnFilters = false;

            //when
            TransformationApplicationService.append(transformation, scope, params);

            //then
            var expectedParams = {
                param: 'value',
                scope: 'column',
                column_id: '0001',
                column_name: 'firstname',
                row_id: undefined
            };
            expect(PlaygroundService.appendStep).toHaveBeenCalledWith('tolowercase', expectedParams);
        }));

        it('should call appendStep with row', inject((TransformationApplicationService, PlaygroundService) => {
            //given
            var transformation = {name: 'tolowercase'};
            var scope = 'line';
            var params = {param: 'value'};
            stateMock.playground.grid.selectedLine = {tdpId: 125};
            stateMock.playground.filter.applyTransformationOnFilters = false;

            //when
            TransformationApplicationService.append(transformation, scope, params);

            //then
            var expectedParams = {
                param: 'value',
                scope: 'line',
                column_id: undefined,
                column_name: undefined,
                row_id: 125
            };
            expect(PlaygroundService.appendStep).toHaveBeenCalledWith('tolowercase', expectedParams);
        }));

        it('should call appendStep without param', inject((TransformationApplicationService, PlaygroundService) => {
            //given
            var transformation = {name: 'tolowercase'};
            var scope = 'column';
            stateMock.playground.grid.selectedColumn = {id: '0001', name: 'firstname'};
            stateMock.playground.filter.applyTransformationOnFilters = false;

            //when
            TransformationApplicationService.append(transformation, scope);

            //then
            var expectedParams = {
                scope: 'column',
                column_id: '0001',
                column_name: 'firstname',
                row_id: undefined
            };
            expect(PlaygroundService.appendStep).toHaveBeenCalledWith('tolowercase', expectedParams);
        }));

        it('should call appendStep with filter', inject((TransformationApplicationService, PlaygroundService) => {
            //given
            var transformation = {name: 'tolowercase'};
            var scope = 'column';
            stateMock.playground.grid.selectedColumn = {id: '0001', name: 'firstname'};
            stateMock.playground.filter.applyTransformationOnFilters = true;

            //when
            TransformationApplicationService.append(transformation, scope);

            //then
            var expectedParams = {
                scope: 'column',
                column_id: '0001',
                column_name: 'firstname',
                row_id: undefined,
                filter: {
                    eq: {
                        field: '0001',
                        value: 'john'
                    }
                }
            };
            expect(PlaygroundService.appendStep).toHaveBeenCalledWith('tolowercase', expectedParams);
        }));

        it('should create an append closure', inject((TransformationApplicationService, PlaygroundService) => {
            //given
            var transformation = {name: 'tolowercase'};
            var scope = 'column';
            var params = {param: 'value'};
            stateMock.playground.grid.selectedColumn = {id: '0001', name: 'firstname'};
            stateMock.playground.grid.selectedLine = {tdpId: 125};
            stateMock.playground.filter.applyTransformationOnFilters = false;

            //when
            var closure = TransformationApplicationService.appendClosure(transformation, scope);
            closure(params);

            //then
            var expectedParams = {
                param: 'value',
                scope: 'column',
                column_id: '0001',
                column_name: 'firstname',
                row_id: 125
            };
            expect(PlaygroundService.appendStep).toHaveBeenCalledWith('tolowercase', expectedParams);
        }));
    });

    describe('Edit Cell', () => {
        /*       it('should append cell edition step', inject(function ($rootScope, PlaygroundService, PreparationService, FilterAdapterService) {
         //given
         var preparationId = '64f3543cd466f545';
         stateMock.playground.preparation = {id: preparationId};

         var type = 'inside_range';
         var colId = '0001';
         var args = {
         interval: [1000, 2000],
         type: 'integer',
         label: '[1,000 .. 2,000['
         };
         var filter = FilterAdapterService.createFilter(type, colId, null, null, args, null, null);

         stateMock.playground.filter = {
         applyTransformationOnFilters : true,
         gridFilters : [filter]
         };

         var rowItem = {tdpId: 58, '0000': 'McDonald', '0001': 'Ronald'};
         var column = {id: '0001', name: 'firstname'};
         var newValue = 'Donald';
         var updateAllCellWithValue = false;

         //when
         PlaygroundService.editCell(rowItem, column, newValue, updateAllCellWithValue);
         $rootScope.$digest();

         //then
         var expectedParams = {
         scope: 'cell',
         column_id: '0001',
         column_name: 'firstname',
         row_id: 58,
         cell_value: {
         token: 'Ronald',
         operator: 'equals'
         },
         replace_value: 'Donald',
         filter:{
         range:{
         field:'0001',
         start:1000,
         end:2000,
         type:'integer',
         label:'[1,000 .. 2,000['
         }
         }
         };
         expect(PreparationService.appendStep).toHaveBeenCalledWith(
         preparationId,
         {action: 'replace_on_value', parameters: expectedParams}
         );
         }));
         */
        it('should append step on cell scope', inject((TransformationApplicationService, PlaygroundService) => {
            //given
            const rowItem = {tdpId: 58, '0000': 'McDonald', '0001': 'Ronald'};
            const column = {id: '0001', name: 'firstname'};
            const newValue = 'Donald';
            const updateAllCellWithValue = false; // only selected cell

            stateMock.playground.grid.selectedLine = {tdpId: 58};
            stateMock.playground.grid.selectedColumn = {id: '0001', name: 'firstname'};
            stateMock.playground.filter.applyTransformationOnFilters = false;

            //when
            TransformationApplicationService.editCell(rowItem, column, newValue, updateAllCellWithValue);

            //then
            const expectedParams = {
                cell_value: {
                    token: 'Ronald',
                    operator: 'equals'
                },
                replace_value: 'Donald',
                scope: 'cell',
                row_id: 58,
                column_id: '0001',
                column_name: 'firstname'
            };
            expect(PlaygroundService.appendStep).toHaveBeenCalledWith('replace_on_value', expectedParams);
        }));

        it('should append step on column scope', inject((TransformationApplicationService, PlaygroundService) => {
            //given
            const rowItem = {tdpId: 58, '0000': 'McDonald', '0001': 'Ronald'};
            const column = {id: '0001', name: 'firstname'};
            const newValue = 'Donald';
            const updateAllCellWithValue = true; // all cells in column

            stateMock.playground.grid.selectedLine = {tdpId: 58};
            stateMock.playground.grid.selectedColumn = {id: '0001', name: 'firstname'};
            stateMock.playground.filter.applyTransformationOnFilters = false;

            //when
            TransformationApplicationService.editCell(rowItem, column, newValue, updateAllCellWithValue);

            //then
            const expectedParams = {
                cell_value: {
                    token: 'Ronald',
                    operator: 'equals'
                },
                replace_value: 'Donald',
                scope: 'column',
                row_id: 58,
                column_id: '0001',
                column_name: 'firstname'
            };
            expect(PlaygroundService.appendStep).toHaveBeenCalledWith('replace_on_value', expectedParams);
        }));

        it('should append step with filters', inject((TransformationApplicationService, PlaygroundService) => {
            //given
            const rowItem = {tdpId: 58, '0000': 'McDonald', '0001': 'Ronald'};
            const column = {id: '0001', name: 'firstname'};
            const newValue = 'Donald';
            const updateAllCellWithValue = true;

            stateMock.playground.grid.selectedLine = {tdpId: 58};
            stateMock.playground.grid.selectedColumn = {id: '0001', name: 'firstname'};
            stateMock.playground.filter.applyTransformationOnFilters = true; // apply on filter

            //when
            TransformationApplicationService.editCell(rowItem, column, newValue, updateAllCellWithValue);

            //then
            const expectedParams = {
                cell_value: {
                    token: 'Ronald',
                    operator: 'equals'
                },
                replace_value: 'Donald',
                scope: 'column',
                row_id: 58,
                column_id: '0001',
                column_name: 'firstname',
                filter: {
                    eq: {
                        field: '0001',
                        value: 'john'
                    }
                }
            };
            expect(PlaygroundService.appendStep).toHaveBeenCalledWith('replace_on_value', expectedParams);
        }));
    });
});