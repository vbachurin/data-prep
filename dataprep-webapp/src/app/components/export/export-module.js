import ExportCtrl from './export-controller';
import Export from './export-directive';

(() => {
    'use strict';

    angular.module('data-prep.export',
        [
            'talend.widget',
            'data-prep.services.utils',
            'data-prep.services.playground',
            'data-prep.services.export',
            'data-prep.services.state'
        ])
        .controller('ExportCtrl', ExportCtrl)
        .directive('export', Export);
})();