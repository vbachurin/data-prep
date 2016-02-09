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

    function FilterBar(state, FilterService) {
        return {
            restrict: 'E',
            templateUrl: 'components/filter/bar/filter-bar.html',
            scope: {},
            bindToController: true,
            controller: function (){
                this.filterService = FilterService;
                this.state = state;
            },
            controllerAs: 'filterBarCtrl'
        };
    }

    angular.module('data-prep.filter-bar')
        .directive('filterBar', FilterBar);
})();