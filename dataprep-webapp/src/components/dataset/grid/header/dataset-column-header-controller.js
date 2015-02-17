(function () {
    'use strict';

    /**
     * DatasetColumnHeader directive controller
     * @param $rootScope
     * @param TransformationService
     */
    function DatasetColumnHeaderCtrl($rootScope, TransformationService, DatasetGridService) {
        var vm = this;

        /**
         * Compute quality bars percentage
         */
        vm.refreshQualityBar = function() {
            var MIN_PERCENT = 10;
            var column = vm.column;

            column.total = column.quality.valid + column.quality.empty + column.quality.invalid;

            // *_percent is the real % of empty/valid/invalid records, while *_percent_width is the width % of the bar.
            // They can be differents if less than MIN_PERCENT are valid/invalid/empty, to assure a min width of each bar. To be usable by the user.
            // TODO remove completely one bar if absolute zero records match (ie: if 0 invalid records, do not display invalid bar)
            column.quality.emptyPercent = Math.ceil(column.quality.empty * 100 / column.total);
            column.quality.emptyPercentWidth = Math.max(column.quality.emptyPercent, MIN_PERCENT);

            column.quality.invalidPercent = Math.ceil(column.quality.invalid * 100 / column.total);
            column.quality.invalidPercentWidth = Math.max(column.quality.invalidPercent, MIN_PERCENT);

            column.quality.validPercent = 100 - column.quality.emptyPercent - column.quality.invalidPercent;
            column.quality.validPercentWidth = 100 - column.quality.emptyPercentWidth - column.quality.invalidPercentWidth;
        };

        /**
         * Perform a transformation on column
         * @param action - the
         * action name
         */
        vm.transform = function (action) {
            $rootScope.$emit('talend.loading.start');
            TransformationService.transform(vm.metadata.id, action, {'column_name': vm.column.id})
                .then(function (response) {
                    DatasetGridService.updateRecords(response.data.records);
                })
                .finally(function () {
                    $rootScope.$emit('talend.loading.stop');
                });
        };
    }

    angular.module('data-prep-dataset')
        .controller('DatasetColumnHeaderCtrl', DatasetColumnHeaderCtrl);
})();
