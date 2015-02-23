(function () {
    'use strict';

    /**
     * DatasetColumnHeader directive controller
     * @param TransformationService
     */
    function DatasetColumnHeaderCtrl(TransformationService) {
        var vm = this;

        /**
         * Compute quality bars percentage
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
         * Insert a divider between each group of menus
         * @param menuGroups - the menus grouped by category
         * @returns {Array}
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
         * Group all menus by category and insert dividers between each group
         * @param menus - the menu list
         * @returns {Array}
         */
        var groupMenus = function(menus) {
            var groups = _.groupBy(menus, function(menuItem) { return menuItem.category; });
            var groupsAndDividers = insertDividers(groups);

            return _.flatten(groupsAndDividers);
        };

        /**
         * Insert adapted html input type in each parameter in the menu
         * @param menu - the menu with parameters to adapt
         */
        var insertType = function(menu) {
            if(menu.parameters) {
                _.forEach(menu.parameters, function(param) {
                    switch (param.type) {
                        case 'numeric':
                        case 'integer':
                        case 'double':
                        case 'float':
                            param.inputType = 'number';
                            break;
                        default:
                            param.inputType = 'text';
                            break;
                    }
                });
            }
        };

        /**
         * Adapt each parameter type to HTML input type
         * @param menus - the menus with parameters to adapt
         * @returns {*}
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
         * Remove 'column_name' parameters (automatically sent and not asked to user), and clean empty arrays
         * @param menus - the menus to clean
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
         * Get transformations from rest call
         */
        vm.initTransformations = function () {
            if (!vm.transformations && !vm.initTransformationsInProgress) {
                vm.initTransformationsInProgress = true;

                TransformationService.getTransformations(vm.metadata.id, vm.column.id)
                    .then(function(response) {
                        var menus = cleanParamsAndItems(response.data);
                        menus = adaptInputTypes(menus);
                        vm.transformations = groupMenus(menus);
                    })
                    .finally(function() {
                        vm.initTransformationsInProgress = false;
                    });
            }
        };
    }

    angular.module('data-prep-dataset')
        .controller('DatasetColumnHeaderCtrl', DatasetColumnHeaderCtrl);
})();
