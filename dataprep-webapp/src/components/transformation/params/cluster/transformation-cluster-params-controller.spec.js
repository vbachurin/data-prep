describe('Transform cluster params controller', function () {
    'use strict';

    var createController, scope, details;

    beforeEach(module('data-prep.transformation-params'));

    beforeEach(inject(function ($rootScope, $controller) {
        details = {
            titles: [
                'We found these values',
                'And we\'ll keep this value'
            ],
            clusters: [
                {
                    parameters: [
                        {
                            name: 'Texa',
                            type: 'boolean',
                            description: 'parameter.Texa.desc',
                            label: 'parameter.Texa.label',
                            default: 'true'
                        },
                        {
                            name: 'Tixass',
                            type: 'boolean',
                            description: 'parameter.Tixass.desc',
                            label: 'parameter.Tixass.label',
                            default: 'true'
                        },
                        {
                            name: 'Tex@s',
                            type: 'boolean',
                            description: 'parameter.Tex@s.desc',
                            label: 'parameter.Tex@s.label',
                            default: 'true'
                        }
                    ],
                    'replace': {
                        name: 'replaceValue',
                        type: 'string',
                        description: 'parameter.replaceValue.desc',
                        label: 'parameter.replaceValue.label',
                        default: 'Texas'
                    }
                },
                {
                    parameters: [
                        {
                            name: 'Massachusetts',
                            type: 'boolean',
                            description: 'parameter.Massachusetts.desc',
                            label: 'parameter.Massachusetts.label',
                            default: 'false'
                        },
                        {
                            name: 'Masachusetts',
                            type: 'boolean',
                            description: 'parameter.Masachusetts.desc',
                            label: 'parameter.Masachusetts.label',
                            default: 'true'
                        },
                        {
                            name: 'Massachussetts',
                            type: 'boolean',
                            description: 'parameter.Massachussetts.desc',
                            label: 'parameter.Massachussetts.label',
                            default: 'true'
                        },
                        {
                            name: 'Massachusets',
                            type: 'boolean',
                            description: 'parameter.Massachusets.desc',
                            label: 'parameter.Massachusets.label',
                            default: 'true'
                        },
                        {
                            name: 'Masachussets',
                            type: 'boolean',
                            description: 'parameter.Masachussets.desc',
                            label: 'parameter.Masachussets.label',
                            default: 'true'
                        }
                    ],
                    replace: {
                        name: 'replaceValue',
                        type: 'string',
                        description: 'parameter.replaceValue.desc',
                        label: 'parameter.replaceValue.label',
                        default: 'Massachussets'
                    }
                }
            ]
        };
        scope = $rootScope.$new();

        createController = function () {
            var ctrlFn = $controller('TransformClusterParamsCtrl', {
                $scope: scope
            }, true);
            ctrlFn.instance.details = details;
            return ctrlFn();
        };
    }));

    it('should init allCheckboxState flag to true', function () {
        //when
        var ctrl = createController();

        //then
        expect(ctrl.allCheckboxState).toBe(true);
    });

    it('should init clusters active flag to true', function () {
        //when
        var ctrl = createController();

        //then
        expect(ctrl.details.clusters[0].active).toBe(true);
        expect(ctrl.details.clusters[1].active).toBe(true);
    });

    it('should set clusters "active" flags to "allCheckboxState" flag value', function () {
        //given
        var ctrl = createController();
        ctrl.details.clusters[0].active = false;
        ctrl.details.clusters[1].active = false;
        ctrl.allCheckboxState = true;

        //when
        ctrl.refreshClusterState();

        //then
        expect(ctrl.details.clusters[0].active).toBe(true);
        expect(ctrl.details.clusters[1].active).toBe(true);
    });

    it('should set "allCheckboxState" flag to true if all clusters are actives', function () {
        //given
        var ctrl = createController();
        ctrl.details.clusters[0].active = true;
        ctrl.details.clusters[1].active = true;
        ctrl.allCheckboxState = false;

        //when
        ctrl.refreshToggleCheckbox();

        //then
        expect(ctrl.allCheckboxState).toBe(true);
    });

    it('should set "allCheckboxState" flag to false if a cluster is inactive', function () {
        //given
        var ctrl = createController();
        ctrl.details.clusters[0].active = true;
        ctrl.details.clusters[1].active = false;
        ctrl.allCheckboxState = true;

        //when
        ctrl.refreshToggleCheckbox();

        //then
        expect(ctrl.allCheckboxState).toBe(false);
    });
});