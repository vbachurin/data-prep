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

    var localizationCol = {
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
    };

    var barChartStrCol = {
        'domain': 'barchartAndString',
        'type': 'string',
        'statistics': {
            'frequencyTable': [
                {
                    'data': 'toto',
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
        'domain': 'STATE_CODE_US',
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
    });

    describe('The Visualization Tab: Horizontal barchart and Map', function () {
        it('should set the data to the frequencyTable because column type is string', inject(function (StatisticsService) {
            //when
            StatisticsService.processVisuData(barChartStrCol);

            //then
            expect(StatisticsService.data).toBe(barChartStrCol.statistics.frequencyTable);
            expect(StatisticsService.boxplotData).toBe(null);
            expect(StatisticsService.stateDistribution).toBe(null);
        }));

        it('should set both the data and the stateDistribution to null because column domain is LOCALIZATION', inject(function (StatisticsService) {
            //when
            StatisticsService.processVisuData(localizationCol);

            //then
            expect(StatisticsService.data).toBe(null);
            expect(StatisticsService.boxplotData).toBe(null);
            expect(StatisticsService.stateDistribution).toBe(null);
        }));

        it('should set the data to the frequencyTable because column type is boolean', inject(function (StatisticsService) {
            //when
            StatisticsService.processVisuData(barChartBoolCol);

            //then
            expect(StatisticsService.data).toBe(barChartBoolCol.statistics.frequencyTable);
            expect(StatisticsService.boxplotData).toBe(null);
            expect(StatisticsService.stateDistribution).toBe(null);
        }));

        it('should set the data to null and stateDistribution to the column, because the column domain contains STATE_CODE', inject(function (StatisticsService) {
            //when
            StatisticsService.processVisuData(mapCol);

            //then
            expect(StatisticsService.data).toBe(null);
            expect(StatisticsService.boxplotData).toBe(null);
            expect(StatisticsService.stateDistribution).toBe(mapCol);
        }));

        it('should extract Data from the histogram', inject(function (StatisticsService) {
            //when
            var convertedData = StatisticsService.extractNumericData(barChartNumCol.statistics.histogram);

            //then
            expect(convertedData[1].data).toBe(barChartNumCol.statistics.histogram[1].range.min + ' ... ' + barChartNumCol.statistics.histogram[1].range.max);
        }));

        it('should set the data to the conversion of the histogram, because the column type is number', inject(function (StatisticsService) {
            //when
            StatisticsService.processVisuData(barChartNumCol);

            //then
            expect(StatisticsService.data).toEqual(StatisticsService.extractNumericData(barChartNumCol.statistics.histogram));
            expect(StatisticsService.boxplotData).toBe(null);
            expect(StatisticsService.stateDistribution).toBe(null);
        }));

        it('should reset data when column type is not supported', inject(function (StatisticsService) {
            //when
            StatisticsService.processVisuData(unknownTypeCol);

            //then
            expect(StatisticsService.data).toBe(null);
            expect(StatisticsService.boxplotData).toBe(null);
            expect(StatisticsService.stateDistribution).toBe(null);
        }));
    });


    describe('The Value Tab: values texts', function () {
        it('should init common statistics when the column type is a boolean', inject(function (StatisticsService) {
            //given
            var col = {
                id: '0001',
                type: 'boolean',
                domain: 'STATE_CODE_US',
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
            StatisticsService.processVisuData(col);

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
            StatisticsService.processVisuData(col);

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
            StatisticsService.processVisuData(col);

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
            StatisticsService.processVisuData(col);

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

    describe('The OTHER Tab: boxplot data', function () {
        it('should set boxplotData to null because quantile values are Nan', inject(function (StatisticsService) {
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
            StatisticsService.processVisuData(col);

            //then
            expect(StatisticsService.boxplotData).toBe(null);
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
            StatisticsService.processVisuData(col);

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
});
