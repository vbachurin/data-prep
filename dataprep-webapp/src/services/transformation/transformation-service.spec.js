describe('Transformation Service', function() {
    'use strict';

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

    var menusRestMock = function() {
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

    var expectedMenusMock = function() {
        return [
            {
                'name':'uppercase',
                'category':'case',
                'items':null,
                'parameters':null
            },
            {
                'name':'lowercase',
                'category':'case',
                'items':null,
                'parameters':null
            },
            {
                'name':'withParam',
                'category':'case',
                'items':null,
                'parameters':[
                    {
                        'name':'param',
                        'type':'string',
                        'default':'.',
                        'inputType':'text'
                    }
                ]
            },
            {
                'name':'split',
                'category':'split',
                'parameters':null,
                'items':[
                    {
                        'name':'mode',
                        'values':[
                            {
                                'name':'noparam'
                            },
                            {
                                'name':'regex',
                                'parameters':[
                                    {
                                        'name':'regexp',
                                        'type':'string',
                                        'default':'.',
                                        'inputType':'text'
                                    }
                                ]
                            },
                            {
                                'name':'index',
                                'parameters':[
                                    {
                                        'name':'index',
                                        'type':'integer',
                                        'default':'5',
                                        'inputType':'number'
                                    }
                                ]
                            },
                            {
                                'name':'threeParams',
                                'parameters':[
                                    {
                                        'name':'index',
                                        'type':'numeric',
                                        'default':'5',
                                        'inputType':'number'
                                    },
                                    {
                                        'name':'index2',
                                        'type':'float',
                                        'default':'5',
                                        'inputType':'number'
                                    },
                                    {
                                        'name':'index3',
                                        'type':'double',
                                        'default':'5',
                                        'inputType':'number'
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        ];
    };

    beforeEach(module('data-prep.services.transformation'));

    beforeEach(inject(function ($q, TransformationRestService) {
        spyOn(TransformationRestService, 'getTransformations').and.returnValue($q.when({data: menusRestMock()}));
        spyOn(TransformationRestService, 'getDynamicParameters').and.returnValue($q.when({data : textClusteringParams}));
    }));

    it('should reset params before dynamic params fetch', inject(function (TransformationService) {
        //given
        var transformation = {
            name: 'textclustering',
            category: 'quickfix',
            dynamic: true,
            parameters: [],
            items: [],
            cluster: {}
        };
        var infos = {
            datasetId: '78bae6345aef9965e22b54',
            preparationId: '721cd4455fb69e89543d4',
            stepId: '6b9312964fa564e864'
        };

        //when
        TransformationService.initDynamicParameters(transformation, infos);

        //then
        expect(transformation.parameters).toBe(null);
        expect(transformation.items).toBe(null);
        expect(transformation.cluster).toBe(null);
    }));

    it('should fetch dynamic parameters and inject them into transformation', inject(function ($rootScope, TransformationService) {
        //given
        var transformation = {
            name: 'textclustering',
            category: 'quickfix',
            dynamic: true,
            parameters: [],
            items: [],
            cluster: {}
        };
        var infos = {
            datasetId: '78bae6345aef9965e22b54',
            preparationId: '721cd4455fb69e89543d4',
            stepId: '6b9312964fa564e864'
        };

        //when
        TransformationService.initDynamicParameters(transformation, infos);
        $rootScope.$digest();

        //then
        expect(transformation.cluster).toBe(textClusteringParams.details);
    }));

    it('should reset current values to initial saved values in simple param', inject(function(TransformationService) {
        //given
        var parameters = [
            {
                name: 'param1',
                type: 'string',
                initialValue: 'myParam1',
                inputType: 'text',
                value: 'myNewParam1'
            },
            {
                name: 'param2',
                type: 'integer',
                initialValue: 5,
                inputType: 'number',
                value: 6
            }
        ];

        //when
        TransformationService.resetParamValue(parameters, null);

        //then
        expect(parameters[0].value).toBe('myParam1');
        expect(parameters[1].value).toBe(5);
    }));

    it('should reset current values to initial saved values in choice param', inject(function(TransformationService) {
        //given
        var choices = [
            {
                name: 'mode',
                type: 'LIST',
                values: [
                    {
                        name: 'regex',
                        parameters : [
                            {name: 'regex', type: 'text', initialValue: 'param1Value'},
                            {name: 'comment', type: 'text', initialValue: 'my comment'}
                        ]
                    },
                    {name: 'index'}
                ]
            }
        ];
        choices[0].initialValue = choices[0].values[1];

        choices[0].selectedValue = choices[0].values[0];
        choices[0].values[0].parameters[0].value = 'newParam1Value';
        choices[0].values[0].parameters[1].value = 'myNewcomment';

        //when
        TransformationService.resetParamValue(choices, 'CHOICE');

        //then
        expect(choices[0].selectedValue).toBe(choices[0].values[1]);
        expect(choices[0].values[0].parameters[0].value).toBe('param1Value');
        expect(choices[0].values[0].parameters[1].value).toBe('my comment');
    }));

    it('should reset current values to initial saved values in cluster param', inject(function(TransformationService) {
        //given
        var cluster = {
            titles: [
                'We found these values',
                'And we\'ll keep this value'
            ],
            clusters: [
                {
                    initialActive: true,
                    active: true,
                    parameters: [
                        {
                            name: 'Texa',
                            type: 'boolean',
                            description: 'parameter.Texa.desc',
                            label: 'parameter.Texa.label',
                            default: null,
                            initialValue: true,
                            value: false
                        },
                        {
                            name: 'Tixass',
                            type: 'boolean',
                            description: 'parameter.Tixass.desc',
                            label: 'parameter.Tixass.label',
                            default: null,
                            initialValue: true,
                            value: true
                        },
                        {
                            name: 'Tex@s',
                            type: 'boolean',
                            description: 'parameter.Tex@s.desc',
                            label: 'parameter.Tex@s.label',
                            default: null,
                            initialValue: true,
                            value: false
                        }
                    ],
                    'replace': {
                        name: 'replaceValue',
                        type: 'string',
                        description: 'parameter.replaceValue.desc',
                        label: 'parameter.replaceValue.label',
                        default: 'Texas',
                        initialValue: 'Texas',
                        value: 'toxos'
                    }
                },
                {
                    initialActive: true,
                    active: false,
                    parameters: [
                        {
                            name: 'Massachusetts',
                            type: 'boolean',
                            description: 'parameter.Massachusetts.desc',
                            label: 'parameter.Massachusetts.label',
                            default: null,
                            initialValue: true,
                            value: true
                        },
                        {
                            name: 'Masachusetts',
                            type: 'boolean',
                            description: 'parameter.Masachusetts.desc',
                            label: 'parameter.Masachusetts.label',
                            default: null,
                            initialValue: false,
                            value: false
                        },
                        {
                            name: 'Massachussetts',
                            type: 'boolean',
                            description: 'parameter.Massachussetts.desc',
                            label: 'parameter.Massachussetts.label',
                            default: null,
                            initialValue: false,
                            value: true
                        },
                        {
                            name: 'Massachusets',
                            type: 'boolean',
                            description: 'parameter.Massachusets.desc',
                            label: 'parameter.Massachusets.label',
                            default: null,
                            initialValue: false,
                            value: false
                        },
                        {
                            name: 'Masachussets',
                            type: 'boolean',
                            description: 'parameter.Masachussets.desc',
                            label: 'parameter.Masachussets.label',
                            default: null,
                            initialValue: true,
                            value: true
                        }
                    ],
                    replace: {
                        name: 'replaceValue',
                        type: 'string',
                        description: 'parameter.replaceValue.desc',
                        label: 'parameter.replaceValue.label',
                        default: 'Massachussets',
                        initialValue: 'Massachussets',
                        value: 'Ma chaussette'
                    }
                }
            ]
        };

        //when
        TransformationService.resetParamValue(cluster, 'CLUSTER');

        //then
        expect(cluster.clusters[0].active).toBe(true);
        expect(cluster.clusters[0].parameters[0].value).toBe(true);
        expect(cluster.clusters[0].parameters[1].value).toBe(true);
        expect(cluster.clusters[0].parameters[2].value).toBe(true);
        expect(cluster.clusters[0].replace.value).toBe('Texas');

        expect(cluster.clusters[1].active).toBe(true);
        expect(cluster.clusters[1].parameters[0].value).toBe(true);
        expect(cluster.clusters[1].parameters[1].value).toBe(false);
        expect(cluster.clusters[1].parameters[2].value).toBe(false);
        expect(cluster.clusters[1].parameters[3].value).toBe(false);
        expect(cluster.clusters[1].parameters[4].value).toBe(true);
        expect(cluster.clusters[1].replace.value).toBe('Massachussets');
    }));
});