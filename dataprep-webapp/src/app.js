(function() {
    'use strict';

    angular.module('data-prep',
        [
            'data-prep-utils', //utils components: constants, filters, ...
            'data-prep-dataset', //dataset getter, manipulation, etc

            'ui.router',
            'angularFileUpload' //file upload with progress support
        ])

        .config(function ($stateProvider, $urlRouterProvider) {
            $stateProvider
                .state('nav', {
                    abstract: true,
                    templateUrl: 'components/navbar/navbar.html'
                })
                .state('nav.home', {
                    url: '/home',
                    templateUrl: 'app/home/home.html',
                    controller: 'HomeCtrl',
                    controllerAs: 'homeCtrl'
                });

            $urlRouterProvider.otherwise('/home');
        });
})();
