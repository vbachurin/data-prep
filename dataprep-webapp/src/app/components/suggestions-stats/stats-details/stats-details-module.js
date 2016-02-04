import StatsDetailsCtrl from './stats-details-controller';
import StatsDetails from './stats-details-directive';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.stats-details
     * @description This module contains the controller and directives for the statistics tabs
     * @requires talend.widget
     * @requires data-prep.services.state
     * @requires data-prep.services.filter
     */
    angular.module('data-prep.stats-details',
        [
            'talend.widget',
            'data-prep.services.state',
            'data-prep.services.filter'
        ])
        .controller('StatsDetailsCtrl', StatsDetailsCtrl)
        .directive('statsDetails', StatsDetails);
})();