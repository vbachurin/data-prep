'use strict';

angular.module('data-prep', ['ui.router', 'angularFileUpload'])
    .config(function ($stateProvider, $urlRouterProvider) {
        $stateProvider
            .state('home', {
                url: '/',
                templateUrl: 'home/home.html',
                controller: 'HomeCtrl',
                controllerAs: 'homeCtrl',
                resolve: {datasets: function(DatasetService) {
                    return DatasetService.getDatasets();
                }}
            });

        $urlRouterProvider.otherwise('/');
    })
;
