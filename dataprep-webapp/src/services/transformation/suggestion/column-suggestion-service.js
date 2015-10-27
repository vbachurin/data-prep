(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:ColumnSuggestionService
     * @description Transformation Column suggestion service. This service provide the current column suggestions
     * @requires data-prep.services.transformation.service:TransformationCacheService
     * @requires data-prep.services.utils.service:ConverterService
     */
    function ColumnSuggestionService($q, $translate, TransformationCacheService) {
        var COLUMN_CATEGORY = 'column_metadata';
        var self = this;

        self.allTransformations = [];

        /**
         * @ngdoc property
         * @name transformations
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The currently selected column transformations
         * @type {Object}
         */
        self.transformations = null;


        //Sort by object key in alphabetical order
        function sortObject(o) {
            var sorted = {},
                key, a = [];

            for (key in o) {
                if (o.hasOwnProperty(key)) {
                    a.push(key);
                }
            }

            a.sort();

            for (key = 0; key < a.length; key++) {
                sorted[a[key]] = o[a[key]];
            }
            return sorted;
        }

        /**
         * @ngdoc method
         * @name filterAndGroup
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object[]} allTransformationsArray All transformations list
         * @param {object[]} transformationsSuggestedArray Suggested transformations list
         * @description Keep only the non 'column_metadata' category and group them by category
         * @returns {object} An object containing {key: value} = {category: [transformations]}
         */
        function filterAndGroup(allTransformationsArray, transformationsSuggestedArray) {

            var allTransformations = allTransformationsArray;
            var transformationsSuggested = transformationsSuggestedArray;

            //labelHtml is used to display actions list whereas label is used for preview
            function addLabelHtmlBasedOnParameters(item) {
                if (!!(item.parameters || item.items) || item.dynamic) {
                    item.labelHtml = item.label + '...';
                } else {
                    item.labelHtml = item.label;
                }
            }

            //Process all transformations list
            angular.forEach(allTransformations, function (item) {
                addLabelHtmlBasedOnParameters(item);
                item.categoryHtml = item.category.toUpperCase();
            });
            var allTransfosFiltered = _.chain(allTransformations)
                .filter(function (transfo) {
                    return transfo.category !== COLUMN_CATEGORY;
                })
                .sortBy(function (transfo) {
                    return transfo.label.toLowerCase();
                })
                .value();
            var allTransfosFilteredGroupedSorted = sortObject(_.groupBy(allTransfosFiltered, function (action) {
                return action.categoryHtml;
            }));

            //Process suggested transformations list
            angular.forEach(transformationsSuggested, function (item) {
                addLabelHtmlBasedOnParameters(item);
                item.categoryHtml = $translate.instant('ACTION_SUGGESTION').toUpperCase();
            });
            var transfosSuggestedFiltered = _.chain(transformationsSuggested)
                .filter(function (transfo) {
                    return transfo.category !== COLUMN_CATEGORY;
                })
                .value();
            var transfosSuggestedFilteredGrouped = _.groupBy(transfosSuggestedFiltered, function (action) {
                return action.categoryHtml;
            });

            //Concatenate these two lists with respective order
            return _.extend(transfosSuggestedFilteredGrouped, allTransfosFilteredGroupedSorted);
        }

        /**
         * @ngdoc method
         * @name initTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The target column
         * @description Get and process the transformations from backend
         */
        this.initTransformations = function initTransformations(column) {
            self.transformations = null;

            $q
                .all([
                    TransformationCacheService.getTransformations(column, true)
                        .then(function (allTransformations) {
                            return allTransformations;
                        }),
                    TransformationCacheService.getTransformations(column, false)
                        .then(function (transformationsSuggested) {
                            return transformationsSuggested;
                        })
                ])
                .then(function (values) {
                    self.allTransformations = values[0];
                    self.transformations = filterAndGroup(values[0], values[1]);
                });
        };

        /**
         * @ngdoc method
         * @name updateTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description update self.transformations keys(actions' category) when highlighting
         */
        this.updateTransformations = function updateTransformations() {
            //Remove old keys
            var transfos = _.flatten(_.values(self.transformations));
            //Update keys
            self.transformations = _.groupBy(transfos, function (action) {
                return action.categoryHtml;
            });
        };

        /**
         * @ngdoc method
         * @name reset
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description Reset the current column and the transformations
         */
        this.reset = function reset() {
            self.transformations = null;
        };
    }

    angular.module('data-prep.services.transformation')
        .service('ColumnSuggestionService', ColumnSuggestionService);
})();