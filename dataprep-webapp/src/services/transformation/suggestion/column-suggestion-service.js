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

        /**
         * @ngdoc property
         * @name transformations
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The currently selected column transformations
         * @type {Object}
         */
        self.transformations = null;


        //Sort by object key
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
         * @param {object[]} allTransformations All transformations list
         * @param {object[]} transformationsSuggested Suggested transformations list
         * @description Keep only the non 'columns' category and group them by category
         * @returns {object} An object containing {key: value} = {category: [transformations]}
         */
        function filterAndGroup(allTransformations, transformationsSuggested) {

            //Add labelHtml which is copy of label in order to manage the highlight action label
            angular.forEach(allTransformations, function(item){
                item.labelHtml= item.label;
                item.categoryHtml= item.category.toUpperCase();
            });

            //Add labelHtml which is copy of label in order to manage the highlight action label
            angular.forEach(transformationsSuggested, function(item){
                item.labelHtml= item.label;
                item.categoryHtml= $translate.instant('ACTION_SUGGESTION').toUpperCase();
            });

            var allTransfosFiltered = _.chain(allTransformations)
                                    .filter(function (transfo) {
                                        return transfo.category !== COLUMN_CATEGORY;
                                    })
                                    .sortBy(function (transfo) {
                                        return transfo.label.toLowerCase();
                                    })
                                    .value();
            var allTransfosFilteredGroupedSorted = sortObject(_.groupBy(allTransfosFiltered, function(action){ return action.categoryHtml;}));


            var transfosSuggestedFiltered = _.chain(transformationsSuggested)
                                            .filter(function (transfo) {
                                                return transfo.category !== COLUMN_CATEGORY;
                                            })
                                            .value();

            var transfosSuggestedFilteredGrouped = _.groupBy(transfosSuggestedFiltered, function(action){ return action.categoryHtml;});

            return _.extend(transfosSuggestedFilteredGrouped, allTransfosFilteredGroupedSorted);
        }

        /**
         * @ngdoc method
         * @name initTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The target column
         * @description Get and preparation the transformations from backend
         */
        this.initTransformations = function initTransformations(column) {
            self.transformations = null;

            $q.all([
                TransformationCacheService.getTransformations(column, true)
                    .then(function (allTransformations) {
                        return allTransformations;
                    }),
                TransformationCacheService.getTransformations(column, false)
                    .then(function (transformationsSuggested) {
                        return transformationsSuggested;
                    })
            ])
            .then(function(values) {
                    self.transformations = filterAndGroup(values[0], values[1]);
            });
        };

        /**
         * @ngdoc method
         * @name updateTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description update self.transformations keys when highlighting
         */
        this.updateTransformations = function updateTransformations() {
            var transfos = _.flatten(_.values(self.transformations));
            self.transformations = _.groupBy(transfos, function(action){ return action.categoryHtml;});
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