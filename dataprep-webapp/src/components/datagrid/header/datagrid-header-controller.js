(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid-header.controller:DatagridHeaderCtrl
     * @description Dataset Column Header controller.
     * @requires data-prep.services.transformation.service:TransformationService
     * @requires data-prep.services.utils.service:ConverterService
     * @requires data-prep.services.filter.service:FilterService
     */
    function DatagridHeaderCtrl(TransformationService, ConverterService, FilterService) {
        var vm = this;

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
            // They can be different if less than MIN_PERCENT are valid/invalid/empty, to assure a min width of each bar. To be usable by the user.
            column.quality.emptyPercent = column.quality.empty <= 0 ? 0 : Math.ceil(column.quality.empty * 100 / column.total);
            column.quality.emptyPercentWidth = column.quality.empty <= 0 ? 0 : Math.max(column.quality.emptyPercent, MIN_PERCENT);

            column.quality.invalidPercent = column.quality.invalid <= 0 ? 0 : Math.ceil(column.quality.invalid * 100 / column.total);
            column.quality.invalidPercentWidth = column.quality.invalid <= 0 ? 0 : Math.max(column.quality.invalidPercent, MIN_PERCENT);

            column.quality.validPercent = 100 - column.quality.emptyPercent - column.quality.invalidPercent;
            column.quality.validPercentWidth = 100 - column.quality.emptyPercentWidth - column.quality.invalidPercentWidth;
        };

        /**
         * @ngdoc method
         * @name insertDividers
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @param {object[]} menuGroups - the menus grouped by category
         * @description [PRIVATE] Insert a divider between each group of menus
         * @returns {object[]} - each element is a group of menu or a divider
         */
        var insertDividers = function(menuGroups) {
            var divider = {isDivider : true};
            var result = [];
            _.forEach(menuGroups, function(group) {
                if(result.length) {
                    result.push(divider);
                }

                result.push(group);
            });

            return result;
        };

        /**
         * @ngdoc method
         * @name groupMenus
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @param {object[]} menus - the unordered menus
         * @description [PRIVATE] Group all menus by category and insert dividers between each group
         * @returns {object[]} - each element is a menu item or a divider
         */
        var groupMenus = function(menus) {
            var groups = _.groupBy(menus, function(menuItem) { return menuItem.category; });
            var groupsAndDividers = insertDividers(groups);
            return _.flatten(groupsAndDividers);
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

                TransformationService.getTransformations(vm.column)
                    .then(function(menus) {
                        vm.transformations = groupMenus(menus);
                    })
                    .catch(function() {
                        vm.transformationsRetrieveError = true;
                    })
                    .finally(function() {
                        vm.initTransformationsInProgress = false;
                    });
            }
        };

        /**
         * @ngdoc method
         * @name setColumnSimplifiedType
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description set the column simplified, more user friendly, type.
         */
        var setColumnSimplifiedType = function () {
            vm.column.simplifiedType = ConverterService.simplifyType(vm.column.type);
        };


        /**
         * @ngdoc method
         * @name filterInvalidRecords
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description Create a filter for invalid records on the given column.
         * @param {object} column - the column to filter
         */
        vm.filterInvalidRecords = function(column) {
            FilterService.addFilter('invalid_records', column.id, column.name, {values: column.quality.invalidValues});
        };

        /**
         * @ngdoc method
         * @name filterEmptyRecords
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @description Create a filter for empty records on the given column.
         * @param {object} column - the column to filter
         */
        vm.filterEmptyRecords = function(column) {
            FilterService.addFilter('empty_records', column.id, column.name, {});
        };

        setColumnSimplifiedType();
    }

    angular.module('data-prep.datagrid-header')
        .controller('DatagridHeaderCtrl', DatagridHeaderCtrl);
})();
