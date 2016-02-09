/*  ============================================================================

  Copyright (C) 2006-2016 Talend Inc. - www.talend.com

  This source code is available under agreement available at
  https://github.com/Talend/data-prep/blob/master/LICENSE

  You should have received a copy of the agreement
  along with this program; if not, write to Talend SA
  9 rue Pages 92150 Suresnes, France

  ============================================================================*/

(function () {
    'use strict';

    /**
     * @ngdoc directive
     * @name data-prep.dataset-list.directive:DatasetList
     * @description This directive display the dataset list from {@link
     *     data-prep.services.dataset.service:DatasetService DatasetService}
     * @requires data-prep.dataset-list.controller:DatasetListCtrl
     * @restrict E
     */
    function DatasetList () {
        return {
            restrict: 'E',
            templateUrl: 'components/dataset/list/dataset-list.html',
            bindToController: true,
            controllerAs: 'datasetListCtrl',
            controller: 'DatasetListCtrl',
            link: function (scope, iElement, iAttrs, ctrl) {
                ctrl.focusOnNameInput = function focusOnNameInput () {
                    angular.element('.clone-name input[type="text"]').eq(0)[0].focus();
                };
            }
        };
    }

    angular.module('data-prep.dataset-list')
        .directive('datasetList', DatasetList);
})();