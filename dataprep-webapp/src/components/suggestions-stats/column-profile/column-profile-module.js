(function() {
    'use strict';

    angular.module('data-prep.column-profile', [
        'highcharts-ng',
        'talend.widget',
        'data-prep.services.dataset',
        'data-prep.services.statistics',
        'data-prep.services.playground',
        'data-prep.services.state'
    ]);
})();