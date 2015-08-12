(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.suggestions-stats.service:SuggestionsStatsAggregationsService
     * @description This Service manages the aggregations
     */
    function SuggestionsStatsAggregationsService(DatagridService, $filter) {


        var service = {
            columnSelected : null,
            aggregationSelected: null,
            columnAggregationSelected: null,
            calculationAggregationSelected: null,
            numericColumns: null,
            barChartValueKey: '',
            barChartValueKeyLabel: '',
            updateAggregations: updateAggregations,
            updateAggregationsChanges: updateAggregationsChanges
        };

        return service;


        /**
         * @ngdoc method
         * @name updateAggregations
         * @methodOf data-prep.suggestions-stats.service:SuggestionsStatsAggregationsService
         * @param {object} column The selected column
         * @description update aggregation (excluding selected column)and charts triggered from Playground
         */
        function updateAggregations(column) {

            service.columnSelected = column;

            service.numericColumns = DatagridService.getNumberColumns(service.columnSelected.id); //get updated list

            service.updateAggregationsChanges(null, null); //reset "aggregation" dropdown
        }

        /**
         * @ngdoc method
         * @name updateAggregationsChanges
         * @methodOf data-prep.suggestions-stats.service:SuggestionsStatsAggregationsService
         * @param {object} column The selected aggregation column
         * @param {object} calculation The selected aggregation operation
         * @description update aggregation triggered from Column-Profile
         */
        function updateAggregationsChanges(column, calculation) {

            service.calculationAggregationSelected = calculation;
            service.columnAggregationSelected = column;

            if (column && calculation) {

                service.barChartValueKey = service.calculationAggregationSelected.id;
                service.barChartValueKeyLabel = $filter('translate')(service.calculationAggregationSelected.name);
                service.aggregationSelected = $filter('translate')(service.calculationAggregationSelected.name)+' ('+$filter('translate')(service.columnAggregationSelected.name)+') ';
            } else {

                service.barChartValueKey = '';
                service.barChartValueKeyLabel = '';
                service.aggregationSelected = $filter('translate')('LINE_COUNT');
            }
        }

    }

    angular.module('data-prep.suggestions-stats')
        .service('SuggestionsStatsAggregationsService', SuggestionsStatsAggregationsService);
})();