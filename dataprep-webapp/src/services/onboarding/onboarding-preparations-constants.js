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
            element: '#dataset-sampling-select',
            title: 'Dataset sampling',
            content: 'Click here to select a sample size.',
            position: 'right'
        }
    ];

    angular.module('data-prep.services.onboarding')
        .constant('preparationTour', preparationTour);
})();