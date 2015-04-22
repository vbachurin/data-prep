(function () {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.datagrid-header.controller:DatagridHeaderCtrl
     * @description Dataset Column Header controller.
     * @requires data-prep.services.transformation.service:TransformationService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function DatagridHeaderCtrl(TransformationService, ConverterService) {
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
         * @name insertType
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @param {object[]} menu - the menu item with parameters to adapt
         * @description [PRIVATE] Insert adapted html input type in each parameter in the menu
         */
        var insertType = function(menu) {
            if(menu.parameters) {
                _.forEach(menu.parameters, function(param) {
                    param.inputType = ConverterService.toInputType(param.type);
                });
            }
        };

        /**
         * @ngdoc method
         * @name adaptInputTypes
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @param {object[]} menus - the menus with parameters to adapt
         * @description [PRIVATE] Adapt each parameter type to HTML input type
         */
        var adaptInputTypes = function(menus) {
            _.forEach(menus, function(menu) {
                insertType(menu);

                _.forEach(menu.items, function(item) {
                    _.forEach(item.values, function(choiceValue) {
                        insertType(choiceValue);
                    });
                });
            });

            return menus;
        };

        /**
         * @ngdoc method
         * @name cleanParamsAndItems
         * @methodOf data-prep.datagrid-header.controller:DatagridHeaderCtrl
         * @param {object[]} menus - the menus to clean
         * @description [PRIVATE] Remove 'column_name' parameters (automatically sent), and clean empty arrays (choices and params)
         */
        var cleanParamsAndItems = function(menus) {
            return _.forEach(menus, function(menu) {
                //params
                var filteredParameters = _.filter(menu.parameters, function(param) {
                    return param.name !== 'column_name';
                });
                menu.parameters = filteredParameters.length ? filteredParameters : null;

                //items
                menu.items = menu.items.length ? menu.items : null;
            });
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

                TransformationService.getTransformations(vm.metadata.id, vm.column.id)
                    .then(function(response) {
                        var menus = cleanParamsAndItems(response.data);
                        menus = adaptInputTypes(menus);
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
    }

    angular.module('data-prep.datagrid-header')
        .controller('DatagridHeaderCtrl', DatagridHeaderCtrl);
})();
