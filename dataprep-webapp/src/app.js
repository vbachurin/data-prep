(function() {
    'use strict';

    angular.module('data-prep',
        [
            'data-prep-utils', //utils components: constants, filters, ...
            'data-prep-dataset', //dataset getter, manipulation, etc

            'talend.widget', //components widget built on bourbon (modal, dropdown, ...)

            'ui.router', //more advanced router
            'toaster', //toaster popup
            'pascalprecht.translate' //internationalization
        ])

        //Performance config
        .config(['$httpProvider', '$compileProvider', 'disableDebug', function ($httpProvider, $compileProvider, disableDebug) {
            $httpProvider.useApplyAsync(true);

            if(disableDebug) {
                $compileProvider.debugInfoEnabled(false);
            }
        }])

        //Translate config
        .config(['$translateProvider', function ($translateProvider) {
            $translateProvider.useStaticFilesLoader({
                prefix: 'i18n/',
                suffix: '.json'
            });

            $translateProvider.preferredLanguage('en');
        }])

        //Router config
        .config(function ($stateProvider, $urlRouterProvider) {
            $stateProvider
                .state('nav', {
                    abstract: true,
                    templateUrl: 'app/navbar/navbar.html'
                })
                .state('nav.home', {
                    url: '/home',
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeCtrl',
                    controllerAs: 'homeCtrl'
                });

            $urlRouterProvider.otherwise('/home');
        })

        //Language from browser
        .run(function ($window, $translate) {
            var language = ($window.navigator.language === 'fr') ? 'fr' : 'en';
            $translate.use(language);
        });
})();
