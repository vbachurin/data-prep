describe('Parameters Service', () => {
    'use strict';

    function textClusteringParams() {
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
    }

    beforeEach(angular.mock.module('data-prep.services.parameters'));

    describe('reset', () => {
        it('should reset current values to initial saved values in simple param', inject((ParametersService) => {
            // given
            const parameters = [
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

            // when
            ParametersService.resetParamValue(parameters, null);

            // then
            expect(parameters[0].value).toBe('myParam1');
            expect(parameters[1].value).toBe(5);
        }));

        it('should reset current values to initial saved values in choice param', inject((ParametersService) => {
            // given
            const choices = [
                {
                    name: 'mode',
                    type: 'LIST',
                    values: [
                        {
                            name: 'regex',
                            parameters: [
                                { name: 'regex', type: 'text', initialValue: 'param1Value' },
                                { name: 'comment', type: 'text', initialValue: 'my comment' }
                            ]
                        },
                        { name: 'index' }
                    ]
                }
            ];
            choices[0].initialValue = choices[0].values[1];

            choices[0].selectedValue = choices[0].values[0];
            choices[0].values[0].parameters[0].value = 'newParam1Value';
            choices[0].values[0].parameters[1].value = 'myNewcomment';

            // when
            ParametersService.resetParamValue(choices, 'CHOICE');

            // then
            expect(choices[0].selectedValue).toBe(choices[0].values[1]);
            expect(choices[0].values[0].parameters[0].value).toBe('param1Value');
            expect(choices[0].values[0].parameters[1].value).toBe('my comment');
        }));

        it('should reset current values to initial saved values in cluster param', inject((ParametersService) => {
            // given
            const cluster = {
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

            // when
            ParametersService.resetParamValue(cluster, 'CLUSTER');

            // then
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

        it('should do nothing when param is falsy', inject((ParametersService) => {
            // given
            const params = null;

            // when
            ParametersService.resetParamValue(params, 'CLUSTER');

            // then
            expect(params).toBe(null);
        }));
    });

    describe('init params values', () => {
        it('should init params values on simple params', inject((ParametersService) => {
            //given
            const transformation = {
                parameters: [
                    { name: 'column_id', type: 'text', value: 'col', implicit: true },
                    { name: 'pattern', type: 'text', default: 'toto' },
                    { name: 'patternBool', type: 'boolean', default: 'false' }
                ]
            };

            const paramValues = {
                column_name: 'col',
                pattern: 'tata',
                patternBool: 'true'
            };

            //when
            ParametersService.initParamsValues(transformation, paramValues);

            //then
            expect(transformation.parameters[0].value).toBe('tata');
            expect(transformation.parameters[0].initialValue).toBe('tata');
            expect(transformation.parameters[0].inputType).toBe('text');
            expect(transformation.parameters[1].value).toBe(true);
            expect(transformation.parameters[1].initialValue).toBe(true);
        }));

        it('should init params values on select params', inject((ParametersService) => {
            //given
            const transformation = {
                parameters: [
                    {
                        name: 'mode',
                        type: 'select',
                        configuration: {
                            values: [
                                {
                                    name: 'regex',
                                    value: 'regex',
                                    parameters: [
                                        { name: 'pattern', type: 'text', default: 'toto' }
                                    ]
                                },
                                { name: 'index', value: 'index' }
                            ]
                        },
                        default: 'index'
                    }]
            };

            var paramValues = {
                column_name: 'col',
                mode: 'regex',
                pattern: 'azerty'
            };

            //when
            ParametersService.initParamsValues(transformation, paramValues);

            //then
            expect(transformation.parameters[0].initialValue).toBe(transformation.parameters[0].configuration.values[0].value);
            expect(transformation.parameters[0].value).toBe(transformation.parameters[0].configuration.values[0].value);
            expect(transformation.parameters[0].configuration.values[0].parameters[0].initialValue).toBe('azerty');
            expect(transformation.parameters[0].configuration.values[0].parameters[0].value).toBe('azerty');
            expect(transformation.parameters[0].configuration.values[0].parameters[0].inputType).toBe('text');
        }));

        it('should init params values on cluster params', inject((ParametersService) => {
            //given
            const transformation = {
                cluster: textClusteringParams().details
            };

            const paramValues = {
                column_name: 'col',
                Texa: 'MyTexas',
                Tixass: 'MyTexas',
                Masachusetts: 'Ma chaussette',
                Massachussetts: 'Ma chaussette',
                Massachusets: 'Ma chaussette'
            };

            //when
            ParametersService.initParamsValues(transformation, paramValues);

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
    });
});
