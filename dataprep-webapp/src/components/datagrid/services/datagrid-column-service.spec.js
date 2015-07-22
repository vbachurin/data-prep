describe('Datagrid column service', function () {
    'use strict';

    var gridMock, columnsMetadata;

    beforeEach(module('data-prep.datagrid'));

    beforeEach(inject(function () {
        columnsMetadata = [
            {id: '0000', name: 'col0', type: 'string'},
            {id: '0001', name: 'col1', type: 'integer'},
            {id: '0002', name: 'col2', type: 'string'}
        ];

        /*global SlickGridMock:false */
        gridMock = new SlickGridMock();
    }));

    describe('on creation', function () {
        it('should add column reorder handler', inject(function (DatagridColumnService) {
            //given
            spyOn(gridMock.onColumnsReordered, 'subscribe').and.returnValue();

            //when
            DatagridColumnService.init(gridMock);

            //then
            expect(gridMock.onColumnsReordered.subscribe).toHaveBeenCalled();
        }));
    });

    describe('update columns', function() {
        var headers = [
            {element: {detach: function() {}}},
            {element: {detach: function() {}}},
            {element: {detach: function() {}}}];
        var formatter = function() {};

        beforeEach(inject(function(DatagridColumnService, DatagridStyleService) {
            DatagridColumnService.init(gridMock);
            headers.forEach(function(header) {
                spyOn(header.element, 'detach').and.returnValue();
            });

            spyOn(DatagridStyleService, 'getColumnPreviewStyle').and.returnValue('');
            spyOn(DatagridStyleService, 'columnFormatter').and.returnValue(formatter);
        }));

        it('should detach all header directives', inject(function(DatagridColumnService) {
            //given
            DatagridColumnService.colHeaderElements = headers;

            //when
            DatagridColumnService.updateColumns([], true, false);

            //then
            headers.forEach(function(header) {
                expect(header.element.detach).toHaveBeenCalled();
            });
        }));

        it('should create and set new preview grid columns', inject(function(DatagridColumnService) {
            //when
            DatagridColumnService.updateColumns(columnsMetadata, true, false);

            //then
            var gridColumns = gridMock.getColumns();
            expect(gridColumns[0].id).toEqual('0000');
            expect(gridColumns[0].field).toEqual('0000');
            expect(gridColumns[0].name).toEqual('<div class="grid-header ">   <div class="grid-header-title dropdown-button ng-binding">col0</div>       <div class="grid-header-type ng-binding">text</div>   </div><div class="quality-bar"><div class="record-unknown"></div></div>');
            expect(gridColumns[0].formatter).toEqual(formatter);
            expect(gridColumns[0].minWidth).toEqual(80);
            expect(gridColumns[0].tdpColMetadata).toEqual({ id: '0000', name: 'col0', type: 'string' } );

            expect(gridColumns[1].id).toEqual('0001');
            expect(gridColumns[1].field).toEqual('0001');
            expect(gridColumns[1].name).toEqual('<div class="grid-header ">   <div class="grid-header-title dropdown-button ng-binding">col1</div>       <div class="grid-header-type ng-binding">number</div>   </div><div class="quality-bar"><div class="record-unknown"></div></div>');
            expect(gridColumns[1].formatter).toEqual(formatter);
            expect(gridColumns[1].minWidth).toEqual(80);
            expect(gridColumns[1].tdpColMetadata).toEqual({ id: '0001', name: 'col1', type: 'integer' } );

            expect(gridColumns[2].id).toEqual('0002');
            expect(gridColumns[2].field).toEqual('0002');
            expect(gridColumns[2].name).toEqual('<div class="grid-header ">   <div class="grid-header-title dropdown-button ng-binding">col2</div>       <div class="grid-header-type ng-binding">text</div>   </div><div class="quality-bar"><div class="record-unknown"></div></div>');
            expect(gridColumns[2].formatter).toEqual(formatter);
            expect(gridColumns[2].minWidth).toEqual(80);
            expect(gridColumns[2].tdpColMetadata).toEqual({ id: '0002', name: 'col2', type: 'string' } );
        }));

        it('should create and set new grid columns', inject(function(DatagridColumnService) {
            //when
            DatagridColumnService.updateColumns(columnsMetadata, false, false);

            //then
            var gridColumns = gridMock.getColumns();
            expect(gridColumns[0].id).toEqual('0000');
            expect(gridColumns[0].field).toEqual('0000');
            expect(gridColumns[0].name).toEqual('<div id="datagrid-header-0"></div>');
            expect(gridColumns[0].formatter).toEqual(formatter);
            expect(gridColumns[0].minWidth).toEqual(80);
            expect(gridColumns[0].tdpColMetadata).toEqual({ id: '0000', name: 'col0', type: 'string' } );

            expect(gridColumns[1].id).toEqual('0001');
            expect(gridColumns[1].field).toEqual('0001');
            expect(gridColumns[1].name).toEqual('<div id="datagrid-header-1"></div>');
            expect(gridColumns[1].formatter).toEqual(formatter);
            expect(gridColumns[1].minWidth).toEqual(80);
            expect(gridColumns[1].tdpColMetadata).toEqual({ id: '0001', name: 'col1', type: 'integer' } );

            expect(gridColumns[2].id).toEqual('0002');
            expect(gridColumns[2].field).toEqual('0002');
            expect(gridColumns[2].name).toEqual('<div id="datagrid-header-2"></div>');
            expect(gridColumns[2].formatter).toEqual(formatter);
            expect(gridColumns[2].minWidth).toEqual(80);
            expect(gridColumns[2].tdpColMetadata).toEqual({ id: '0002', name: 'col2', type: 'string' } );
        }));

        it('should fill colHeaderElements with datagrid headers', inject(function(DatagridColumnService) {
            //given
            expect(DatagridColumnService.colHeaderElements).toEqual([]);

            //when
            DatagridColumnService.updateColumns(columnsMetadata, false, false);

            //then
            expect(DatagridColumnService.colHeaderElements.length).toBe(3);

            columnsMetadata.forEach(function(colMetadata, index) {
                expect(DatagridColumnService.colHeaderElements[index].element).toBeDefined();
                expect(DatagridColumnService.colHeaderElements[index].scope).toBeDefined();
                expect(DatagridColumnService.colHeaderElements[index].scope.column).toBe(colMetadata);
                expect(DatagridColumnService.colHeaderElements[index].column).toBe(colMetadata);
            });
        }));

        it('should reuse same column header and update column metadata', inject(function(DatagridColumnService) {
            //given
            var newColumnsMetadata = [
                {id: '0000', name: 'col0', type: 'string'}, //Test case : reuse it
                {id: '0001', name: 'NewCol1Name', type: 'integer'}, //Test case : reuse it
                {id: '0002Bis', name: 'NewColName2', type: 'integer'}
            ];

            DatagridColumnService.updateColumns(columnsMetadata, false, false);
            var oldColHeaderElements = DatagridColumnService.colHeaderElements;
            expect(oldColHeaderElements.length).toBe(3);

            oldColHeaderElements.forEach(function(header) {
                spyOn(header.scope, '$destroy').and.returnValue();
                spyOn(header.element, 'remove').and.returnValue();
                spyOn(header.element, 'detach').and.returnValue();
            });

            //when
            DatagridColumnService.updateColumns(newColumnsMetadata, false, false);

            //then
            expect(DatagridColumnService.colHeaderElements.length).toBe(3);
            expect(DatagridColumnService.colHeaderElements[0]).toBe(oldColHeaderElements[0]);
            expect(DatagridColumnService.colHeaderElements[0].scope.column).toBe(newColumnsMetadata[0]);
            expect(DatagridColumnService.colHeaderElements[1]).toBe(oldColHeaderElements[1]);
            expect(DatagridColumnService.colHeaderElements[1].scope.column).toBe(newColumnsMetadata[1]);
            expect(DatagridColumnService.colHeaderElements[2]).not.toBe(oldColHeaderElements[2]);
        }));

        it('should delete unused header and create a new one for the new column', inject(function(DatagridColumnService) {
            //given
            var newColumnsMetadata = [
                {id: '0000', name: 'col0', type: 'string'},
                {id: '0001', name: 'NewCol1Name', type: 'integer'},
                {id: '0002Bis', name: 'NewColName2', type: 'integer'} //Test case : delete it and create a new one
            ];

            DatagridColumnService.updateColumns(columnsMetadata, false, false);
            var oldColHeaderElements = DatagridColumnService.colHeaderElements;
            expect(oldColHeaderElements.length).toBe(3);

            oldColHeaderElements.forEach(function(header) {
                spyOn(header.scope, '$destroy').and.returnValue();
                spyOn(header.element, 'remove').and.returnValue();
                spyOn(header.element, 'detach').and.returnValue();
            });

            //when
            DatagridColumnService.updateColumns(newColumnsMetadata, false, false);

            //then
            expect(oldColHeaderElements[0].scope.$destroy).not.toHaveBeenCalled();
            expect(oldColHeaderElements[0].element.remove).not.toHaveBeenCalled();
            expect(oldColHeaderElements[1].scope.$destroy).not.toHaveBeenCalled();
            expect(oldColHeaderElements[1].element.remove).not.toHaveBeenCalled();
            expect(oldColHeaderElements[2].scope.$destroy).toHaveBeenCalled();
            expect(oldColHeaderElements[2].element.remove).toHaveBeenCalled();
        }));

        it('should force column recreation', inject(function(DatagridColumnService) {
            //given
            var newColumnsMetadata = [
                {id: '0000', name: 'col0', type: 'string'},
                {id: '0001', name: 'NewCol1Name', type: 'integer'},
                {id: '0002Bis', name: 'NewColName2', type: 'integer'}
            ];
            var forceCreation = true;

            DatagridColumnService.updateColumns(columnsMetadata, false, false);
            var oldColHeaderElements = DatagridColumnService.colHeaderElements;
            expect(oldColHeaderElements.length).toBe(3);

            oldColHeaderElements.forEach(function(header) {
                spyOn(header.scope, '$destroy').and.returnValue();
                spyOn(header.element, 'remove').and.returnValue();
                spyOn(header.element, 'detach').and.returnValue();
            });

            //when
            DatagridColumnService.updateColumns(newColumnsMetadata, false, forceCreation);

            //then
            expect(oldColHeaderElements[0].scope.$destroy).toHaveBeenCalled();
            expect(oldColHeaderElements[0].element.remove).toHaveBeenCalled();
            expect(oldColHeaderElements[1].scope.$destroy).toHaveBeenCalled();
            expect(oldColHeaderElements[1].element.remove).toHaveBeenCalled();
            expect(oldColHeaderElements[2].scope.$destroy).toHaveBeenCalled();
            expect(oldColHeaderElements[2].element.remove).toHaveBeenCalled();
        }));
    });

    describe('on column reorder event', function() {
        var headers;

        beforeEach(inject(function(DatagridColumnService) {
            spyOn(gridMock.onColumnsReordered, 'subscribe').and.returnValue();

            headers = [
                {element: {remove: function() {}}, scope: {$destroy: function() {}}},
                {element: {remove: function() {}}, scope: {$destroy: function() {}}},
                {element: {remove: function() {}}, scope: {$destroy: function() {}}}];
            headers.forEach(function(header) {
                spyOn(header.element, 'remove').and.returnValue();
                spyOn(header.scope, '$destroy').and.returnValue();
            });

            DatagridColumnService.init(gridMock);
            DatagridColumnService.colHeaderElements = headers.slice(0);
        }));

        it('should remove and destroy all existing headers', inject(function(DatagridService) {
            //given
            DatagridService.data = {columns: columnsMetadata};

            //when
            var onColumnsReordered = gridMock.onColumnsReordered.subscribe.calls.argsFor(0)[0];
            onColumnsReordered();

            //then
            headers.forEach(function(header) {
                expect(header.element.remove).toHaveBeenCalled();
                expect(header.scope.$destroy).toHaveBeenCalled();
            });
        }));

        it('should create new headers', inject(function(DatagridService, DatagridColumnService) {
            //given
            DatagridService.data = {columns: columnsMetadata};

            //when
            var onColumnsReordered = gridMock.onColumnsReordered.subscribe.calls.argsFor(0)[0];
            onColumnsReordered();

            //then
            expect(DatagridColumnService.colHeaderElements.length).toBe(3);
            expect(DatagridColumnService.colHeaderElements[0].scope.column).toBe(columnsMetadata[0]);
            expect(DatagridColumnService.colHeaderElements[0].column).toBe(columnsMetadata[0]);
            expect(DatagridColumnService.colHeaderElements[0].element).toBeDefined();
            expect(DatagridColumnService.colHeaderElements[1].scope.column).toBe(columnsMetadata[1]);
            expect(DatagridColumnService.colHeaderElements[1].column).toBe(columnsMetadata[1]);
            expect(DatagridColumnService.colHeaderElements[1].element).toBeDefined();
            expect(DatagridColumnService.colHeaderElements[2].scope.column).toBe(columnsMetadata[2]);
            expect(DatagridColumnService.colHeaderElements[2].column).toBe(columnsMetadata[2]);
            expect(DatagridColumnService.colHeaderElements[2].element).toBeDefined();
        }));
    });
});
