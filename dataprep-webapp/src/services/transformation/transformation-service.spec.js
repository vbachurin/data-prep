/*jshint camelcase: false */

describe('Transformation Service', function () {
    'use strict';

    var textClusteringParams = function() {
        return {
            'type': 'cluster',
            'details': {
                'titles': [
                    'We found these values',
                    'And we\'ll keep this value'
                ],
                'clusters': [
                    {
                        'parameters': [
                            {
                                'name': 'Texa',
                                'type': 'boolean',
                                'description': 'parameter.Texa.desc',
                                'label': 'parameter.Texa.label',
                                'default': null
                            },
                            {
                                'name': 'Tixass',
                                'type': 'boolean',
                                'description': 'parameter.Tixass.desc',
                                'label': 'parameter.Tixass.label',
                                'default': null
                            },
                            {
                                'name': 'Tex@s',
                                'type': 'boolean',
                                'description': 'parameter.Tex@s.desc',
                                'label': 'parameter.Tex@s.label',
                                'default': null
                            }
                        ],
                        'replace': {
                            'name': 'replaceValue',
                            'type': 'string',
                            'description': 'parameter.replaceValue.desc',
                            'label': 'parameter.replaceValue.label',
                            'default': 'Texas'
                        }
                    },
                    {
                        'parameters': [
                            {
                                'name': 'Massachusetts',
                                'type': 'boolean',
                                'description': 'parameter.Massachusetts.desc',
                                'label': 'parameter.Massachusetts.label',
                                'default': null
                            },
                            {
                                'name': 'Masachusetts',
                                'type': 'boolean',
                                'description': 'parameter.Masachusetts.desc',
                                'label': 'parameter.Masachusetts.label',
                                'default': null
                            },
                            {
                                'name': 'Massachussetts',
                                'type': 'boolean',
                                'description': 'parameter.Massachussetts.desc',
                                'label': 'parameter.Massachussetts.label',
                                'default': null
                            },
                            {
                                'name': 'Massachusets',
                                'type': 'boolean',
                                'description': 'parameter.Massachusets.desc',
                                'label': 'parameter.Massachusets.label',
                                'default': null
                            },
                            {
                                'name': 'Masachussets',
                                'type': 'boolean',
                                'description': 'parameter.Masachussets.desc',
                                'label': 'parameter.Masachussets.label',
                                'default': null
                            }
                        ],
                        'replace': {
                            'name': 'replaceValue',
                            'type': 'string',
                            'description': 'parameter.replaceValue.desc',
                            'label': 'parameter.replaceValue.label',
                            'default': 'Massachussets'
                        }
                    },
                    {
                        'parameters': [
                            {
                                'name': 'Tato',
                                'type': 'boolean',
                                'description': 'parameter.Tato.desc',
                                'label': 'parameter.Tato.label',
                                'default': null
                            },
                            {
                                'name': 'tata',
                                'type': 'boolean',
                                'description': 'parameter.tata.desc',
                                'label': 'parameter.tata.label',
                                'default': null
                            },
                            {
                                'name': 't@t@',
                                'type': 'boolean',
                                'description': 'parameter.t@t@.desc',
                                'label': 'parameter.t@t@.label',
                                'default': null
                            }
                        ],
                        'replace': {
                            'name': 'replaceValue',
                            'type': 'string',
                            'description': 'parameter.replaceValue.desc',
                            'label': 'parameter.replaceValue.label',
                            'default': 'Tata'
                        }
                    }
                ]
            }
        };
    };

    var menusRestMock = function () {
        return [
            {
                'name': 'uppercase',
                'category': 'case',
                items: [],
                parameters: [
                    {name: 'column_name', type: 'string'},
                    {name: 'column_id', type: 'string'}
                ]
            },
            {
                'name': 'lowercase',
                'category': 'case',
                items: [],
                parameters: [
                    {name: 'column_name', type: 'string'},
                    {name: 'column_id', type: 'string'}
                ]
            },
            {
                'name': 'cut',
                'category': 'split',
                items: [],
                parameters: [
                    {name: 'column_name', type: 'string'},
                    {name: 'column_id', type: 'string'},
                    {name: 'value', type: 'string'}
                ]
            },
            {
                'name': 'split',
                'category': 'split',
                parameters: [
                    {name: 'column_name', type: 'string'},
                    {name: 'column_id', type: 'string'}
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

    beforeEach(module('data-prep.services.transformation'));

    beforeEach(inject(function ($q, TransformationRestService) {
        spyOn(TransformationRestService, 'getTransformations').and.returnValue($q.when({data: menusRestMock()}));
        spyOn(TransformationRestService, 'getDynamicParameters').and.returnValue($q.when({data: textClusteringParams()}));
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
        expect(transformation.cluster).toEqual(textClusteringParams().details);
    }));

    it('should reset current values to initial saved values in simple param', inject(function (TransformationService) {
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

    it('should reset current values to initial saved values in choice param', inject(function (TransformationService) {
        //given
        var choices = [
            {
                name: 'mode',
                type: 'LIST',
                values: [
                    {
                        name: 'regex',
                        parameters: [
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

    it('should reset current values to initial saved values in cluster param', inject(function (TransformationService) {
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

    it('should reset/do nothing when param is not null/undefined', inject(function (TransformationService) {
        //given
        var params = null;

        //when
        TransformationService.resetParamValue(params, 'CLUSTER');

        //then
        expect(params).toBe(null);
    }));

    it('should init params values on simple params', inject(function (TransformationService) {
        //given
        var transformation = {
            parameters: [
                {name: 'column_name', type: 'text', value: 'col'},
                {name: 'pattern', type: 'text', default: 'toto'}
            ]
        };

        var paramValues = {
            column_name: 'col',
            pattern: 'tata'
        };

        //when
        TransformationService.initParamsValues(transformation, paramValues);

        //then
        expect(transformation.parameters[0].value).toBe('tata');
        expect(transformation.parameters[0].initialValue).toBe('tata');
        expect(transformation.parameters[0].inputType).toBe('text');
    }));

    it('should init params values on simple params', inject(function (TransformationService) {
        //given
        var transformation = {
            items: [{
                name: 'mode',
                values: [
                    {name: 'regex', parameters: [{name: 'pattern', type: 'text', default: 'toto'}]},
                    {name: 'index', default: true}
                ]
            }]
        };

        var paramValues = {
            column_name: 'col',
            mode: 'regex',
            pattern: 'azerty'
        };

        //when
        TransformationService.initParamsValues(transformation, paramValues);

        //then
        expect(transformation.items[0].initialValue).toBe(transformation.items[0].values[0]);
        expect(transformation.items[0].selectedValue).toBe(transformation.items[0].values[0]);
        expect(transformation.items[0].values[0].parameters[0].initialValue).toBe('azerty');
        expect(transformation.items[0].values[0].parameters[0].value).toBe('azerty');
        expect(transformation.items[0].values[0].parameters[0].inputType).toBe('text');
    }));

    it('should init params values on simple params', inject(function (TransformationService) {
        //given
        var transformation = {
            cluster: textClusteringParams().details
        };

        var paramValues = {
            column_name: 'col',
            Texa: 'MyTexas',
            Tixass: 'MyTexas',
            Masachusetts: 'Ma chaussette',
            Massachussetts: 'Ma chaussette',
            Massachusets: 'Ma chaussette'
        };

        //when
        TransformationService.initParamsValues(transformation, paramValues);

        //then
        expect(transformation.cluster.clusters[0].parameters[0].value).toBe(true);
        expect(transformation.cluster.clusters[0].parameters[0].initialValue).toBe(true);
        expect(transformation.cluster.clusters[0].parameters[1].value).toBe(true);
        expect(transformation.cluster.clusters[0].parameters[1].initialValue).toBe(true);
        expect(transformation.cluster.clusters[0].parameters[2].value).toBe(false);
        expect(transformation.cluster.clusters[0].parameters[2].initialValue).toBe(false);
        expect(transformation.cluster.clusters[0].replace.value).toBe('MyTexas');
        expect(transformation.cluster.clusters[0].replace.initialValue).toBe('MyTexas');

        expect(transformation.cluster.clusters[1].parameters[0].value).toBe(false);
        expect(transformation.cluster.clusters[1].parameters[0].initialValue).toBe(false);
        expect(transformation.cluster.clusters[1].parameters[1].value).toBe(true);
        expect(transformation.cluster.clusters[1].parameters[1].initialValue).toBe(true);
        expect(transformation.cluster.clusters[1].parameters[2].value).toBe(true);
        expect(transformation.cluster.clusters[1].parameters[2].initialValue).toBe(true);
        expect(transformation.cluster.clusters[1].parameters[3].value).toBe(true);
        expect(transformation.cluster.clusters[1].parameters[3].initialValue).toBe(true);
        expect(transformation.cluster.clusters[1].parameters[4].value).toBe(false);
        expect(transformation.cluster.clusters[1].parameters[4].initialValue).toBe(false);
        expect(transformation.cluster.clusters[1].replace.value).toBe('Ma chaussette');
        expect(transformation.cluster.clusters[1].replace.initialValue).toBe('Ma chaussette');

        expect(transformation.cluster.clusters[2].parameters[0].value).toBe(false);
        expect(transformation.cluster.clusters[2].parameters[0].initialValue).toBe(false);
        expect(transformation.cluster.clusters[2].parameters[1].value).toBe(false);
        expect(transformation.cluster.clusters[2].parameters[1].initialValue).toBe(false);
        expect(transformation.cluster.clusters[2].parameters[2].value).toBe(false);
        expect(transformation.cluster.clusters[2].parameters[2].initialValue).toBe(false);
        expect(transformation.cluster.clusters[2].replace.value).toBe('Tata');
        expect(transformation.cluster.clusters[2].replace.initialValue).toBe('Tata');
    }));

    it('should get column transformations with adapted input types and without column_name parameter', inject(function ($rootScope, TransformationService) {
        //given
        var column = {};
        var transformations = null;

        //when
        TransformationService.getTransformations(column)
            .then(function(result) {
                transformations = result;
            });
        $rootScope.$digest();

        //then
        expect(transformations[0].parameters).toBe(null); // delete column_name & column_id parameter
        expect(transformations[1].parameters).toBe(null); // delete column_name & column_id parameter
        expect(transformations[2].parameters.length).toBe(1); // delete column_name & column_id parameter
        expect(transformations[2].parameters[0].inputType).toBe('text'); //adapt input type
        expect(transformations[3].parameters).toBe(null); // delete column_name & column_id parameter
        expect(transformations[3].items[0].values[1].parameters[0].inputType).toBe('text'); //adapt input type
        expect(transformations[3].items[0].values[2].parameters[0].inputType).toBe('number'); //adapt input type
        expect(transformations[3].items[0].values[3].parameters[0].inputType).toBe('number'); //adapt input type
        expect(transformations[3].items[0].values[3].parameters[1].inputType).toBe('number'); //adapt input type
        expect(transformations[3].items[0].values[3].parameters[2].inputType).toBe('number'); //adapt input type
    }));
});