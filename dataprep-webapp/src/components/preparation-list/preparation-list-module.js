(function() {
    'use strict';

    angular.module('data-prep.preparation-list', [
        'ui.router',
        'data-prep.services.preparation',
        'data-prep.services.playground',
        'talend.widget'
    ]);
})();