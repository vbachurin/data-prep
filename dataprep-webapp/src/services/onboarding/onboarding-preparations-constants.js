(function() {
    'use strict';

    var preparationTour = [
        {
            element: '.no-js',
            title: 'Welcome to Talend Data Preparation!',
            content: 'To know more about Talend Data Preparation, take this quick tour!',
            position: 'right'
        },
        {
            element: '#help-import-local',
            title: 'Importing files',
            content: 'Click here to import a new local file (csv or Excel).',
            position: 'right'
        }
    ];

    angular.module('data-prep.services.onboarding')
        .constant('preparationTour', preparationTour);
})();