import LookupCtrl from './lookup-controller';
import Lookup from './lookup-directive';

import LookupDatagridColumnService from './grid/services/lookup-datagrid-column-service';
import LookupDatagridGridService from './grid/services/lookup-datagrid-grid-service';
import LookupDatagridStyleService from './grid/services/lookup-datagrid-style-service';
import LookupDatagridTooltipService from './grid/services/lookup-datagrid-tooltip-service';
import LookupDatagridCtrl from './grid/lookup-datagrid-controller';
import LookupDatagrid from './grid/lookup-datagrid-directive';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.lookup
     * @description This module contains the dataset lookup
     * @requires talend.widget
     * @requires data-prep.lookup-datagrid-header
     * @requires data-prep.services.dataset
     * @requires data-prep.services.lookup
     * @requires data-prep.services.state
     * @requires data-prep.services.statistics
     * @requires data-prep.services.transformation
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.lookup',
        [
            'talend.widget',
            'data-prep.lookup-datagrid-header',
            'data-prep.services.dataset',
            'data-prep.services.lookup',
            'data-prep.services.state',
            'data-prep.services.statistics',
            'data-prep.services.transformation',
            'data-prep.services.utils'
        ])
        .controller('LookupCtrl', LookupCtrl)
        .directive('lookup', Lookup)

        .service('LookupDatagridColumnService', LookupDatagridColumnService)
        .service('LookupDatagridGridService', LookupDatagridGridService)
        .service('LookupDatagridStyleService', LookupDatagridStyleService)
        .service('LookupDatagridTooltipService', LookupDatagridTooltipService)
        .controller('LookupDatagridCtrl', LookupDatagridCtrl)
        .directive('lookupDatagrid', LookupDatagrid);
})();