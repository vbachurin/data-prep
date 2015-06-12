(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid-header.controller:DatagridHeaderCtrl
     * @description Dataset Column Header controller.
     * @requires data-prep.services.transformation.service:TransformationService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function DatagridHeaderCtrl(TransformationCacheService, ConverterService) {
        var COLUMN_CATEGORY = 'columns';
        var vm = this;
        vm.column.simplifiedType = ConverterService.simplifyType(vm.column.type);

        /**
         * @ngdoc method
         * @name refreshQualityBar
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description [PRIVATE] Compute quality bars percentage
         */
        vm.refreshQualityBar = function () {
            var MIN_PERCENT = 10;
            var column = vm.column;

            column.total = column.quality.valid + column.quality.empty + column.quality.invalid;

            // *_percent is the real % of empty/valid/invalid records, while *_percent_width is the width % of the bar.
            // They can be differents if less than MIN_PERCENT are valid/invalid/empty, to assure a min width of each bar. To be usable by the user.
            column.quality.emptyPercent = column.quality.empty <= 0 ? 0 : Math.ceil(column.quality.empty * 100 / column.total);
            column.quality.emptyPercentWidth = column.quality.empty <= 0 ? 0 : Math.max(column.quality.emptyPercent, MIN_PERCENT);

            column.quality.invalidPercent = column.quality.invalid <= 0 ? 0 : Math.ceil(column.quality.invalid * 100 / column.total);
            column.quality.invalidPercentWidth = column.quality.invalid <= 0 ? 0 : Math.max(column.quality.invalidPercent, MIN_PERCENT);

            column.quality.validPercent = 100 - column.quality.emptyPercent - column.quality.invalidPercent;
            column.quality.validPercentWidth = 100 - column.quality.emptyPercentWidth - column.quality.invalidPercentWidth;
        };

        /**
         * @ngdoc method
         * @name initTransformations
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description Get transformations from REST call
         */
        vm.initTransformations = function () {
            if (!vm.transformations && !vm.initTransformationsInProgress) {
                vm.transformationsRetrieveError = false;
                vm.initTransformationsInProgress = true;

                TransformationCacheService.getTransformations(vm.column)
                    .then(function(menus) {
                        vm.transformations = _.filter(menus, function(menu) {
                            return menu.category === COLUMN_CATEGORY;
                        });
                    })
                    .catch(function() {
                        vm.transformationsRetrieveError = true;
                    })
                    .finally(function() {
                        vm.initTransformationsInProgress = false;
                    });
            }
        };
    }

    angular.module('data-prep.datagrid-header')
        .controller('DatagridHeaderCtrl', DatagridHeaderCtrl);
})();
