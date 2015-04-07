(function() {
    'use strict';

    angular.module('data-prep',
        [
            'ngSanitize',
            'ui.router', //more advanced router
            'data-prep.home',
            'data-prep.services.rest' //rest interceptors
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
                    templateUrl: 'components/navbar/navbar.html'
                })
                .state('nav.home', {
                    url: '/home',
                    templateUrl: 'components/home/home.html',
                    controller: 'HomeCtrl',
                    controllerAs: 'homeCtrl',
                    resolve: {
                        //waiting for translation resource to be load
                        //once the $translate promise is resolve, the router will perform the asked routing
                        translateLoaded : function($translate) {
                            return $translate('ALL_FOLDERS');
                        }
                    }
                })
                .state('nav.home.datasets', {
                    url: '/datasets',
                    views: {
                        'home-content': {
                            template: '<dataset-list></dataset-list>'
                        }
                    }
                })
                .state('nav.home.preparations', {
                    url: '/preparations?prepid',
                    views: {
                        'home-content': {
                            template: '<preparation-list></preparation-list>'
                        }
                    }
                });

            $urlRouterProvider.otherwise('/home');
        })

        //Language from browser
        .run(function ($window, $translate) {
            var language = ($window.navigator.language === 'fr') ? 'fr' : 'en';
            $translate.use(language);
        });
})();
