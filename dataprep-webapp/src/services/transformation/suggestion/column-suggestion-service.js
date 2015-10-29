(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:ColumnSuggestionService
     * @description Transformation Column suggestion service. This service provide the current column suggestions
     * @requires data-prep.services.transformation.service:TransformationCacheService
     */
    function ColumnSuggestionService($q, $translate, TransformationCacheService) {
        var COLUMN_CATEGORY = 'column_metadata';
        var self = this;

        self.allTransformations = [];

        /**
         * @ngdoc property
         * @name searchActionString
         * @propertyOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @description Actions to search
         */
        self.searchActionString ='';

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
         * @name filteredTransformations
         * @propertyOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description The filtered transformations list
         * @type {Object}
         */
        self.filteredTransformations = null;

        //Sort object properties in alphabetical order
        function sortProperties(object) {
            var sortedObject = {};
            _.chain(Object.getOwnPropertyNames(object))
                .sort()
                .forEach(function(key) {
                    sortedObject[key] = object[key];
                })
                .value();

            return sortedObject;
        }

        /**
         * @ngdoc method
         * @name filterAndGroup
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {array} transformations All transformations list
         * @param {array} suggestions Suggested transformations list
         * @description Keep only the non 'column_metadata' category and group them by category
         * @returns {object} An object containing {key: value} = {category: [transformations]}
         */
        function filterAndGroup(transformations, suggestions) {

            //labelHtml is used to display actions list whereas label is used for preview
            function getLabelHtml(item) {
                return item.label + (item.parameters || item.dynamic ? '...' : '');
            }

            function isNotColumnCategory(item) {
                return item.category !== COLUMN_CATEGORY;
            }

            //Process transformations
            var adaptedTransformations = _.chain(transformations)
                .filter(isNotColumnCategory)
                .map(function(transfo) {
                    transfo.labelHtml = getLabelHtml(transfo);
                    transfo.categoryHtml = transfo.category.toUpperCase();
                    return transfo;
                })
                .sortBy(function (transfo) {
                    return transfo.label.toLowerCase();
                })
                .groupBy('categoryHtml')
                .value();
            adaptedTransformations = sortProperties(adaptedTransformations);

            //Process suggestions
            var suggestionCategory = $translate.instant('ACTION_SUGGESTION').toUpperCase();
            var adaptedSuggestions = _.chain(suggestions)
                .filter(isNotColumnCategory)
                .map(function(sugg) {
                    sugg.labelHtml = getLabelHtml(sugg);
                    sugg.categoryHtml = suggestionCategory;
                    return sugg;
                })
                .groupBy('categoryHtml')
                .value();

            //Concatenate these two lists with respective order
            return _.extend(adaptedSuggestions, adaptedTransformations);
        }

        /**
         * @ngdoc method
         * @name initTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The target column
         * @description Get and process the transformations from backend
         */
        this.initTransformations = function initTransformations(column) {
            self.allTransformations = null;
            self.transformations = null;
            self.filteredTransformations = null;
            self.searchActionString ='';

            $q
                .all([
                    TransformationCacheService.getTransformations(column),
                    TransformationCacheService.getSuggestions(column)
                ])
                .then(function (values) {
                    self.allTransformations = values[0];
                    self.transformations = filterAndGroup(values[0], values[1]);
                    self.filterTransformations ();
                });
        };

        /**
         * @ngdoc method
         * @name filterTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description Filter transformations list by searchString
         */
        this.filterTransformations = function filterTransformations() {

            /**
             * Escape regex expressions
             */
            function escapeRegex(text) {
                return text.replace(/[-[\]{}()*+?.,\\^$|#\s]/g, '\\$&');
            }

            /**
             * Filter transformations list to match search
             */
            function matchSearch(transfo) {
                var searchStringLowerCase = self.searchActionString.toLowerCase();

                //Remove highlight html code from label
                transfo.labelHtml = transfo.labelHtml.replace(new RegExp('(<span class="highlighted">)', 'g'), '');
                transfo.labelHtml = transfo.labelHtml.replace(new RegExp('(</span>)', 'g'), '');
                //Remove highlight html code from category
                transfo.categoryHtml = transfo.categoryHtml.replace(new RegExp('(<span class="highlighted">)', 'g'), '');
                transfo.categoryHtml = transfo.categoryHtml.replace(new RegExp('(</span>)', 'g'), '');

                if (!searchStringLowerCase) {
                    return true;
                }
                return transfo.labelHtml.toLowerCase().indexOf(searchStringLowerCase) !== -1 ||
                    transfo.description.toLowerCase().indexOf(searchStringLowerCase) !== -1 ||
                    transfo.categoryHtml.toLowerCase().indexOf(searchStringLowerCase) !== -1;
            }

            /**
             * Highlight filtered transformations
             */
            function highlightText(transfo) {
                var searchStringLowerCase = self.searchActionString.toLowerCase();

                if (searchStringLowerCase) {
                    if (transfo.labelHtml.toLowerCase().indexOf(searchStringLowerCase) !== -1) {
                        //Add html code to highlight searchActionString
                        transfo.labelHtml = transfo.labelHtml.replace(new RegExp('(' + escapeRegex(self.searchActionString) + ')', 'gi'),
                            '<span class="highlighted">$1</span>');
                    }
                    if (transfo.categoryHtml.toLowerCase().indexOf(searchStringLowerCase) !== -1) {
                        //Add html code to highlight searchActionString
                        transfo.categoryHtml = transfo.categoryHtml.replace(new RegExp('(' + escapeRegex(self.searchActionString) + ')', 'gi'),
                            '<span class="highlighted">$1</span>');
                    }
                }
                return transfo;
            }

            self.filteredTransformations = _.chain(_.flatten(_.values(self.transformations)))
                .filter(matchSearch)
                .map(highlightText)
                .value();

            //regenerate self.filteredTransformations keys(actions' category) after filtering and highlighting
            self.filteredTransformations = _.groupBy(self.filteredTransformations, function (action) {
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