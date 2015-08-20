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

    afterEach(inject(function (StatisticsService) {
        StatisticsService.resetCharts();
    }));

    describe('filters', function () {
        it('should add a new "contains" filter', inject(function (StatisticsService, FilterService, $timeout) {
            //given
            StatisticsService.selectedColumn = {};
            StatisticsService.selectedColumn.id = 'toto';
            spyOn(FilterService, 'addFilter').and.returnValue();

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
            spyOn(FilterService, 'addFilter').and.returnValue();

            //when
            StatisticsService.addFilter('');
            $timeout.flush();

            //then
            expect(FilterService.addFilter).toHaveBeenCalledWith('empty_records', 'toto', undefined, {});
        }));

        it('should add a new "inside_range" filter', inject(function (StatisticsService, FilterService, $timeout) {
            //given
            StatisticsService.selectedColumn = {};
            StatisticsService.selectedColumn.id = '0000';
            spyOn(FilterService, 'addFilter').and.returnValue();
            StatisticsService.rangeLimits = {
                min : -5,
                max : 25
            };
            //when
            StatisticsService.addRangeFilter([0,22]);
            $timeout.flush();

            //then
            expect(FilterService.addFilter).toHaveBeenCalledWith('inside_range', '0000', undefined, {phrase:[0,22]});
            expect(StatisticsService.rangeLimits).toEqual({
                min : -5,
                minBrush : 0,
                max : 25,
                maxBrush : 22
            });
        }));
    });

    describe('The Visualization data for Horizontal barchart and Map', function () {
        describe('Map data', function() {
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
                StatisticsService.boxplotData = {};
                StatisticsService.data = {};

                //when
                StatisticsService.processData(mapCol);

                //then
                expect(StatisticsService.boxplotData).toBeFalsy();
                expect(StatisticsService.data).toBeFalsy();
            }));
        });

        describe('Histogram data', function() {
            it('should set the frequency data when column type is "string"', inject(function (StatisticsService, TextFormatService) {
                //given
                expect(StatisticsService.data).toBeFalsy();

                //when
                StatisticsService.processData(barChartStrCol);

                //then
                expect(StatisticsService.data).toEqual(barChartStrCol.statistics.frequencyTable);
                expect(StatisticsService.data[0].formattedValue).toEqual(TextFormatService.computeHTMLForLeadingOrTrailingHiddenChars(StatisticsService.data[0].data));
            }));

            it('should reset non histogram data when column type is "string"', inject(function (StatisticsService) {
                //given
                StatisticsService.boxplotData = {};
                StatisticsService.stateDistribution = {};

                //when
                StatisticsService.processData(barChartStrCol);

                //then
                expect(StatisticsService.boxplotData).toBeFalsy();
                expect(StatisticsService.stateDistribution).toBeFalsy();
            }));

            it('should set the frequency data when column type is "boolean"', inject(function (StatisticsService) {
                //given
                expect(StatisticsService.data).toBeFalsy();

                //when
                StatisticsService.processData(barChartBoolCol);

                //then
                expect(StatisticsService.data).toEqual(barChartBoolCol.statistics.frequencyTable);
            }));

            it('should reset non histogram data when column type is "boolean"', inject(function (StatisticsService) {
                //given
                StatisticsService.boxplotData = {};
                StatisticsService.stateDistribution = {};

                //when
                StatisticsService.processData(barChartBoolCol);

                //then
                expect(StatisticsService.boxplotData).toBeFalsy();
                expect(StatisticsService.stateDistribution).toBeFalsy();
            }));

            it('should set the range data frequency when column type is "number"', inject(function (StatisticsService) {
                //given
                expect(StatisticsService.data).toBeFalsy();

                //when
                StatisticsService.processData(barChartNumCol);

                //then
                expect(StatisticsService.data[1].data).toBe(barChartNumCol.statistics.histogram[1].range.min + ' ... ' + barChartNumCol.statistics.histogram[1].range.max);
            }));

            it('should reset non histogram data when column type is "number"', inject(function (StatisticsService) {
                //given
                StatisticsService.stateDistribution = {};

                //when
                StatisticsService.processData(barChartNumCol);

                //then
                expect(StatisticsService.stateDistribution).toBeFalsy();
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

        it('should reset data when column type is not supported', inject(function (StatisticsService) {
            //given
            StatisticsService.boxplotData = {};
            StatisticsService.data = {};
            StatisticsService.stateDistribution = {};

            //when
            StatisticsService.processData(unknownTypeCol);

            //then
            expect(StatisticsService.data).toBeFalsy();
            expect(StatisticsService.boxplotData).toBeFalsy();
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
                        averageLengthWithBlank: 11.783242375675245,
                        minimalLength: 12,
                        minimalLengthWithBlank: 13,
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
                AVG_LENGTH: 10.13,
                AVG_LENGTH_WITH_BLANK: 11.78,
                MIN_LENGTH: 12,
                MIN_LENGTH_WITH_BLANK: 13,
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
            expect(StatisticsService.boxplotData).toBeFalsy();
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
            expect(StatisticsService.boxplotData).toEqual({
                min:10,
                max:11,
                q1:15,
                q2:16,
                median:14,
                mean:12,
                variance:13
            });
        }));
    });

    it('should reset all data', inject(function(StatisticsService) {
        //given
        StatisticsService.boxplotData = {};
        StatisticsService.data = {};
        StatisticsService.stateDistribution = {};
        StatisticsService.statistics = {};

        //when
        StatisticsService.resetCharts();

        //then
        expect(StatisticsService.boxplotData).toBeFalsy();
        expect(StatisticsService.data).toBeFalsy();
        expect(StatisticsService.stateDistribution).toBeFalsy();
        expect(StatisticsService.statistics).toBeFalsy();
        expect(StatisticsService.rangeLimits).toBeFalsy();
    }));

    /***************************** rangeSlider **************************************/
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
        FilterService.filters = [{colId:'0001', type:'inside_range', args:{phrase:[5,10]}}];

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
});
