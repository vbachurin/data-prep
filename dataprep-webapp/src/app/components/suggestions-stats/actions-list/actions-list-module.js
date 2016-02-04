import ActionsListCtrl from './actions-list-controller';
import ActionsList from './actions-list-directive';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.actions-list
     * @description This module display a transformation actions list
     * @requires talend.widget
     * @requires data-prep.services.transformation
     * @requires data-prep.services.state
     */
    angular.module('data-prep.actions-list',
        [
            'talend.widget',
            'data-prep.services.transformation',
            'data-prep.services.state'
        ])
        .controller('ActionsListCtrl', ActionsListCtrl)
        .directive('actionsList', ActionsList);
})();