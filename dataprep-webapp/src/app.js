(function() {
    'use strict';

    angular.module('data-prep',
        [
            'ngSanitize',
            'ui.router', //more advanced router
            'data-prep.navbar',
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
            $translateProvider.useSanitizeValueStrategy(null);
        }])

        //Router config
        .config(function ($stateProvider, $urlRouterProvider) {
            $stateProvider
                .state('nav', {
                    abstract: true,
                    controller: 'NavbarCtrl',
                    controllerAs: 'navbarCtrl',
                    templateUrl: 'components/navbar/navbar.html'
                })
                .state('nav.home', {
                    abstract: true,
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
                    url: '/datasets?datasetid',
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
                })
                .state('nav.home.recentfiles', {
                    url: '/recentfiles',
                    views: {
                        'home-content': {
                            template: '<div style="padding-top:100px; text-align: center;font-size: xx-large;">Coming Soon ...</div>'
                        }
                    }
                })
                .state('nav.home.favorites', {
                    url: '/favorites',
                    views: {
                        'home-content': {
                            template: '<div style="padding-top:100px; text-align: center;font-size: xx-large;">Coming Soon ...</div>'
                        }
                    }
                });

            $urlRouterProvider.otherwise('/home/datasets');
        })

        //Language from browser
        .run(function ($window, $translate) {
            var language = ($window.navigator.language === 'fr') ? 'fr' : 'en';
            $translate.use(language);
        });
})();
