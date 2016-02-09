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

    /**
     * @ngdoc directive
     * @name data-prep.preparation-list.directive:PreparationList
     * @description This directive display the preparations list.
     * @restrict E
     */
    function PreparationList() {
        return {
            restrict: 'E',
            templateUrl: 'components/preparation-list/preparation-list.html',
            bindToController: true,
            controllerAs: 'preparationListCtrl',
            controller: 'PreparationListCtrl'
        };
    }

    angular.module('data-prep.preparation-list')
        .directive('preparationList', PreparationList);
})();