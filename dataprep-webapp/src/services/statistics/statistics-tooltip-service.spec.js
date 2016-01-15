/*jshint camelcase: false */

describe('Statistics Tooltip service', function () {
    'use strict';
    var stateMock;

    beforeEach(module('data-prep.services.statistics', function ($provide) {
        stateMock = {playground: {}};
        stateMock.playground.filter = {
            gridFilters: [88]
        };
        $provide.constant('state', stateMock);
    }));

    describe('in case of filters', function () {
        it('should construct tooltip template for horizontal chart', inject(function (StatisticsTooltipService) {
            //given
            var data  = {'formattedValue':'96ebf96df2','occurrences':1,'data':'96ebf96df2'};
            var secData = {'formattedValue':'96ebf96df2','filteredOccurrences':1};

            //when
            var template = StatisticsTooltipService.getTooltipTemplate(data, secData,  'formattedValue', 'Occurrences', 'occurrences', 'filteredOccurrences');

            //then
            expect(template).toBe(
                '<strong>Occurrences matching your filter: </strong><span style="color:yellow">1 (100.0%)</span>'+
                '<br/><br/>'+
                '<strong>Occurrences in entire dataset:</strong> <span style="color:yellow">1 </span>'+
                '<br/><br/>'+
                '<strong>Record:</strong> <span style="color:yellow">96ebf96df2</span>');
        }));

        it('should construct tooltip template for vertical chart', inject(function (StatisticsTooltipService) {
            //given
            var data  = {'data':{'type':'number','min':-9.375,'max':2},'occurrences':1};
            var secData = {'data':{'type':'number','min':-9.375,'max':2},'filteredOccurrences':0};

            //when
            var template = StatisticsTooltipService.getTooltipTemplate(data, secData, 'data', 'Occurrences', 'occurrences', 'filteredOccurrences');

            //then
            expect(template).toBe(
                '<strong>Occurrences matching your filter: </strong><span style="color:yellow">0 (0.0%)</span>'+
                '<br/><br/>'+
                '<strong>Occurrences in entire dataset:</strong> <span style="color:yellow">1 </span>'+
                '<br/><br/>'+
                '<strong>Range: </strong> <span style="color:yellow">[-9.375, 2[</span>'
            );
        }));

        it('should construct tooltip template when second data are NOT YET received', inject(function (StatisticsTooltipService) {
            //given
            var data  = {'formattedValue':'96ebf96df2','occurrences':1,'data':'96ebf96df2'};
            var secData;

            //when
            var template = StatisticsTooltipService.getTooltipTemplate(data, secData,  'formattedValue', 'Occurrences', 'occurrences', '');

            //then
            expect(template).toBe(
                '<strong>Occurrences matching your filter: </strong><span style="color:yellow">0 (0%)</span>'+
                '<br/><br/>'+
                '<strong>Occurrences in entire dataset:</strong> <span style="color:yellow">1 </span>'+
                '<br/><br/>'+
                '<strong>Record:</strong> <span style="color:yellow">96ebf96df2</span>');
        }));

        it('should construct tooltip template for vertical chart in case of min === max', inject(function (StatisticsTooltipService) {
            //given
            var data  = {'data':{'type':'number','min':-9.375,'max':-9.375},'occurrences':1};
            var secData = {'data':{'type':'number','min':-9.375,'max':-9.375},'filteredOccurrences':0};

            //when
            var template = StatisticsTooltipService.getTooltipTemplate(data, secData, 'data', 'Occurrences', 'occurrences', 'filteredOccurrences');

            //then
            expect(template).toBe(
                '<strong>Occurrences matching your filter: </strong><span style="color:yellow">0 (0.0%)</span>'+
                '<br/><br/>'+
                '<strong>Occurrences in entire dataset:</strong> <span style="color:yellow">1 </span>'+
                '<br/><br/>'+
                '<strong>Value: </strong> <span style="color:yellow">-9.375</span>'
            );
        }));

        it('should construct tooltip template for vertical chart in case of bar corresponding to the hovered range', inject(function (StatisticsTooltipService) {
            //given
            var data  = {'data':{'type':'number','min':-9.375,'max':2},'occurrences':0};
            var secData = {'data':{'type':'number','min':-9.375,'max':2},'filteredOccurrences':0};

            //when
            var template = StatisticsTooltipService.getTooltipTemplate(data, secData, 'data', 'Occurrences', 'occurrences', 'filteredOccurrences');

            //then
            expect(template).toBe(
                '<strong>Occurrences matching your filter: </strong><span style="color:yellow">0 (0%)</span>'+
                '<br/><br/>'+
                '<strong>Occurrences in entire dataset:</strong> <span style="color:yellow">0 </span>'+
                '<br/><br/>'+
                '<strong>Range: </strong> <span style="color:yellow">[-9.375, 2[</span>'
            );
        }));

    });

    describe('in case of NO filters', function () {
        it('should construct tooltip template for horizontal chart', inject(function (StatisticsTooltipService) {
            //given
            var data  = {'formattedValue':'96ebf96df2','occurrences':1,'data':'96ebf96df2'};
            var secData = {'formattedValue':'96ebf96df2','filteredOccurrences':1};
            stateMock.playground.filter.gridFilters = [];

            //when
            var template = StatisticsTooltipService.getTooltipTemplate(data, secData,  'formattedValue', 'Occurrences', 'occurrences', 'filteredOccurrences');

            //then
            expect(template).toBe(
                '<strong>Occurrences: </strong> <span style="color:yellow">1</span>'+
                '<br/><br/>'+
                '<strong>Record:</strong> <span style="color:yellow">96ebf96df2</span>');

        }));

        it('should construct tooltip template for vertical chart', inject(function (StatisticsTooltipService) {
            //given
            var data  = {'data':{'type':'number','min':-9.375,'max':2},'occurrences':1};
            var secData = {'data':{'type':'number','min':-9.375,'max':2},'filteredOccurrences':0};
            stateMock.playground.filter.gridFilters = [];

            //when
            var template = StatisticsTooltipService.getTooltipTemplate(data, secData, 'data', 'Occurrences', 'occurrences', 'filteredOccurrences');

            //then
            expect(template).toBe(
                '<strong>Occurrences: </strong> <span style="color:yellow">1</span>'+
                '<br/><br/>'+
                '<strong>Range: </strong> <span style="color:yellow">[-9.375, 2[</span>'
            );
        }));
    });
});