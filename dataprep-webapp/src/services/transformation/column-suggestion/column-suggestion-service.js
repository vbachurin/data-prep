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

        /**
         * @ngdoc property
         * @name currentColumn
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The currently selected column
         * @type {Object}
         */
        self.currentColumn = null;

        /**
         * @ngdoc property
         * @name transformations
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The currently selected column transformations
         * @type {Object}
         */
        self.transformations = null;

        /**
         * @ngdoc method
         * @name filterAndGroup
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object[]} transfos The transformations list
         * @description [PRIVATE] Keep only the non 'columns' category and group them by category
         * @returns {object} An object containing {key: value} = {category: [transformations]}
         */
        var filterAndGroup = function filterAndGroup(transfos) {

            var sortedAndUngroupedTrans = _.chain(transfos)
                .filter(function(transfo) {
                    return transfo.category !== COLUMN_CATEGORY;
                })
                .sortBy(function(action) {
                    return action.label.toLowerCase();
                })
                .value();

            return sortedAndUngroupedTrans;
        };

        /**
         * @ngdoc method
         * @name setColumn
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The new selected column
         * @description Set the selected column and init its suggested transformations
         */
        this.setColumn = function setColumn(column) {
            if(column === self.currentColumn) {
                return;
            }

            self.currentColumn = column;
            self.transformations = null;
            TransformationCacheService.getTransformations(column)
                .then(function(transformations) {
                    if(self.currentColumn === column) {
                        self.transformations = filterAndGroup(transformations);
                    }
                });
        };

        /**
         * @ngdoc method
         * @name reset
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description Reset the current column and the transformations
         */
        this.reset = function reset() {
            self.currentColumn = null;
            self.transformations = null;
        };
    }

    angular.module('data-prep.services.transformation')
        .service('ColumnSuggestionService', ColumnSuggestionService);
})();