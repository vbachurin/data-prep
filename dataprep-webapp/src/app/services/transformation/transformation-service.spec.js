describe('Transformation Service', () => {
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
    };

    function transformationsRestMock() {
        return [
            {
                name: 'uppercase',
                label: 'Change case to uppercase',
                category: 'case',
                parameters: [
                    { name: 'column_name', type: 'string', implicit: true },
                    { name: 'column_id', type: 'string', implicit: true }
                ]
            },
            {
                name: 'lowercase',
                label: 'Change case to lowercase',
                category: 'case',
                parameters: [
                    { name: 'column_name', type: 'string', implicit: true },
                    { name: 'column_id', type: 'string', implicit: true }
                ]
            },
            {
                name: 'cut',
                label: 'Remove part of the text',
                category: 'split',
                parameters: [
                    { name: 'column_name', type: 'string', implicit: true },
                    { name: 'column_id', type: 'string', implicit: true },
                    { name: 'value', type: 'string' }
                ]
            },
            {
                name: 'split',
                label: 'Cut in parts',
                category: 'split',
                parameters: [
                    { name: 'column_name', type: 'string', implicit: true },
                    { name: 'column_id', type: 'string', implicit: true },
                    {
                        name: 'mode',
                        type: 'select',
                        configuration: {
                            values: [
                                { name: 'noparam', value: 'noparam' },
                                {
                                    name: 'regex',
                                    value: 'regex',
                                    parameters: [
                                        {
                                            name: 'regexp',
                                            type: 'string',
                                            default: '.'
                                        }
                                    ]
                                },
                                {
                                    name: 'index',
                                    value: 'index',
                                    'parameters': [
                                        {
                                            'name': 'index',
                                            'type': 'integer',
                                            default: '5'
                                        }
                                    ]
                                },
                                {
                                    name: 'threeParams',
                                    value: 'threeParams',
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
                        }
                    }
                ]
            }
        ];
    };

    beforeEach(angular.mock.module('data-prep.services.transformation'));

    beforeEach(inject(($q, TransformationRestService) => {
        spyOn(TransformationRestService, 'getLineTransformations').and.returnValue($q.when({ data: transformationsRestMock() }));
        spyOn(TransformationRestService, 'getColumnTransformations').and.returnValue($q.when({ data: transformationsRestMock() }));
        spyOn(TransformationRestService, 'getColumnSuggestions').and.returnValue($q.when({ data: transformationsRestMock() }));
        spyOn(TransformationRestService, 'getDynamicParameters').and.returnValue($q.when({ data: textClusteringParams() }));
    }));

    describe('dynamic parameters', () => {
        it('should reset params before dynamic params fetch', inject((TransformationService) => {
            //given
            const transformation = {
                name: 'textclustering',
                category: 'quickfix',
                dynamic: true,
                parameters: [],
                cluster: {}
            };
            const infos = {
                datasetId: '78bae6345aef9965e22b54',
                preparationId: '721cd4455fb69e89543d4',
                stepId: '6b9312964fa564e864'
            };

            //when
            TransformationService.initDynamicParameters(transformation, infos);

            //then
            expect(transformation.parameters).toBe(null);
            expect(transformation.cluster).toBe(null);
        }));

        it('should fetch dynamic parameters and inject them into transformation', inject(($rootScope, TransformationService) => {
            //given
            const transformation = {
                name: 'textclustering',
                category: 'quickfix',
                dynamic: true,
                parameters: [],
                cluster: {}
            };
            const infos = {
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
    });

    describe('fetch', () => {
        describe('line transformations', () => {
            it('should get column transformations', inject(($rootScope, TransformationService, TransformationRestService) => {
                //given
                let allTransformations = null;

                //when
                TransformationService.getLineTransformations()
                    .then((result) => {
                        allTransformations = result.allTransformations;
                    });
                $rootScope.$digest();

                //then
                expect(TransformationRestService.getLineTransformations).toHaveBeenCalled();
                expect(allTransformations.length).toBe(4);
                expect(allTransformations[0].name).toBe('uppercase');
                expect(allTransformations[1].name).toBe('lowercase');
                expect(allTransformations[2].name).toBe('cut');
                expect(allTransformations[3].name).toBe('split');
            }));

            it('should sort them by label and group them by category', inject(($rootScope, TransformationService) => {
                //given
                let allCategories = null;

                //when
                TransformationService.getLineTransformations()
                    .then((result) => {
                        allCategories = result.allCategories;
                    });
                $rootScope.$digest();

                //then
                expect(allCategories.length).toBe(2);
                expect(allCategories[0].category).toBe('case');
                expect(allCategories[0].transformations[0].name).toBe('lowercase');
                expect(allCategories[0].transformations[1].name).toBe('uppercase');
                expect(allCategories[1].category).toBe('split');
                expect(allCategories[1].transformations[0].name).toBe('split'); //split is before because label is before cut
                expect(allCategories[1].transformations[1].name).toBe('cut');
            }));

            it('should remove implicit parameters', inject(($rootScope, TransformationService) => {
                //given
                let allTransformations = null;

                //when
                TransformationService.getLineTransformations()
                    .then((result) => {
                        allTransformations = result.allTransformations;
                    });
                $rootScope.$digest();

                //then
                // delete column_name & column_id parameter
                expect(allTransformations[0].parameters).toBe(null);
                expect(allTransformations[1].parameters).toBe(null);
                expect(allTransformations[2].parameters.length).toBe(1);
                expect(allTransformations[3].parameters.length).toBe(1);
            }));

            it('should inject input types', inject(($rootScope, TransformationService) => {
                //given
                let allTransformations = null;

                //when
                TransformationService.getLineTransformations()
                    .then((result) => {
                        allTransformations = result.allTransformations;
                    });
                $rootScope.$digest();

                //then
                expect(allTransformations[2].parameters[0].inputType).toBe('text');
                expect(allTransformations[3].parameters[0].configuration.values[1].inputType).toBe('text');
                expect(allTransformations[3].parameters[0].configuration.values[2].parameters[0].inputType).toBe('number');
                expect(allTransformations[3].parameters[0].configuration.values[3].parameters[0].inputType).toBe('number');
            }));

            it('should inject UI labels', inject(($rootScope, TransformationService) => {
                //given
                let allTransformations = null;

                //when
                TransformationService.getLineTransformations()
                    .then((result) => {
                        allTransformations = result.allTransformations;
                    });
                $rootScope.$digest();

                //then
                expect(allTransformations[0].labelHtml).toBe('Change case to uppercase');
                expect(allTransformations[1].labelHtml).toBe('Change case to lowercase');
                expect(allTransformations[2].labelHtml).toBe('Remove part of the text...');
                expect(allTransformations[3].labelHtml).toBe('Cut in parts...');
            }));
        });

        describe('column transformations', () => {
            it('should get column transformations', inject(($rootScope, TransformationService, TransformationRestService) => {
                //given
                let allTransformations = null;
                const column = { id: '0002' };

                //when
                TransformationService.getColumnTransformations(column)
                    .then((result) => {
                        allTransformations = result.allTransformations;
                    });
                $rootScope.$digest();

                //then
                expect(TransformationRestService.getColumnTransformations).toHaveBeenCalledWith(column);
                expect(allTransformations.length).toBe(4);
                expect(allTransformations[0].name).toBe('uppercase');
                expect(allTransformations[1].name).toBe('lowercase');
                expect(allTransformations[2].name).toBe('cut');
                expect(allTransformations[3].name).toBe('split');
            }));

            it('should sort them by label and group them by category', inject(($rootScope, TransformationService) => {
                //given
                let allCategories = null;
                const column = { id: '0002' };

                //when
                TransformationService.getColumnTransformations(column)
                    .then((result) => {
                        allCategories = result.allCategories;
                    });
                $rootScope.$digest();

                //then
                expect(allCategories.length).toBe(2);
                expect(allCategories[0].category).toBe('case');
                expect(allCategories[0].transformations[0].name).toBe('lowercase');
                expect(allCategories[0].transformations[1].name).toBe('uppercase');
                expect(allCategories[1].category).toBe('split');
                expect(allCategories[1].transformations[0].name).toBe('split'); //split is before because label is before cut
                expect(allCategories[1].transformations[1].name).toBe('cut');
            }));

            it('should remove implicit parameters', inject(($rootScope, TransformationService) => {
                //given
                let allTransformations = null;
                const column = { id: '0002' };

                //when
                TransformationService.getColumnTransformations(column)
                    .then((result) => {
                        allTransformations = result.allTransformations;
                    });
                $rootScope.$digest();

                //then
                // delete column_name & column_id parameter
                expect(allTransformations[0].parameters).toBe(null);
                expect(allTransformations[1].parameters).toBe(null);
                expect(allTransformations[2].parameters.length).toBe(1);
                expect(allTransformations[3].parameters.length).toBe(1);
            }));

            it('should inject input types', inject(($rootScope, TransformationService) => {
                //given
                let allTransformations = null;
                const column = { id: '0002' };

                //when
                TransformationService.getColumnTransformations(column)
                    .then((result) => {
                        allTransformations = result.allTransformations;
                    });
                $rootScope.$digest();

                //then
                expect(allTransformations[2].parameters[0].inputType).toBe('text');
                expect(allTransformations[3].parameters[0].configuration.values[1].inputType).toBe('text');
                expect(allTransformations[3].parameters[0].configuration.values[2].parameters[0].inputType).toBe('number');
                expect(allTransformations[3].parameters[0].configuration.values[3].parameters[0].inputType).toBe('number');
            }));

            it('should inject UI labels', inject(($rootScope, TransformationService) => {
                //given
                let allTransformations = null;
                const column = { id: '0002' };

                //when
                TransformationService.getColumnTransformations(column)
                    .then((result) => {
                        allTransformations = result.allTransformations;
                    });
                $rootScope.$digest();

                //then
                expect(allTransformations[0].labelHtml).toBe('Change case to uppercase');
                expect(allTransformations[1].labelHtml).toBe('Change case to lowercase');
                expect(allTransformations[2].labelHtml).toBe('Remove part of the text...');
                expect(allTransformations[3].labelHtml).toBe('Cut in parts...');
            }));
        });

        describe('column suggestions', () => {
            it('should get column suggestions', inject(($rootScope, TransformationService, TransformationRestService) => {
                //given
                let suggestions = null;
                const column = { id: '0002' };

                //when
                TransformationService.getColumnSuggestions(column)
                    .then((result) => {
                        suggestions = result;
                    });
                $rootScope.$digest();

                //then
                expect(TransformationRestService.getColumnSuggestions).toHaveBeenCalledWith(column);
                expect(suggestions.length).toBe(4);
                expect(suggestions[0].name).toBe('uppercase');
                expect(suggestions[1].name).toBe('lowercase');
                expect(suggestions[2].name).toBe('cut');
                expect(suggestions[3].name).toBe('split');
            }));

            it('should remove implicit parameters', inject(($rootScope, TransformationService, TransformationRestService) => {
                //given
                let suggestions = null;
                const column = { id: '0002' };

                //when
                TransformationService.getColumnSuggestions(column)
                    .then((result) => {
                        suggestions = result;
                    });
                $rootScope.$digest();

                //then
                expect(TransformationRestService.getColumnSuggestions).toHaveBeenCalledWith(column);
                // delete column_name & column_id parameter
                expect(suggestions[0].parameters).toBe(null);
                expect(suggestions[1].parameters).toBe(null);
                expect(suggestions[2].parameters.length).toBe(1);
                expect(suggestions[3].parameters.length).toBe(1);
            }));

            it('should inject input types', inject(($rootScope, TransformationService, TransformationRestService) => {
                //given
                let suggestions = null;
                const column = { id: '0002' };

                //when
                TransformationService.getColumnSuggestions(column)
                    .then((result) => {
                        suggestions = result;
                    });
                $rootScope.$digest();

                //then
                expect(TransformationRestService.getColumnSuggestions).toHaveBeenCalledWith(column);
                expect(suggestions[2].parameters[0].inputType).toBe('text');
                expect(suggestions[3].parameters[0].configuration.values[1].inputType).toBe('text');
                expect(suggestions[3].parameters[0].configuration.values[2].parameters[0].inputType).toBe('number');
                expect(suggestions[3].parameters[0].configuration.values[3].parameters[0].inputType).toBe('number');
            }));

            it('should inject UI labels', inject(($rootScope, TransformationService, TransformationRestService) => {
                //given
                let transformations = null;
                const column = { id: '0002' };

                //when
                TransformationService.getColumnSuggestions(column)
                    .then((result) => {
                        transformations = result;
                    });
                $rootScope.$digest();

                //then
                expect(TransformationRestService.getColumnSuggestions).toHaveBeenCalledWith(column);
                expect(transformations[0].labelHtml).toBe('Change case to uppercase');
                expect(transformations[1].labelHtml).toBe('Change case to lowercase');
                expect(transformations[2].labelHtml).toBe('Remove part of the text...');
                expect(transformations[3].labelHtml).toBe('Cut in parts...');
            }));
        });
    });
});
