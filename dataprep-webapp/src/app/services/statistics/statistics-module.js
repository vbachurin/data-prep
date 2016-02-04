import StatisticsTooltipService from './statistics-tooltip-service';
import StatisticsRestService from './rest/statistics-rest-service';
import StatisticsService from './statistics-service';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.statistics
     * @description This module contains the statistics service
     * @requires data-prep.services.playground
     * @requires data-prep.services.state
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.services.statistics',
        [
            'data-prep.services.playground',
            'data-prep.services.state',
            'data-prep.services.utils'
        ])
        .service('StatisticsTooltipService', StatisticsTooltipService)
        .service('StatisticsRestService', StatisticsRestService)
        .service('StatisticsService', StatisticsService);
})();