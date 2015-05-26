/*jshint camelcase: false */

describe('Transform menu controller', function () {
    'use strict';

    var createController, scope;

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

    var textClusteringParams = {
        'type':'cluster',
        'details':{
            'titles':[
                'We found these values',
                'And we\'ll keep this value'
            ],
            'clusters':[
                {
                    'parameters':[
                        {
                            'name':'Texa',
                            'type':'boolean',
                            'description':'parameter.Texa.desc',
                            'label':'parameter.Texa.label',
                            'default':'true'
                        },
                        {
                            'name':'Tixass',
                            'type':'boolean',
                            'description':'parameter.Tixass.desc',
                            'label':'parameter.Tixass.label',
                            'default':'true'
                        },
                        {
                            'name':'Tex@s',
                            'type':'boolean',
                            'description':'parameter.Tex@s.desc',
                            'label':'parameter.Tex@s.label',
                            'default':'true'
                        }
                    ],
                    'replace':{
                        'name':'replaceValue',
                        'type':'string',
                        'description':'parameter.replaceValue.desc',
                        'label':'parameter.replaceValue.label',
                        'default':'Texas'
                    }
                },
                {
                    'parameters':[
                        {
                            'name':'Massachusetts',
                            'type':'boolean',
                            'description':'parameter.Massachusetts.desc',
                            'label':'parameter.Massachusetts.label',
                            'default':'false'
                        },
                        {
                            'name':'Masachusetts',
                            'type':'boolean',
                            'description':'parameter.Masachusetts.desc',
                            'label':'parameter.Masachusetts.label',
                            'default':'true'
                        },
                        {
                            'name':'Massachussetts',
                            'type':'boolean',
                            'description':'parameter.Massachussetts.desc',
                            'label':'parameter.Massachussetts.label',
                            'default':'true'
                        },
                        {
                            'name':'Massachusets',
                            'type':'boolean',
                            'description':'parameter.Massachusets.desc',
                            'label':'parameter.Massachusets.label',
                            'default':'true'
                        },
                        {
                            'name':'Masachussets',
                            'type':'boolean',
                            'description':'parameter.Masachussets.desc',
                            'label':'parameter.Masachussets.label',
                            'default':'true'
                        }
                    ],
                    'replace':{
                        'name':'replaceValue',
                        'type':'string',
                        'description':'parameter.replaceValue.desc',
                        'label':'parameter.replaceValue.label',
                        'default':'Massachussets'
                    }
                }
            ]
        }
    };

    beforeEach(module('data-prep.transformation-menu'));

    beforeEach(inject(function ($rootScope, $controller, $q, PlaygroundService, TransformationRestService) {
        scope = $rootScope.$new();

        createController = function () {
            var ctrl = $controller('TransformMenuCtrl', {
                $scope: scope
            });
            ctrl.metadata = metadata;
            ctrl.column = column;
            return ctrl;
        };

        spyOn(PlaygroundService, 'appendStep').and.returnValue($q.when(true));
        spyOn(TransformationRestService, 'getDynamicParameters').and.returnValue($q.when({data : textClusteringParams}));
    }));

    it('should do nothing on select if the menu is a divider', inject(function (PlaygroundService) {
        //given
        var ctrl = createController();
        ctrl.menu = {isDivider: true};

        //when
        ctrl.select();

        //then
        expect(ctrl.showModal).toBeFalsy();
        expect(PlaygroundService.appendStep).not.toHaveBeenCalled();
    }));

    it('should open modal on select if item has parameters', inject(function (PlaygroundService) {
        //given
        var ctrl = createController();
        ctrl.menu = {parameters: [{name: 'param1', type: 'text', default: '.'}]};

        //when
        ctrl.select();

        //then
        expect(ctrl.showModal).toBeTruthy();
        expect(PlaygroundService.appendStep).not.toHaveBeenCalled();
    }));

    it('should open modal on select if item has choice', inject(function (PlaygroundService) {
        //given
        var ctrl = createController();
        ctrl.menu = {items: [{name: 'choice', values: [{name: 'choice1'}, {name: 'choice2'}]}]};

        //when
        ctrl.select();

        //then
        expect(ctrl.showModal).toBeTruthy();
        expect(PlaygroundService.appendStep).not.toHaveBeenCalled();
    }));

    it('should call transform on simple menu select', inject(function ($rootScope, PlaygroundService) {
        //given
        var ctrl = createController();
        ctrl.menu = {name: 'uppercase', category: 'case'};

        //when
        ctrl.select();
        $rootScope.$digest();

        //then
        expect(ctrl.showModal).toBeFalsy();
        expect(PlaygroundService.appendStep).toHaveBeenCalledWith('uppercase', column, undefined);
    }));

    it('should fetch dynamic parameters', inject(function ($rootScope, PlaygroundService, PreparationService, TransformationRestService) {
        //given
        var ctrl = createController();
        ctrl.menu = {name: 'textclustering', category: 'quickfix', dynamic: true};

        PlaygroundService.currentMetadata = {id: '78bae6345aef9965e22b54'};
        PreparationService.currentPreparationId = '721cd4455fb69e89543d4';

        //when
        ctrl.select();
        $rootScope.$digest();

        //then
        expect(TransformationRestService.getDynamicParameters).toHaveBeenCalledWith('textclustering', 'MostPopulousCity', '78bae6345aef9965e22b54', '721cd4455fb69e89543d4');
        expect(ctrl.menu.cluster).toBe(textClusteringParams.details);
    }));

    it('should display modal and set flags on dynamic params fetch', inject(function ($rootScope, PlaygroundService, PreparationService) {
        //given
        var ctrl = createController();
        ctrl.menu = {name: 'textclustering', category: 'quickfix', dynamic: true};

        PlaygroundService.currentMetadata = {id: '78bae6345aef9965e22b54'};
        PreparationService.currentPreparationId = '721cd4455fb69e89543d4';

        //when
        expect(ctrl.showModal).toBeFalsy();
        ctrl.select();
        expect(ctrl.showModal).toBeTruthy();
        expect(ctrl.dynamicFetchInProgress).toBeTruthy();
        $rootScope.$digest();

        //then
        expect(ctrl.showModal).toBeTruthy();
        expect(ctrl.dynamicFetchInProgress).toBeFalsy();
    }));

    it('should reset params before dynamic params fetch', inject(function ($rootScope, PlaygroundService, PreparationService) {
        //given
        var ctrl = createController();
        ctrl.menu = {name: 'textclustering', category: 'quickfix', dynamic: true};

        PlaygroundService.currentMetadata = {id: '78bae6345aef9965e22b54'};
        PreparationService.currentPreparationId = '721cd4455fb69e89543d4';

        ctrl.menu.parameters = {};
        ctrl.menu.items = {};
        ctrl.menu.cluster = {};

        //when
        ctrl.select();

        //then
        expect(ctrl.menu.parameters).toBe(null);
        expect(ctrl.menu.items).toBe(null);
        expect(ctrl.menu.cluster).toBe(null);
    }));

    it('should call playground service to append step and hide modal', inject(function ($rootScope, PlaygroundService) {
        //given
        var ctrl = createController();
        ctrl.showModal = true;
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
        expect(ctrl.showModal).toBeTruthy();
        $rootScope.$digest();

        //then
        expect(PlaygroundService.appendStep).toHaveBeenCalledWith(
            'uppercase',
            column,
            {param1: 'param1Value', param2: 4});
        expect(ctrl.showModal).toBeFalsy();
    }));
});