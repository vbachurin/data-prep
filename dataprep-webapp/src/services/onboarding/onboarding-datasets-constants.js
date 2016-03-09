(function() {
    'use strict';

    var datasetTour = [
        {
            element: '.no-js',
            title: '<center>Welcome to</br>Talend Data Preparation</center>',
            content: 'To quickly learn how to use it, click <b>Next</b>.</br>To access the online documentation, click <a href="https://help.talend.com/pages/viewpage.action?pageId=266307043&utm_medium=dpdesktop&utm_source=on_boarding" target="_blank">here</a>.',
            position: 'right'
        },
        {
            element: '#nav_home_datasets',
            title: '',
            content: 'Here you can browse through and manage the datasets you created.<br/>A dataset holds the raw data that can be used as raw material without affecting your original data.',
            position: 'right'
        },
        {
            element: '#dataset_0',
            title: '',
            content: 'Here you can find some ready-to-use datasets to get familiar with Talend Data Preparation.',
            position: 'bottom'
        },
        {
            element: '#help-import-local',
            title: '',
            content: 'Here you can create your own datasets from your own data.',
            position: 'right'
        },
        {
            element: '#onboarding-icon',
            title: '',
            content: 'Click here to play this tour again.',
            position: 'bottom'
        },
        {
            element: '#message-icon',
            title: '',
            content: 'Click here to send feedback to Talend.',
            position: 'bottom'
        },
        {
            element: '#online-help-icon',
            title: '',
            content: 'Click here to access the <a href="https://help.talend.com/pages/viewpage.action?pageId=266307043&utm_medium=dpdesktop&utm_source=on_boarding" target="_blank">online help</a>.',
            position: 'bottom'
        }
    ];

    angular.module('data-prep.services.onboarding')
        .constant('datasetTour', datasetTour);
})();
