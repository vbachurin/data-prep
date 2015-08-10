describe('Suggestions stats aggregation service', function() {
    'use strict';

    beforeEach(module('data-prep.datagrid'));
    beforeEach(module('data-prep.suggestions-stats'));
    beforeEach(module('pascalprecht.translate', function ($translateProvider) {
        $translateProvider.translations('en', {
            'MAX': 'Max'
        });
        $translateProvider.preferredLanguage('en');
    }));

    it('should update aggregation triggered from Column-Profile', inject(function ($rootScope, SuggestionsStatsAggregationsService) {
        //given
        var column = {'id':'0001', 'name': 'city'};

        var  calculation = {id: 'max', name: 'MAX'};

        //when
        SuggestionsStatsAggregationsService.updateAggregationsChanges(column,calculation);

        //then
        expect(SuggestionsStatsAggregationsService.columnAggregationSelected).toBe(column);
        expect(SuggestionsStatsAggregationsService.calculationAggregationSelected).toBe(calculation);
        expect(SuggestionsStatsAggregationsService.aggregationSelected).toBe('Max (city) ');
    }));

    it('should reset aggregation triggered from Playground if other column selected', inject(function ($rootScope, SuggestionsStatsAggregationsService, DatagridService) {

        //given
        var column = {'id':'0001', 'name': 'city'};

        spyOn(DatagridService,'getNumberColumns').and.returnValue([
            {id: '0000', type: 'string', tdpColMetadata: {id: '0000', name: 'col0'}},
            {id: '0001', type: 'number', tdpColMetadata: {id: '0001', name: 'col1'}},
            {id: '0002', type: 'number', tdpColMetadata: {id: '0002', name: 'col2'}},
            {id: '0003', type: 'number', tdpColMetadata: {id: '0003', name: 'col3'}},
            {id: '0004', type: 'number', tdpColMetadata: {id: '0004', name: 'col4'}}]);

        spyOn(SuggestionsStatsAggregationsService,'updateAggregationsChanges').and.returnValue();


        //when
        SuggestionsStatsAggregationsService.updateAggregations(column);

        //then
        expect(SuggestionsStatsAggregationsService.updateAggregationsChanges).toHaveBeenCalledWith(null,null);

    }));

    it('should update aggregation triggered from Playground if same column selected', inject(function ($rootScope, SuggestionsStatsAggregationsService, DatagridService) {

        //given
        var column = {'id':'0001', 'name': 'city1'};

        spyOn(DatagridService,'getNumberColumns').and.returnValue([
            {id: '0000', 'name': 'city1', type: 'number'},
            {id: '0001', 'name': 'city2', type: 'number'}]);

        spyOn(SuggestionsStatsAggregationsService,'updateAggregationsChanges').and.returnValue();

        //when
        SuggestionsStatsAggregationsService.columnAggregationSelected = {'id':'0001', 'name': 'city'};
        SuggestionsStatsAggregationsService.columnSelected = {'id':'0001', 'name': 'city'};
        SuggestionsStatsAggregationsService.calculationAggregationSelected = {id: 'max', name: 'MAX'};
        SuggestionsStatsAggregationsService.updateAggregations(column);


        //then
        expect(SuggestionsStatsAggregationsService.updateAggregationsChanges).toHaveBeenCalledWith(
            SuggestionsStatsAggregationsService.columnAggregationSelected,
            SuggestionsStatsAggregationsService.calculationAggregationSelected);

    }));



});