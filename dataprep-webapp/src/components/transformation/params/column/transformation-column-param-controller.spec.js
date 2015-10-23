describe('Transform column param controller', function () {
    'use strict';

    var createController, scope;
    var parameter = {value: {}};
    var stateMock = {
        playground: {
            // available dataset/preparation columns
            data: {
                columns: [
                    {id: '0001', name: 'first name'},
                    {id: '0002', name: 'last name'},
                    {id: '0003', name: 'birth date'}
                ]
            },
            grid: {
                // selected column
                selectedColumn: {}
            }
        }
    };

    beforeEach(module('data-prep.transformation-params', function ($provide) {

        // set the selected column to the first one
        stateMock.playground.grid.selectedColumn = stateMock.playground.data.columns[0];

        $provide.constant('state', stateMock);
    }));


    beforeEach(inject(function ($rootScope, $controller) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrlFn = $controller('TransformColumnParamCtrl', {
                $scope: scope
            }, true);
            ctrlFn.instance.parameter = parameter;
            return ctrlFn();
        };
    }));


    it('should remove current column', function () {
        // when
        var ctrl = createController();

        // then
        expect(ctrl.columns.length).toBe(2);
        expect(ctrl.columns[0]).toBe(stateMock.playground.data.columns[1]);
        expect(ctrl.columns[1]).toBe(stateMock.playground.data.columns[2]);

    });

    it('should set selected value to first column', function () {
        // when
        var ctrl = createController();

        // then
        expect(ctrl.parameter.value).toBe(stateMock.playground.data.columns[1].id);

    });
});