describe('Statistics service', function () {
    'use strict';

    var barChartNumCol = {
        'domain': 'barchartAndNumeric',
        'type': 'numeric',
        'statistics': {
            'frequencyTable': [],
            'histogram': [
                {
                    'occurrences': 5,
                    'range': {
                        'min': 0,
                        'max': 10
                    }
                },
                {
                    'occurrences': 15,
                    'range': {
                        'min': 10,
                        'max': 20
                    }
                }
            ],
            count: 4,
            distinctCount: 5,
            duplicateCount: 6,
            empty: 7,
            invalid: 8,
            valid: 9,
            min: 10,
            max: 11,
            mean: 12,
            variance: 13,
            quantiles: {
                lowerQuantile: 'NaN'
            }
        }
    };

    /*var localizationCol = {
        'domain': 'LOCALIZATION',
        'type': 'double',
        'statistics': {
            'frequencyTable': [],
            'histogram': [
                {
                    'occurrences': 5,
                    'range': {
                        'min': 0,
                        'max': 10
                    }
                },
                {
                    'occurrences': 15,
                    'range': {
                        'min': 10,
                        'max': 20
                    }
                }
            ],
            count: 4,
            distinctCount: 5,
            duplicateCount: 6,
            empty: 7,
            invalid: 8,
            valid: 9,
            min: 10,
            max: 11,
            mean: 12,
            variance: 13,
            quantiles: {
                lowerQuantile: 'NaN'
            }
        }
    };*/

    var barChartStrCol = {
        'domain': 'barchartAndString',
        'type': 'string',
        'statistics': {
            'frequencyTable': [
                {
                    'data': '   toto',
                    'occurences': 202
                },
                {
                    'data': 'titi',
                    'occurences': 2
                },
                {
                    'data': 'coucou',
                    'occurences': 102
                },
                {
                    'data': 'cici',
                    'occurences': 22
                }
            ],
            textLengthSummary: {
                averageLength: 10.13248646854654,
                averageLengthIgnoreBlank: 11.783242375675245,
                minimalLength: 12,
                minimalLengthIgnoreBlank: 13,
                maximalLength: 14
            },
            count: 4,
            distinctCount: 5,
            duplicateCount: 6,
            empty: 7,
            invalid: 8,
            valid: 9,
            min: 10,
            max: 11,
            mean: 12,
            variance: 13,
            quantiles: {
                lowerQuantile: 'NaN'
            }
        }
    };

    var mapCol = {
        'domain': 'US_STATE_CODE',
        'type': '',
        'statistics': {
            'frequencyTable': [
                {
                    'data': 'MI',
                    'occurences': 202
                },
                {
                    'data': 'WA',
                    'occurences': 2
                },
                {
                    'data': 'DE',
                    'occurences': 102
                },
                {
                    'data': 'IL',
                    'occurences': 22
                }
            ],
            textLengthSummary: {
                averageLength: 10.13248646854654,
                averageLengthWithBlank: 11.783242375675245,
                minimalLength: 12,
                minimalLengthWithBlank: 13,
                maximalLength: 14
            },
            count: 4,
            distinctCount: 5,
            duplicateCount: 6,
            empty: 7,
            invalid: 8,
            valid: 9,
            min: 10,
            max: 11,
            mean: 12,
            variance: 13,
            quantiles: {
                lowerQuantile: 'NaN'
            }
        }
    };

    var barChartBoolCol = {
        'domain': 'barchartAndBool',
        'type': 'boolean',
        'statistics': {
            'frequencyTable': [
                {
                    'data': 'true',
                    'occurences': 2
                },
                {
                    'data': 'false',
                    'occurences': 20
                },
                {
                    'data': '',
                    'occurences': 10
                }
            ]
        }
    };

    var unknownTypeCol = {
        'domain': '',
        'type': 'unknown'
    };

    beforeEach(module('data-prep.services.statistics'));

    describe('filters', function () {
        beforeEach(inject(function(FilterService) {
            spyOn(FilterService, 'addFilter').and.returnValue();
        }));

        it('should add a new "contains" filter', inject(function (StatisticsService, FilterService, $timeout) {
            //given
            StatisticsService.selectedColumn = {};
            StatisticsService.selectedColumn.id = 'toto';

            //when
            StatisticsService.addFilter('volvo');
            $timeout.flush();

            //then
            expect(FilterService.addFilter).toHaveBeenCalledWith('contains', 'toto', undefined, {phrase: 'volvo'});
        }));

        it('should add a new "empty" filter', inject(function (StatisticsService, FilterService, $timeout) {
            //given
            StatisticsService.selectedColumn = {};
            StatisticsService.selectedColumn.id = 'toto';

            //when
            StatisticsService.addFilter('');
            $timeout.flush();

            //then
            expect(FilterService.addFilter).toHaveBeenCalledWith('empty_records', 'toto', undefined, {});
        }));

        it('should add a new "inside_range" filter', inject(function (StatisticsService, FilterService, $timeout) {
            //given
            StatisticsService.rangeLimits = {};
            StatisticsService.selectedColumn = {id: '0000', statistics: {min: 5, max: 55}};

            //when
            StatisticsService.addRangeFilter([0,22]);
            $timeout.flush();

            //then
            expect(FilterService.addFilter).toHaveBeenCalled();

            var args = FilterService.addFilter.calls.argsFor(0);
            expect(args[0]).toBe('inside_range');
            expect(args[1]).toBe('0000');
            expect(args[2]).not.toBeDefined();
            expect(args[3]).toEqual({interval:[0,22]});
        }));

        it('should update rangeLimits brush on new "inside_range" filter add', inject(function (StatisticsService, FilterService, $timeout) {
            //given
            StatisticsService.rangeLimits = {};
            StatisticsService.selectedColumn = {id: '0000', statistics: {min: 5, max: 55}};

            //when
            StatisticsService.addRangeFilter([10,22]);
            $timeout.flush();

            //then
            expect(StatisticsService.rangeLimits.minBrush).toBe(10);
            expect(StatisticsService.rangeLimits.maxBrush).toBe(22);
        }));

        it('should reinit range limits on "inside_range" filter remove when the selected column is the same', inject(function (StatisticsService, FilterService, $timeout) {
            //given
            var originalRangeLimits = {};
            StatisticsService.rangeLimits = originalRangeLimits;
            StatisticsService.selectedColumn = {id: '0000', statistics: {min: 5, max: 55}};

            StatisticsService.addRangeFilter([0,22]);
            $timeout.flush();

            expect(StatisticsService.rangeLimits).toBe(originalRangeLimits);
            expect(FilterService.addFilter).toHaveBeenCalled();
            var removeCallback = FilterService.addFilter.calls.argsFor(0)[4];

            //when
            removeCallback({colId: '0000'});

            //then
            expect(StatisticsService.rangeLimits).not.toBe(originalRangeLimits);
            expect(StatisticsService.rangeLimits).toEqual({
                min: 5,
                max: 55,
                minBrush: undefined,
                maxBrush: undefined
            });
        }));

        it('should do nothing on "inside_range" filter remove when the selected column is NOT the same', inject(function (StatisticsService, FilterService, $timeout) {
            //given
            var originalRangeLimits = {};
            StatisticsService.rangeLimits = originalRangeLimits;
            StatisticsService.selectedColumn = {id: '0000', statistics: {min: 5, max: 55}};

            StatisticsService.addRangeFilter([0,22]);
            $timeout.flush();

            expect(StatisticsService.rangeLimits).toBe(originalRangeLimits);
            expect(FilterService.addFilter).toHaveBeenCalled();
            var removeCallback = FilterService.addFilter.calls.argsFor(0)[4];

            //when
            removeCallback({colId: '0001'});

            //then
            expect(StatisticsService.rangeLimits).toBe(originalRangeLimits);
        }));
    });

    describe('The Visualization data for Horizontal barchart and Map', function () {
        describe('Map data', function () {
            it('should set stateDistribution for geo chart when the column domain contains STATE_CODE', inject(function (StatisticsService) {
                //given
                expect(StatisticsService.stateDistribution).toBeFalsy();

                //when
                StatisticsService.processData(mapCol);

                //then
                expect(StatisticsService.stateDistribution).toBe(mapCol);
            }));

            it('should reset non geo chart data when the column domain contains STATE_CODE', inject(function (StatisticsService) {
                //given
                StatisticsService.boxPlot = {};
                StatisticsService.histogram = {};

                //when
                StatisticsService.processData(mapCol);

                //then
                expect(StatisticsService.boxPlot).toBeFalsy();
                expect(StatisticsService.histogram).toBeFalsy();
            }));
        });

        describe('Histogram data', function () {
            it('should reset non histogram data when column type is "string"', inject(function (StatisticsService) {
                //given
                StatisticsService.boxPlot = {};
                StatisticsService.stateDistribution = {};

                //when
                StatisticsService.processData(barChartStrCol);

                //then
                expect(StatisticsService.boxPlot).toBeFalsy();
                expect(StatisticsService.stateDistribution).toBeFalsy();
            }));

            it('should set the frequency data with formatted value when column type is "string"', inject(function (StatisticsService) {
                //given
                expect(StatisticsService.histogram).toBeFalsy();

                //when
                StatisticsService.processData(barChartStrCol);

                //then
                expect(StatisticsService.histogram).toEqual({
                    data: [
                        {data: '   toto', occurences: 202, formattedValue: '<span class="hiddenChars">   </span>toto'},
                        {data: 'titi', occurences: 2, formattedValue: 'titi'},
                        {data: 'coucou', occurences: 102, formattedValue: 'coucou'},
                        {data: 'cici', occurences: 22, formattedValue: 'cici'}
                    ],
                    key: 'occurrences',
                    label: 'Occurrences',
                    column: barChartStrCol
                });
            }));

            it('should reset non histogram data when column type is "boolean"', inject(function (StatisticsService) {
                //given
                StatisticsService.boxPlot = {};
                StatisticsService.stateDistribution = {};

                //when
                StatisticsService.processData(barChartBoolCol);

                //then
                expect(StatisticsService.boxPlot).toBeFalsy();
                expect(StatisticsService.stateDistribution).toBeFalsy();
            }));

            it('should set the frequency data when column type is "boolean"', inject(function (StatisticsService) {
                //given
                expect(StatisticsService.histogram).toBeFalsy();

                //when
                StatisticsService.processData(barChartBoolCol);

                //then
                expect(StatisticsService.histogram).toEqual({
                    key: 'occurrences',
                    label: 'Occurrences',
                    column: barChartBoolCol,
                    data: barChartBoolCol.statistics.frequencyTable
                });
            }));

            it('should reset non histogram data when column type is "number"', inject(function (StatisticsService) {
                //given
                StatisticsService.stateDistribution = {};

                //when
                StatisticsService.processData(barChartNumCol);

                //then
                expect(StatisticsService.boxPlot).toBeFalsy();
                expect(StatisticsService.stateDistribution).toBeFalsy();
            }));

            it('should set the range data frequency when column type is "number"', inject(function (StatisticsService) {
                //given
                expect(StatisticsService.histogram).toBeFalsy();

                //when
                StatisticsService.processData(barChartNumCol);

                //then
                expect(StatisticsService.histogram.data[1].data).toBe(barChartNumCol.statistics.histogram[1].range.min + ' ... ' + barChartNumCol.statistics.histogram[1].range.max);
            }));
        });

        //TODO coming soon with the globe map
        // it('should set both the data and the stateDistribution to null because column domain is LOCALIZATION', inject(function (StatisticsService) {
        //    //when
        //    StatisticsService.processData(localizationCol);
        //
        //    //then
        //    expect(StatisticsService.data).toBe(null);
        //    expect(StatisticsService.boxplotData).toBe(null);
        //    expect(StatisticsService.stateDistribution).toBe(null);
        //}));

        it('should reset charts data when column type is not supported', inject(function (StatisticsService) {
            //given
            StatisticsService.boxPlot = {};
            StatisticsService.histogram = {};
            StatisticsService.stateDistribution = {};

            //when
            StatisticsService.processData(unknownTypeCol);

            //then
            expect(StatisticsService.histogram).toBeFalsy();
            expect(StatisticsService.boxPlot).toBeFalsy();
            expect(StatisticsService.stateDistribution).toBeFalsy();
        }));
    });

    describe('The statistics values', function () {
        it('should init common statistics when the column type is not "number" or "text"', inject(function (StatisticsService) {
            //given
            var col = {
                id: '0001',
                type: 'boolean',
                domain: 'US_STATE_CODE',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9
                }
            };

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.statistics).toBeTruthy();
            expect(StatisticsService.statistics.common).toEqual({
                COUNT: 4,
                DISTINCT_COUNT: 5,
                DUPLICATE_COUNT: 6,
                VALID: 9,
                EMPTY: 7,
                INVALID: 8
            });
            expect(StatisticsService.statistics.specific).toEqual({});
        }));

        it('should init number statistics whithout quantile', inject(function (StatisticsService) {
            //given
            var col = {
                'id': '0001',
                type: 'integer',
                domain: 'city name',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9,
                    min: 10,
                    max: 11,
                    mean: 12,
                    variance: 13,
                    quantiles: {
                        lowerQuantile: 'NaN'
                    }
                }
            };

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.statistics).toBeTruthy();
            expect(StatisticsService.statistics.common).toEqual({
                COUNT: 4,
                DISTINCT_COUNT: 5,
                DUPLICATE_COUNT: 6,
                VALID: 9,
                EMPTY: 7,
                INVALID: 8
            });
            expect(StatisticsService.statistics.specific).toEqual({
                MIN: 10,
                MAX: 11,
                MEAN: 12,
                VARIANCE: 13
            });
        }));

        it('should init number statistics with quantile', inject(function (StatisticsService) {
            //given
            var col = {
                'id': '0001',
                type: 'integer',
                domain: 'code postal',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9,
                    min: 10,
                    max: 11,
                    mean: 12,
                    variance: 13,
                    quantiles: {
                        median: 14,
                        lowerQuantile: 15,
                        upperQuantile: 16
                    }
                }
            };

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.statistics).toBeTruthy();
            expect(StatisticsService.statistics.common).toEqual({
                COUNT: 4,
                DISTINCT_COUNT: 5,
                DUPLICATE_COUNT: 6,
                VALID: 9,
                EMPTY: 7,
                INVALID: 8
            });
            expect(StatisticsService.statistics.specific).toEqual({
                MIN: 10,
                MAX: 11,
                MEAN: 12,
                VARIANCE: 13,
                MEDIAN: 14,
                LOWER_QUANTILE: 15,
                UPPER_QUANTILE: 16
            });
        }));

        it('should init text statistics', inject(function (StatisticsService) {
            //given
            var col = {
                'id': '0001',
                type: 'string',
                domain: 'text',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9,
                    textLengthSummary: {
                        averageLength: 10.13248646854654,
                        averageLengthIgnoreBlank: 11.783242375675245,
                        minimalLength: 12,
                        minimalLengthIgnoreBlank: 13,
                        maximalLength: 14
                    }
                }
            };
            expect(StatisticsService.statistics).toBeFalsy();

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.statistics).toBeTruthy();
            expect(StatisticsService.statistics.common).toEqual({
                COUNT: 4,
                DISTINCT_COUNT: 5,
                DUPLICATE_COUNT: 6,
                VALID: 9,
                EMPTY: 7,
                INVALID: 8
            });
            expect(StatisticsService.statistics.specific).toEqual({
                AVG_LENGTH: 11.78,
                AVG_LENGTH_WITH_BLANK: 10.13,
                MIN_LENGTH: 13,
                MIN_LENGTH_WITH_BLANK: 12,
                MAX_LENGTH: 14
            });
        }));
    });

    describe('The boxplot data', function () {
        it('should reset boxplotData when quantile values are NaN', inject(function (StatisticsService) {
            //given
            var col = {
                'id': '0001',
                type: 'integer',
                domain: 'city name',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9,
                    min: 10,
                    max: 11,
                    mean: 12,
                    variance: 13,
                    quantiles: {
                        lowerQuantile: 'NaN'
                    }
                }
            };

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.boxPlot).toBeFalsy();
        }));

        it('should set boxplotData statistics with quantile', inject(function (StatisticsService) {
            //given
            var col = {
                'id': '0001',
                type: 'integer',
                domain: 'code postal',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9,
                    min: 10,
                    max: 11,
                    mean: 12,
                    variance: 13,
                    quantiles: {
                        median: 14,
                        lowerQuantile: 15,
                        upperQuantile: 16
                    }
                }
            };

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.boxPlot).toEqual({
                min: 10,
                max: 11,
                q1: 15,
                q2: 16,
                median: 14,
                mean: 12,
                variance: 13
            });
        }));
    });

    describe('Aggregation statistics', function () {
        var currentColumn = {'id': '0001', 'name': 'city'}; // the selected column
        var datasetId = 'abcd';                             // the current data id
        var preparationId = '2132548345365';                // the current preparation id
        var stepId = '9878645468';                          // the currently viewed step id
        var sampleSize = 500;                               // the sample size
        var column = {'id': '0002', 'name': 'state'};       // the column where to perform the aggregation
        var aggregation = 'MAX';                            // the aggregation operation

        var getAggregationsResponse = [                     // the REST aggregation GET result
            {'data': 'Lansing', 'max': 15},
            {'data': 'Helena', 'max': 5},
            {'data': 'Baton Rouge', 'max': 64},
            {'data': 'Annapolis', 'max': 4},
            {'data': 'Pierre', 'max': 104}
        ];

        beforeEach(inject(function(StatisticsService) {
            StatisticsService.selectedColumn = currentColumn;
        }));

        it('should update histogram data with classical occurence histogram when no aggregation is provided', inject(function ($q, $rootScope, StatisticsService, StatisticsRestService) {
            //given
            StatisticsService.selectedColumn = barChartStrCol;
            spyOn(StatisticsRestService, 'getAggregations').and.returnValue($q.when());

            //when
            StatisticsService.processAggregation();

            //then
            expect(StatisticsRestService.getAggregations).not.toHaveBeenCalled();
            expect(StatisticsService.histogram).toEqual({
                data: [
                    {data: '   toto', occurences: 202, formattedValue: '<span class="hiddenChars">   </span>toto'},
                    {data: 'titi', occurences: 2, formattedValue: 'titi'},
                    {data: 'coucou', occurences: 102, formattedValue: 'coucou'},
                    {data: 'cici', occurences: 22, formattedValue: 'cici'}
                ],
                key: 'occurrences',
                label: 'Occurrences',
                column: barChartStrCol
            });
        }));

        it('should update histogram data from REST call result and aggregation infos', inject(function ($q, $rootScope, StatisticsService, StatisticsRestService) {
            //given
            spyOn(StatisticsRestService, 'getAggregations').and.returnValue($q.when(getAggregationsResponse));

            //when
            StatisticsService.processAggregation(datasetId, preparationId, stepId, sampleSize, column, aggregation);
            $rootScope.$digest();

            //then
            expect(StatisticsRestService.getAggregations).toHaveBeenCalledWith({
                datasetId: 'abcd',
                preparationId: '2132548345365',
                stepId: '9878645468',
                sampleSize: 500,
                operations: [{operator: 'MAX', columnId: '0002'}],
                groupBy: ['0001']
            });
            expect(StatisticsService.histogram).toEqual({
                data: [
                    {'data': 'Lansing', 'max': 15, 'formattedValue': 'Lansing'},
                    {'data': 'Helena', 'max': 5, 'formattedValue': 'Helena'},
                    {'data': 'Baton Rouge', 'max': 64, 'formattedValue': 'Baton Rouge'},
                    {'data': 'Annapolis', 'max': 4, 'formattedValue': 'Annapolis'},
                    {'data': 'Pierre', 'max': 104, 'formattedValue': 'Pierre'}
                ],
                key: 'MAX',
                label: 'MAX',
                column: StatisticsService.selectedColumn,
                aggregationColumn: column,
                aggregation: aggregation
            });
        }));

        it('should update histogram with content from cache', inject(function ($q, $rootScope, StatisticsService, StatisticsRestService) {
            //given
            spyOn(StatisticsRestService, 'getAggregations').and.returnValue($q.when(getAggregationsResponse));

            //given : rest call to populate cache
            StatisticsService.processAggregation(datasetId, preparationId, stepId, sampleSize, column, aggregation);
            $rootScope.$digest();
            expect(StatisticsRestService.getAggregations.calls.count()).toBe(1);
            StatisticsService.histogram = null;

            //when
            StatisticsService.processAggregation(datasetId, preparationId, stepId, sampleSize, column, aggregation);
            $rootScope.$digest();

            //then
            expect(StatisticsRestService.getAggregations.calls.count()).toBe(1);
            expect(StatisticsService.histogram).toEqual({
                data: [
                    {'data': 'Lansing', 'max': 15, 'formattedValue': 'Lansing'},
                    {'data': 'Helena', 'max': 5, 'formattedValue': 'Helena'},
                    {'data': 'Baton Rouge', 'max': 64, 'formattedValue': 'Baton Rouge'},
                    {'data': 'Annapolis', 'max': 4, 'formattedValue': 'Annapolis'},
                    {'data': 'Pierre', 'max': 104, 'formattedValue': 'Pierre'}
                ],
                key: 'MAX',
                label: 'MAX',
                column: StatisticsService.selectedColumn,
                aggregationColumn: column,
                aggregation: aggregation
            });
        }));

        it('should reset histogram when REST WS call fails', inject(function ($q, $rootScope, StatisticsService, StatisticsRestService) {
            //given
            spyOn(StatisticsRestService, 'getAggregations').and.returnValue($q.reject());

            //when
            StatisticsService.processAggregation(datasetId, preparationId, stepId, sampleSize, column, aggregation);
            $rootScope.$digest();

            //then
            expect(StatisticsService.histogram).toBeFalsy();
        }));
    });

    describe('The range slider', function() {
        it('should set range and brush limits to the min and max of the column', inject(function (StatisticsService, FilterService) {
            //given
            var col = {
                'id': '0001',
                type: 'integer',
                domain: 'city name',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9,
                    min: 10,
                    max: 11,
                    mean: 12,
                    variance: 13,
                    quantiles: {
                        lowerQuantile: 'NaN'
                    }
                }
            };
            FilterService.filters = [];

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.rangeLimits).toEqual({
                min : 10,
                max : 11,
                minBrush : undefined,
                maxBrush : undefined
            });
        }));

        it('should update the brush limits to the existent ones', inject(function (StatisticsService, FilterService) {
            //given
            var col = {
                'id': '0001',
                type: 'integer',
                domain: 'city name',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9,
                    min: 0,
                    max: 11,
                    mean: 12,
                    variance: 13,
                    quantiles: {
                        lowerQuantile: 'NaN'
                    }
                }
            };
            FilterService.filters = [{colId:'0001', type:'inside_range', args:{interval:[5,10]}}];

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.rangeLimits).toEqual({
                min : 0,
                max : 11,
                minBrush :5,
                maxBrush :10
            });
        }));

        it('should update the brush limits to the minimum', inject(function (StatisticsService, FilterService) {
            //given
            var col = {
                'id': '0001',
                type: 'integer',
                domain: 'city name',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9,
                    min: 0,
                    max: 11,
                    mean: 12,
                    variance: 13,
                    quantiles: {
                        lowerQuantile: 'NaN'
                    }
                }
            };
            // -5 < 0(minimum)
            FilterService.filters = [{colId:'0001', type:'inside_range', args:{interval:[-5,10]}}];

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.rangeLimits).toEqual({
                min : 0,
                max : 11,
                minBrush :0,
                maxBrush :0,
                minFilterVal: -5,
                maxFilterVal: 10
            });
        }));

        it('should update the brush limits to the maximum', inject(function (StatisticsService, FilterService) {
            //given
            var col = {
                'id': '0001',
                type: 'integer',
                domain: 'city name',
                statistics: {
                    count: 4,
                    distinctCount: 5,
                    duplicateCount: 6,
                    empty: 7,
                    invalid: 8,
                    valid: 9,
                    min: 0,
                    max: 11,
                    mean: 12,
                    variance: 13,
                    quantiles: {
                        lowerQuantile: 'NaN'
                    }
                }
            };
            FilterService.filters = [{colId:'0001', type:'inside_range', args:{interval:[5,30]}}];

            //when
            StatisticsService.processData(col);

            //then
            expect(StatisticsService.rangeLimits).toEqual({
                min : 0,
                max : 11,
                minBrush :11,
                maxBrush :11,
                minFilterVal: 5,
                maxFilterVal: 30
            });
        }));
    });

    describe('utils', function() {
        it('should reset all data', inject(function (StatisticsService) {
            //given
            StatisticsService.boxPlot = {};
            StatisticsService.histogram = {};
            StatisticsService.stateDistribution = {};
            StatisticsService.statistics = {};

            //when
            StatisticsService.resetCharts();

            //then
            expect(StatisticsService.boxPlot).toBeFalsy();
            expect(StatisticsService.histogram).toBeFalsy();
            expect(StatisticsService.stateDistribution).toBeFalsy();
            expect(StatisticsService.statistics).toBeFalsy();
        }));

        it('should get numeric columns (as aggregation columns) from datagrid service', inject(function(StatisticsService, DatagridService) {
            //given
            var selectedcolumn = {id: '0001'};
            StatisticsService.selectedColumn = selectedcolumn;

            var datagridNumericColumns = [
                {id: '0002'},
                {id: '0003'}
            ];
            spyOn(DatagridService, 'getNumericColumns').and.returnValue(datagridNumericColumns);

            //when
            var aggregationColumns = StatisticsService.getAggregationColumns();

            //then
            expect(aggregationColumns).toBe(datagridNumericColumns);
            expect(DatagridService.getNumericColumns).toHaveBeenCalledWith(selectedcolumn);
        }));
    });

});
