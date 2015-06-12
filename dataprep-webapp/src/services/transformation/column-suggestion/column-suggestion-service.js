(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:ColumnSuggestionService
     * @description Transformation Column suggestion service. This service provide the current column suggestions
     * @requires data-prep.services.transformation.service:TransformationCacheService
     */
    function ColumnSuggestionService(TransformationCacheService) {
        var COLUMN_CATEGORY = 'columns';
        var self = this;
        self.currentColumn = null;
        self.transformations = null;

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
            //var groupsAndDividers = insertDividers(groups);
            //return _.flatten(groupsAndDividers);

            return groups;
        };

        this.setColumn = function setColumn(column) {
            if(column === self.currentColumn) {
                return;
            }

            self.currentColumn = column;
            TransformationCacheService.getTransformations(column)
                .then(function(transformations) {
                    if(self.currentColumn === column) {
                        var filteredTransfos = _.filter(transformations, function(transfo) {
                            return transfo.category !== COLUMN_CATEGORY;
                        });
                        self.transformations = groupMenus(filteredTransfos);
                    }
                });
        };

        this.reset = function reset() {
            self.currentColumn = null;
            self.transformations = null;
        };
    }

    angular.module('data-prep.services.transformation')
        .service('ColumnSuggestionService', ColumnSuggestionService);
})();