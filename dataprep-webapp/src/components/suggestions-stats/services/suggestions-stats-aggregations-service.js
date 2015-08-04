(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.suggestions-stats.service:SuggestionsStatsAggregationsService
     * @description This Service manages the aggregations
     */
    function SuggestionsStatsAggregationsService(DatagridService, $filter) {


        var service = {
            aggregationSelected: null,
            columnAggregationSelected: null,
            calculationAggregationSelected: null,
            numericColumns: null,
            updateAggregations: updateAggregations,
            updateAggregationsChanges: updateAggregationsChanges
        };

        return service;


        /**
         * @ngdoc method
         * @name updateAggregations
         * @methodOf data-prep.suggestions-stats.service:SuggestionsStatsAggregationsService
         * @param {string} column The selected column
         * @description update aggregations list (excluding selected column)and charts
         */
        function updateAggregations(column) {

            service.numericColumns = DatagridService.getNumberColumns(column.id);

            if(service.columnAggregationSelected) {
                var cols = DatagridService.getNumberColumns(null);
                var colToBeUpdated = _.where(cols, {id: service.columnAggregationSelected.id});

                if(colToBeUpdated.length === 1){
                    service.columnAggregationSelected = colToBeUpdated[0];
                    updateAggregationsChanges(service.columnAggregationSelected, service.calculationAggregationSelected);
                }
            } else {
                updateAggregationsChanges(null, null);
            }
        }

        function updateAggregationsChanges(column, calculation) {

            service.calculationAggregationSelected = calculation;
            service.columnAggregationSelected = column;

            if (column && calculation) {
                service.aggregationSelected = $filter('translate')(service.calculationAggregationSelected.name)+' ('+$filter('translate')(service.columnAggregationSelected.name)+') ';
            } else {
                service.aggregationSelected = $filter('translate')('LINE_COUNT');
            }
        }

    }

    angular.module('data-prep.suggestions-stats')
        .service('SuggestionsStatsAggregationsService', SuggestionsStatsAggregationsService);
})();