(function() {
    'use strict';

    angular.module('data-prep.navbar', [
        'ui.router',
        'data-prep.services.onboarding',
        'data-prep.services.dataset',
        'data-prep.services.utils',
        'data-prep.services.feedback'
    ]);
})();
