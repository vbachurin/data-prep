describe('Type transform menu controller', function () {
    'use strict';

    var createController, scope;
    var currentMetadata = {id: '719b84635c436ef245'};

    var types = [
        {'id': 'ANY', 'name': 'any', 'labelKey': 'ANY'},
        {'id': 'STRING', 'name': 'string', 'labelKey': 'STRING'},
        {'id': 'NUMERIC', 'name': 'numeric', 'labelKey': 'NUMERIC'},
        {'id': 'INTEGER', 'name': 'integer', 'labelKey': 'INTEGER'},
        {'id': 'DOUBLE', 'name': 'double', 'labelKey': 'DOUBLE'},
        {'id': 'FLOAT', 'name': 'float', 'labelKey': 'FLOAT'},
        {'id': 'BOOLEAN', 'name': 'boolean', 'labelKey': 'BOOLEAN'},
        {'id': 'DATE', 'name': 'date', 'labelKey': 'DATE'}
    ];

    beforeEach(module('data-prep.type-transformation-menu'));

    beforeEach(inject(function ($rootScope, $controller, $q, state, ColumnTypesService) {
        scope = $rootScope.$new();
        createController = function () {
            var ctrl = $controller('TypeTransformMenuCtrl', {
                $scope: scope
            });
            ctrl.column = {
                id: '0001',
                domain: 'CITY',
                domainLabel: 'CITY',
                domainFrequency: 18,
                type: 'string',
                semanticDomains: [
                    {id: '', label: '', frequency: 15},
                    {id: 'CITY', label: 'CITY', frequency: 18},
                    {id: 'REGION', label: 'REGION', frequency: 6},
                    {id: 'COUNTRY', label: 'COUNTRY', frequency: 17}
                ]
            };
            return ctrl;
        };

        state.playground.dataset = currentMetadata;
        spyOn(ColumnTypesService, 'getTypes').and.returnValue($q.when(types));
    }));

    it('should get column primitive types', inject(function (ColumnTypesService) {
        //when
        var ctrl = createController();
        scope.$digest();

        //then
        expect(ColumnTypesService.getTypes).toHaveBeenCalled();
        expect(ctrl.types).toBe(types);
    }));

    it('should change domain locally and call backend to add a step', inject(function ($q, PlaygroundService) {
        //given
        spyOn(PlaygroundService, 'addUpdateColumnDomainStep').and.returnValue($q.when(null));
        var ctrl = createController();
        var newDomain = {
            id: 'COUNTRY',
            label: 'COUNTRY',
            frequency: 17
        };

        //when
        ctrl.changeDomain(newDomain);

        //then
        expect(ctrl.column.domain).toBe('COUNTRY');
        expect(ctrl.column.domainLabel).toBe('COUNTRY');
        expect(ctrl.column.domainFrequency).toBe(17);
        expect(ctrl.currentDomain).toBe('COUNTRY');
        expect(ctrl.currentSimplifiedDomain).toBe('COUNTRY');

        expect(PlaygroundService.addUpdateColumnDomainStep).toHaveBeenCalledWith('0001', {id: 'COUNTRY', label: 'COUNTRY', frequency: 17});
    }));

    it('should revert domain when backend return error', inject(function ($q, PlaygroundService) {
        //given
        spyOn(PlaygroundService, 'addUpdateColumnDomainStep').and.returnValue($q.reject(null));
        var ctrl = createController();
        var newDomain = {
            id: 'COUNTRY',
            label: 'COUNTRY',
            frequency: 17
        };

        //when
        ctrl.changeDomain(newDomain);
        scope.$digest();

        //then
        expect(ctrl.column.domain).toBe('CITY');
        expect(ctrl.column.domainLabel).toBe('CITY');
        expect(ctrl.column.domainFrequency).toBe(18);
        expect(ctrl.currentDomain).toBe('CITY');
        expect(ctrl.currentSimplifiedDomain).toBe('CITY');
    }));

    it('should change type and clear domain locally and call backend', inject(function ($q, PlaygroundService) {
        //given
        spyOn(PlaygroundService, 'addUpdateColumnTypeStep').and.returnValue($q.when(null));
        var ctrl = createController();
        var newType = {
            id: 'integer'
        };

        //when
        ctrl.changeType(newType);

        //then
        expect(ctrl.column.type).toBe('integer');
        expect(ctrl.column.domain).toBe('');
        expect(ctrl.column.domainLabel).toBe('');
        expect(ctrl.column.domainFrequency).toBe(0);
        expect(ctrl.currentDomain).toBe('INTEGER');
        expect(ctrl.currentSimplifiedDomain).toBe('integer');
        expect(PlaygroundService.addUpdateColumnTypeStep).toHaveBeenCalledWith('0001', {id:'integer'});
    }));

    it('should revert type and domain when backend return error', inject(function ($q, PlaygroundService) {
        //given
        spyOn(PlaygroundService, 'addUpdateColumnTypeStep').and.returnValue($q.reject(null));
        var ctrl = createController();
        var newType = {
            id: 'integer'
        };

        //when
        ctrl.changeType(newType);
        scope.$digest();

        //then
        expect(ctrl.column.type).toBe('string');
        expect(ctrl.column.domain).toBe('CITY');
        expect(ctrl.column.domainLabel).toBe('CITY');
        expect(ctrl.column.domainFrequency).toBe(18);
        expect(ctrl.currentDomain).toBe('CITY');
        expect(ctrl.currentSimplifiedDomain).toBe('CITY');
    }));

    it('should filter concrete domain and order them', function () {
        //given
        var ctrl = createController();

        //when
        ctrl.adaptDomains();

        //then
        expect(ctrl.domains).toEqual([
            {id: 'CITY', label: 'CITY', frequency: 18},
            {id: 'COUNTRY', label: 'COUNTRY', frequency: 17},
            {id: 'REGION', label: 'REGION', frequency: 6}
        ]);
        expect(ctrl.currentDomain).toBe('CITY');
        expect(ctrl.currentSimplifiedDomain).toBe('CITY');
    });
});