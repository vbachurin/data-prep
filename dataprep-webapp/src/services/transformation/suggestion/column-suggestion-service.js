(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:ColumnSuggestionService
     * @description Transformation Column suggestion service. This service provide the current column suggestions
     * @requires data-prep.services.transformation.service:TransformationCacheService
     */
    function ColumnSuggestionService($q, TransformationCacheService) {
        var COLUMN_CATEGORY = 'column_metadata';
        var FILTERED_CATEGORY = 'filtered';
        var SUGGESTION_CATEGORY = 'suggestion';
        var self = this;

        self.allTransformations = [];
        self.allSuggestions = [];
        self.searchActionString = '';
        self.filteredTransformations = null;

        //Sort object properties in alphabetical order
        function sortProperties(object) {
            var sortedObject = {};
            _.chain(Object.getOwnPropertyNames(object))
                .sort()
                .forEach(function (key) {
                    sortedObject[key] = object[key];
                })
                .value();

            return sortedObject;
        }

        //labelHtml is used to display actions list whereas label is used for preview
        function getLabelHtml(item) {
            return item.label + (item.parameters || item.dynamic ? '...' : '');
        }

        function setHtmlLabels(transformations) {
            _.forEach(transformations, function(transfo) {
                transfo.labelHtml = getLabelHtml(transfo);
                transfo.categoryHtml = transfo.category.toUpperCase();
            });
        }

        function isNotColumnCategory(category) {
            return function (item) {
                return item.category !== category;
            };
        }

        function isColumnCategory(category) {
            return function (item) {
                return item.category === category;
            };
        }

        /**
         * @ngdoc method
         * @name prepareTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {array} suggestions Suggested transformations list
         * @param {array} transformations All transformations list
         * @description Keep only the non 'column_metadata', non 'filtered' categories and group them by category, then add suggestions before
         * @returns {object} An object containing {key: value} = {category: [transformations]}
         */
        function prepareTransformations(suggestions, transformations) {
            var adaptedTransformations = _.chain(transformations)
                .filter(isNotColumnCategory(COLUMN_CATEGORY))
                .filter(isNotColumnCategory(FILTERED_CATEGORY))
                .sortBy(function (transfo) {
                    return transfo.label.toLowerCase();
                })
                .groupBy('categoryHtml')
                .value();

            var sortedTransformations = sortProperties(adaptedTransformations);
            var groupedSuggestions = {};
            if(suggestions.length) {
                groupedSuggestions[SUGGESTION_CATEGORY.toUpperCase()] = suggestions;
            }
            return _.assign({}, groupedSuggestions, sortedTransformations);
        }

        /**
         * @ngdoc method
         * @name prepareSuggestions
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {array} suggestions Suggested transformations list
         * @param {array} transformations All transformations list
         * @description Add 'filtered' category in suggestions
         * @returns {array} An array containing filtered transformations followed by suggestions
         */
        function prepareSuggestions(suggestions, transformations) {

            var adaptedFilteredTransformations = _.chain(transformations)
                .filter(isColumnCategory(FILTERED_CATEGORY))
                .sortBy(function (transfo) {
                    return transfo.label.toLowerCase();
                })
                .value();

            var adaptedSuggestions = _.chain(suggestions)
                .filter(isNotColumnCategory(COLUMN_CATEGORY))
                .value();

            return adaptedFilteredTransformations.concat(adaptedSuggestions);
        }

        /**
         * @ngdoc method
         * @name initTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @param {object} column The target column
         * @description Get and process the transformations from backend
         */
        this.initTransformations = function initTransformations(column) {
            this.reset();

            $q
                .all([
                    TransformationCacheService.getSuggestions(column),
                    TransformationCacheService.getTransformations(column)
                ])
                .then(function (values) {
                    setHtmlLabels(values[0]);
                    setHtmlLabels(values[1]);

                    var suggestions = prepareSuggestions(values[0], values[1]);
                    self.filteredTransformations = prepareTransformations(suggestions, values[1]);
                    self.allSuggestions = values[0];
                    self.allTransformations = values[1];
                });
        };

        //--------------------------------------------------------------------------------------------------------------
        //----------------------------------------------------FILTER----------------------------------------------------
        //--------------------------------------------------------------------------------------------------------------
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

            return !searchStringLowerCase ||
                transfo.labelHtml.toLowerCase().indexOf(searchStringLowerCase) !== -1 ||
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

        /**
         * @ngdoc method
         * @name filterTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description Filter transformations list by searchString
         */
        this.filterTransformations = function filterTransformations() {
            setHtmlLabels(self.allSuggestions);
            setHtmlLabels(self.allTransformations);

            var transformations = _.chain(self.allTransformations)
                .filter(matchSearch)
                .map(highlightText)
                .value();

            var suggestions = _.chain(self.allSuggestions)
                .filter(matchSearch)
                .map(highlightText)
                .value();

            var preparedSuggestions = prepareSuggestions(suggestions, transformations);
            self.filteredTransformations = prepareTransformations(preparedSuggestions, transformations);
        };

        /**
         * @ngdoc method
         * @name reset
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description Reset the current column and the transformations
         */
        this.reset = function reset() {
            self.allTransformations = [];
            self.allSuggestions = [];
            self.searchActionString = '';
            self.filteredTransformations = null;
        };
    }

    angular.module('data-prep.services.transformation')
        .service('ColumnSuggestionService', ColumnSuggestionService);
})();