(function() {
    'use strict';

    var playgroundTour = [
        {
            element: '#datagrid .slick-header-columns-right > .slick-header-column',
            title: 'Columns',
            content: 'Select a column to discover which actions you can do on this context',
            position: 'right'
        },
        {
            element: '#help-suggestions > .actions-suggestions',
            title: 'Available actions',
            content: 'Simply click on one of those to perform it. Easy, you will be able to undo or  change it after.',
            position: 'left'
        },
        {
            element: '#help-stats > .actions-suggestions',
            title: 'Stats',
            content: 'Here are some stats to help you to discover your data.',
            position: 'left'
        }
    ];

    angular.module('data-prep.services.onboarding')
        .constant('playgroundTour', playgroundTour);
})();

