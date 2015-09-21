(function() {
    'use strict';

    var recipeTour = [
        {
            element: '#help-preparation-name',
            title: 'Your new preparation',
            content: 'You can give a name to your brand new preparation. Usefull to find it later...',
            position: 'right',
            tooltipPosition: 'right'
        },
        {
            element: '#help-recipe > ul',
            title: 'Recipe, the steps of your preparation',
            content: 'This is the place where you can: <ul><li> - delete a step,</li><li> - see changes introduced by each step</li><li> - change an existing step</li></ul>',
            position: 'right'
        },
        {
            element: '#help-history',
            title: 'Undo/Redo',
            content: 'You can also undo/redo your last changes.',
            position: 'left'
        }
    ];

    angular.module('data-prep.services.onboarding')
        .constant('recipeTour', recipeTour);
})();
