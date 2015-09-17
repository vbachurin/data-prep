(function() {
    'use strict';

    var recipeTour = [
        {
            element: '#prepNameInput',
            title: 'Your new preparation',
            content: 'You can give a name to your brand new preparation.',
            position: 'right'
        },
        {
            element: '#help-recipe',
            title: 'Recipe',
            content: 'You can delete a step, see changes introduced by each step or change an existing step.',
            position: 'right'
        },
        {
            element: '#help-history',
            title: 'Undo/Redo',
            content: 'You can also undo/redo your last changes.',
            position: 'bottom'
        }
    ];

    angular.module('data-prep.services.onboarding')
        .constant('recipeTour', recipeTour);
})();
