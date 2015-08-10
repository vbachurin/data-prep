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

            service.numericColumns = DatagridService.getNumberColumns(service.columnSelected.id);

            //If same column selected, update it
            if(service.columnAggregationSelected && (service.columnSelected.id === service.columnAggregationSelected.id)) {
                var cols = DatagridService.getNumberColumns(null);
                var colToBeUpdated = _.where(cols, {id: service.columnAggregationSelected.id});
                if(colToBeUpdated.length === 1){
                    service.columnAggregationSelected = colToBeUpdated[0];
                    service.updateAggregationsChanges(service.columnAggregationSelected, service.calculationAggregationSelected);
                }
            } else {
                service.updateAggregationsChanges(null, null); //If another column selected, reset "aggregation" dropdown
            }
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
                service.aggregationSelected = $filter('translate')(service.calculationAggregationSelected.name)+' ('+$filter('translate')(service.columnAggregationSelected.name)+') ';
            } else {
                service.aggregationSelected = $filter('translate')('LINE_COUNT');
            }
        }

    }

    angular.module('data-prep.suggestions-stats')
        .service('SuggestionsStatsAggregationsService', SuggestionsStatsAggregationsService);
})();