/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function() {
    'use strict';

    function Home() {
        return {
            restrict: 'E',
            templateUrl: 'components/home/home.html',
            scope: {},
            bindToController: true,
            controller: 'HomeCtrl',
            controllerAs: 'homeCtrl'
        };
    }

    angular.module('data-prep.home')
        .directive('home', Home);
})();