import TalendEditor from './editor/talend-editor';
import DatagridColumnService from './services/datagrid-column-service';
import DatagridExternalService from './services/datagrid-external-service';
import DatagridGridService from './services/datagrid-grid-service';
import DatagridSizeService from './services/datagrid-size-service';
import DatagridStyleService from './services/datagrid-style-service';
import DatagridTooltipService from './services/datagrid-tooltip-service';
import DatagridCtrl from './datagrid-controller';
import Datagrid from './datagrid-directive';

(function () {
    'use strict';

    Slick.Editors.TalendEditor = TalendEditor;

    /**
     * @ngdoc object
     * @name data-prep.datagrid
     * @description This module contains the controller and directives for the datagrid
     * @requires data-prep.datagrid-header
     * @requires 'data-prep.services.statistics'
     * @requires 'data-prep.services.state'
     * @requires 'data-prep.services.utils'
     */
    angular.module('data-prep.datagrid',
        [
            'data-prep.datagrid-header',
            'data-prep.services.statistics',
            'data-prep.services.state',
            'data-prep.services.utils'
        ])
        .service('DatagridColumnService', DatagridColumnService)
        .service('DatagridExternalService', DatagridExternalService)
        .service('DatagridGridService', DatagridGridService)
        .service('DatagridSizeService', DatagridSizeService)
        .service('DatagridStyleService', DatagridStyleService)
        .service('DatagridTooltipService', DatagridTooltipService)
        .controller('DatagridCtrl', DatagridCtrl)
        .directive('datagrid', Datagrid);
})();