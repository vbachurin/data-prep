describe('Statistics service', function() {
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
			]
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
			]
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
			]
		}
	};

	var barChartBoolCol =   {
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

	beforeEach(module('data-prep.services.statistics'));

	afterEach(inject(function(StatisticsService) {
		StatisticsService.resetCharts();
	}));

	it('Data type is string and the resulted data must be directly the frequencyTable one', inject(function(StatisticsService) {
		//when
		StatisticsService.processVisuData(barChartStrCol);

		//then
		expect(StatisticsService.data).toBe(barChartStrCol.statistics.frequencyTable);
		expect(StatisticsService.stateDistribution).toBe(null);
	}));

	it('Data type is boolean and the resulted data must be directly the frequencyTable one', inject(function(StatisticsService) {
		//when
		StatisticsService.processVisuData(barChartBoolCol);

		//then
		expect(StatisticsService.data).toBe(barChartBoolCol.statistics.frequencyTable);
		expect(StatisticsService.stateDistribution).toBe(null);
	}));

	it('The domain is STATE_CODE and the resulted data must be null', inject(function(StatisticsService) {
		//when
		StatisticsService.processVisuData(mapCol);

		//then
		expect(StatisticsService.data).toBe(null);
		expect(StatisticsService.stateDistribution).toBe(mapCol);
	}));

	it('Extract Data from the histogram', inject(function(StatisticsService) {
		//when
		var convertedData = StatisticsService.extractNumericData(barChartNumCol.statistics.histogram);

		//then
		expect(convertedData[1].data).toBe(barChartNumCol.statistics.histogram[1].range.min+' ... '+barChartNumCol.statistics.histogram[1].range.max);
	}));

	it('Data type is a number and the resulted data must be the conversion of the histogram', inject(function(StatisticsService) {
		//when
		StatisticsService.processVisuData(barChartNumCol);

		//then
		expect(StatisticsService.data).toEqual(StatisticsService.extractNumericData(barChartNumCol.statistics.histogram));
		expect(StatisticsService.stateDistribution).toBe(null);
	}));
});
