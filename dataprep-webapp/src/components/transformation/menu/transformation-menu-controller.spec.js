/*jshint camelcase: false */

describe('Transform menu controller', function () {
    'use strict';

    var createController, scope;
    var result = {
        'records': [{
            'firstname': 'Grover',
            'avgAmount': '82.4',
            'city': 'BOSTON',
            'birth': '01-09-1973',
            'registration': '17-02-2008',
            'id': '1',
            'state': 'AR',
            'nbCommands': '41',
            'lastname': 'Quincy'
        }, {
            'firstname': 'Warren',
            'avgAmount': '87.6',
            'city': 'NASHVILLE',
            'birth': '11-02-1960',
            'registration': '18-08-2007',
            'id': '2',
            'state': 'WA',
            'nbCommands': '17',
            'lastname': 'Johnson'
        }]
    };

    var metadata = {
        'id': '44f5e4ef-96e9-4041-b86a-0bee3d50b18b',
        'name': 'customers_jso_light',
        'author': 'anonymousUser',
        'records': 15,
        'nbLinesHeader': 1,
        'nbLinesFooter': 0,
        'created': '02-16-2015 08:52'
    };
    var column = {
        'id': 'MostPopulousCity',
        'quality': {
            'empty': 5,
            'invalid': 10,
            'valid': 72
        },
        'type': 'string'
    };

    beforeEach(module('data-prep.transformation-menu'));

    beforeEach(inject(function ($rootScope, $controller, $q, PreparationService, DatasetGridService, RecipeService) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('TransformMenuCtrl', {
                $scope: scope
            });
            ctrl.metadata = metadata;
            ctrl.column = column;
            return ctrl;
        };

        spyOn(PreparationService, 'append').and.returnValue($q.when(true));
        spyOn(PreparationService, 'getContent').and.returnValue($q.when({data: result}));
        spyOn($rootScope, '$emit').and.callThrough();
        spyOn(DatasetGridService, 'updateRecords').and.callFake(function() {});
        spyOn(RecipeService, 'refresh').and.callFake(function() {});
    }));

    it('should do nothing on select if the menu is a divider', inject(function (PreparationService) {
        //given
        var ctrl = createController();
        ctrl.menu = {isDivider: true};

        //when
        ctrl.select();

        //then
        expect(ctrl.showModal).toBeFalsy();
        expect(PreparationService.append).not.toHaveBeenCalled();
        expect(PreparationService.getContent).not.toHaveBeenCalled();
    }));

    it('should open modal on select if item has parameters', inject(function (PreparationService) {
        //given
        var ctrl = createController();
        ctrl.menu = {parameters: [{name: 'param1', type: 'text', default: '.'}]};

        //when
        ctrl.select();

        //then
        expect(ctrl.showModal).toBeTruthy();
        expect(PreparationService.append).not.toHaveBeenCalled();
        expect(PreparationService.getContent).not.toHaveBeenCalled();
    }));

    it('should open modal on select if item has choice', inject(function (PreparationService) {
        //given
        var ctrl = createController();
        ctrl.menu = {items: [{name: 'choice', values: [{name: 'choice1'}, {name: 'choice2'}]}]};

        //when
        ctrl.select();

        //then
        expect(ctrl.showModal).toBeTruthy();
        expect(PreparationService.append).not.toHaveBeenCalled();
        expect(PreparationService.getContent).not.toHaveBeenCalled();
    }));

    it('should call transform on simple menu select', inject(function ($rootScope, PreparationService, DatasetGridService, RecipeService) {
        //given
        var ctrl = createController();
        ctrl.menu = {name: 'uppercase', category: 'case'};

        //when
        ctrl.select();
        expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
        $rootScope.$digest();

        //then
        expect(ctrl.showModal).toBeFalsy();
        expect(PreparationService.append).toHaveBeenCalledWith('44f5e4ef-96e9-4041-b86a-0bee3d50b18b', 'uppercase', { column_name: 'MostPopulousCity' });
        expect(PreparationService.getContent).toHaveBeenCalledWith('head');
        expect(DatasetGridService.updateRecords).toHaveBeenCalledWith(result.records);
        expect(RecipeService.refresh).toHaveBeenCalled();
        expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
    }));

    it('should call transform on parameterized menu select', inject(function ($rootScope, PreparationService, DatasetGridService, RecipeService) {
        //given
        var ctrl = createController();
        ctrl.menu = {
            name: 'uppercase',
            category: 'case',
            parameters: [
                {name: 'param1', type: 'text', default: '', value: 'param1Value'},
                {name: 'param2', type: 'int', default: '5', value: 4}
            ]
        };

        //when
        ctrl.transform({param1: 'param1Value', param2: 4 });
        expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.start');
        $rootScope.$digest();

        //then
        expect(ctrl.showModal).toBeFalsy();
        expect(PreparationService.append).toHaveBeenCalledWith(
            '44f5e4ef-96e9-4041-b86a-0bee3d50b18b',
            'uppercase',
            { column_name: 'MostPopulousCity', param1: 'param1Value', param2: 4 });
        expect(PreparationService.getContent).toHaveBeenCalledWith('head');
        expect(DatasetGridService.updateRecords).toHaveBeenCalledWith(result.records);
        expect(RecipeService.refresh).toHaveBeenCalled();
        expect($rootScope.$emit).toHaveBeenCalledWith('talend.loading.stop');
    }));
});