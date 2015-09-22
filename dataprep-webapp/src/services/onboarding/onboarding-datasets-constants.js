(function() {
    'use strict';

    var datasetTour = [
        {
            element: '.no-js',
            title: 'Welcome to Talend Data Preparation',
            content: 'To learn more about Talend Data Preparation, take this quick tour',
            position: 'right'
        },
        {
            element: '#help-import-local',
            title: 'Importing files',
            content: 'Click here to import a new local file (csv or Excel).',
            position: 'right'
        },
        {
            element: '#nav_home_datasets',
            title: 'Browsing datasets',
            content: 'Here you can browse through the datasets you imported.<br/>Datasets correspond to the files you want to work on.',
            position: 'right'
        },
        {
            element: '#nav_home_preparations',
            title: 'Browsing preparations',
            content: 'Here you can browse through the preparations you made for your datasets.<br/>Preparations are transformations you apply on a file to clean it.',
            position: 'right'
        },
        {
            element: '#dataset_0',
            title: 'Opening a dataset',
            content: 'Now open a dataset to get started with Data Prep and create your first preparation!',
            position: 'bottom'
        },
        {
            element: '#onboarding-icon',
            title: 'Help',
            content: 'Click here if you want to see this help again.',
            position: 'bottom'
        }
    ];

    angular.module('data-prep.services.onboarding')
        .constant('datasetTour', datasetTour);
})();
