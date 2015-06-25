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

	var localizationCol = {
		'domain': 'LOCALIZATION',
		'type': 'double'
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

	it('Should add a new filter', inject(function(StatisticsService, FilterService, $timeout) {
		//given
		StatisticsService.selectedColumn = {};
		StatisticsService.selectedColumn.id = 'toto';
		spyOn(FilterService, 'addFilter').and.returnValue();

		//when
		StatisticsService.addFilter('volvo');
		$timeout.flush();

		//then
		expect(FilterService.addFilter).toHaveBeenCalled();
	}));

	it('Should set the data to null, the selectedColumn and the stateDistribution to the column', inject(function(StatisticsService) {
		//when
		StatisticsService.processMapData(barChartStrCol);

		//then
		expect(StatisticsService.data).toBe(null);
		expect(StatisticsService.stateDistribution).toBe(barChartStrCol);
		expect(StatisticsService.selectedColumn).toBe(barChartStrCol);
	}));

	it('Should set the data to the frequencyTable because column type is string', inject(function(StatisticsService) {
		//when
		StatisticsService.processVisuData(barChartStrCol);

		//then
		expect(StatisticsService.data).toBe(barChartStrCol.statistics.frequencyTable);
		expect(StatisticsService.stateDistribution).toBe(null);
	}));

	it('Should set both the data and the stateDistribution to null because column domain is LOCALIZATION', inject(function(StatisticsService) {
		//when
		StatisticsService.processVisuData(localizationCol);

		//then
		expect(StatisticsService.data).toBe(null);
		expect(StatisticsService.stateDistribution).toBe(null);
	}));

	it('Should set the data to the frequencyTable because column type is boolean', inject(function(StatisticsService) {
		//when
		StatisticsService.processVisuData(barChartBoolCol);

		//then
		expect(StatisticsService.data).toBe(barChartBoolCol.statistics.frequencyTable);
		expect(StatisticsService.stateDistribution).toBe(null);
	}));

	it('Should set the data to null and stateDistribution to the column, because the column domain contains STATE_CODE', inject(function(StatisticsService) {
		//when
		StatisticsService.processVisuData(mapCol);

		//then
		expect(StatisticsService.data).toBe(null);
		expect(StatisticsService.stateDistribution).toBe(mapCol);
	}));

	it('should extract Data from the histogram', inject(function(StatisticsService) {
		//when
		var convertedData = StatisticsService.extractNumericData(barChartNumCol.statistics.histogram);

		//then
		expect(convertedData[1].data).toBe(barChartNumCol.statistics.histogram[1].range.min+' ... '+barChartNumCol.statistics.histogram[1].range.max);
	}));

	it('should set the data to the conversion of the histogram, because the column type is number', inject(function(StatisticsService) {
		//when
		StatisticsService.processVisuData(barChartNumCol);

		//then
		expect(StatisticsService.data).toEqual(StatisticsService.extractNumericData(barChartNumCol.statistics.histogram));
		expect(StatisticsService.stateDistribution).toBe(null);
	}));
});
