(function () {
    'use strict';
    /**
     * @ngdoc service
     * @name data-prep.services.statistics.service:StatisticsTooltipService
     * @description prepares the template of the chart's tooltip
     * @requires data-prep.services.state.constant:state
     */
    function StatisticsTooltipService(state) {
        var service = {
          getTooltipTemplate: getTooltipTemplate
        };

        return service;


        /**************************************************************************************************************/
        /************************************************* Chart Tooltip Content **************************************/
        /**************************************************************************************************************/

        /**
         * @name getPercentage
         * @description calculates the percentage
         * @type {Number} numer numerator
         * @type {Number} denum denumerator
         * @returns {string} the percentage label
         */
        function getPercentage(numer, denum) {
            if(denum){
                var quotient = (numer / denum) * 100;
                //toFixed(1) and not toFixed(0) because (19354/19430 * 100).toFixed(0) === '100'
                return '(' + quotient.toFixed(1) + '%)';
            }
            else{
                return '(0%)';
            }
        }

        /**
         * @ngdoc property
         * @name getTooltipTemplate
         * @propertyOf data-prep.services.statistics:StatisticsTooltipService
         * @description creates the html tooltip template
         * @type {Object} data the hovered object
         * @type {Object} secData secondary data object corresponding to the nth hovered item
         * @returns {String} compiled template
         */
        function getTooltipTemplate(data, secData, keyField, keyLabel, primaryValField, secValField){
            var primaryValue = data[primaryValField];
            var range = data[keyField];
            var uniqueValue = range.min === range.max;
            var title, value;
            if(range.min === undefined){//horizontal chart
                title = 'Record:';
                value = range;
            }
            else {//vertical chart
                title = (uniqueValue ? 'Value: ' : 'Range: ');
                value = range.label || (uniqueValue ? range.min : '[' + range.min + ', ' + range.max + '[');
            }

            var secondaryValue, percentage;
            if(secData){
                secondaryValue = secData[secValField];
                percentage = getPercentage(secondaryValue, primaryValue);
            }
            else{
                secondaryValue = 0;
                percentage = '(0%)';
            }

            if(state.playground.filter.gridFilters.length){
                return '<strong>'+ keyLabel + ' matching your filter: </strong>' +
                    '<span style="color:yellow">' + secondaryValue  + ' ' + percentage + '</span>' +
                    '<br/>' +
                    '<br/>' +
                    '<strong>'+ keyLabel + ' in entire dataset:</strong> <span style="color:yellow">' + primaryValue + ' </span>' +
                    '<br/>' +
                    '<br/>' +
                    '<strong>'+ title +'</strong> ' +
                    '<span style="color:yellow">'+ value +'</span>';
            }
            else{
                return '<strong>'+ keyLabel + ': </strong> <span style="color:yellow">'+ primaryValue +'</span>' +
                    '<br/>' +
                    '<br/>' +
                    '<strong>'+ title +'</strong> ' +
                    '<span style="color:yellow">'+ value +'</span>';
            }
        }
    }
    angular.module('data-prep.services.statistics')
        .service('StatisticsTooltipService', StatisticsTooltipService);
})();