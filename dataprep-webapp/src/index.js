'use strict';

angular.module('data-prep', ['ui.router'])
  .config(function ($stateProvider, $urlRouterProvider) {
    $stateProvider
      .state('home', {
        url: '/',
        templateUrl: 'home/home.html',
        controller: 'HomeCtrl'
      });

    $urlRouterProvider.otherwise('/');
  })
;
