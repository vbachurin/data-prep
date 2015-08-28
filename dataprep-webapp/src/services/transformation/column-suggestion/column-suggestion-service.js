(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:ColumnSuggestionService
     * @description Transformation Column suggestion service. This service provide the current column suggestions
     * @requires data-prep.services.transformation.service:TransformationCacheService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function ColumnSuggestionService($rootScope, TransformationCacheService) {
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
         * @ngdoc property
         * @name statistics
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The currently selected column statistics
         * @type {Object}
         */
        self.statistics = null;


        /**
         * @ngdoc property
         * @name tab
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The currently selected tab
         * @type {Object}
         */
        self.tab = null;

        /**
         * @ngdoc method
         * @name filterAndGroup
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object[]} transfos The transformations list
         * @description Keep only the non 'columns' category and group them by category
         * @returns {object} An object containing {key: value} = {category: [transformations]}
         */
        function filterAndGroup(transfos) {
            return _.chain(transfos)
                .filter(function (transfo) {
                    return transfo.category !== COLUMN_CATEGORY;
                })
                .sortBy(function (action) {
                    return action.label.toLowerCase();
                })
                .value();
        }

        /**
         * @ngdoc method
         * @name initTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The target column
         * @description Get and preparation the transformations from backend
         */
        function initTransformations(column) {
            TransformationCacheService.getTransformations(column)
                .then(function (transformations) {
                    if (self.currentColumn === column) {
                        self.transformations = filterAndGroup(transformations);
                    }
                });
        }

        /**
         * @ngdoc method
         * @name setColumn
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The new selected column
         * @description Set the selected column and init its suggested transformations and statistics
         */
        this.setColumn = function setColumn(column) {
            if (column === self.currentColumn) {
                return;
            }

            self.currentColumn = column;
            self.transformations = null;
            initTransformations(column);
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


        /**
         * @ngdoc method
         * @name selectTab
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {String} tab The new selected tab title
         * @description Set the selected tab
         */
        this.selectTab = function setColumn(tab) {
            self.tab = tab;
            $rootScope.$digest();
        };
    }

    angular.module('data-prep.services.transformation')
        .service('ColumnSuggestionService', ColumnSuggestionService);
})();