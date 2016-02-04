import DataPrepApp from './app-directive';

(() => {
    'use strict';

    angular.module('data-prep.app',
        [
            'data-prep.navbar',
            'data-prep.home',
            'data-prep.easter-eggs',
            'data-prep.feedback'
        ])
        .directive('dataprepApp', DataPrepApp);
})();