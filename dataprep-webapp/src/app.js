'use strict';

angular.module('data-prep',
    [
        'data-prep-utils', //utils, constants
        'data-prep-dataset', //dataset getter, manipulation, etc

        'ui.router',
        'angularFileUpload' //file upload with progress support
    ])

    .config(['$compileProvider', 'disableDebug', function ($compileProvider, disableDebug) {
        if(disableDebug) {
            console.log('disable debug mode');
            $compileProvider.debugInfoEnabled(false);
        }
    }])

    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('nav', {
                abstract: true,
                templateUrl: "components/navbar/navbar.html"
            })
            .state('nav.home', {
                url: '/home',
                templateUrl: 'app/home/home.html',
                controller: 'HomeCtrl',
                controllerAs: 'homeCtrl',
                resolve: {datasets: function(DatasetService) {
                    return DatasetService.getDatasets();
                }}
            });

        $urlRouterProvider.otherwise('/home');
    })
;
