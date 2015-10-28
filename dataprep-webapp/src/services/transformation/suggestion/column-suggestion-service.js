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
            if(!self.searchActionString){
                self.filteredTransformations = _.cloneDeep(self.transformations);
            }
            self.filteredTransformations = {};
            var searchStringLowerCase = self.searchActionString.toLowerCase();
            _.forEach(self.transformations, function(transformations, category){
                self.filteredTransformations[category] = [];
                _.forEach(transformations, function(transformation){
                    if(transformation.labelHtml.toLowerCase().indexOf(searchStringLowerCase) !== -1 ||
                        transformation.description.toLowerCase().indexOf(searchStringLowerCase) !== -1 ||
                        transformation.categoryHtml.toLowerCase().indexOf(searchStringLowerCase) !== -1){
                        self.filteredTransformations[category].push(_.cloneDeep(transformation));
                    }
                });
            });
        };


        /**
         * @ngdoc method
         * @name updateTransformations
         * @methodOf data-prep.services.transformation.service:ColumnSuggestionService
         * @description update self.filteredTransformations keys(actions' category) when highlighting
         */
        this.updateTransformations = function updateTransformations() {
            //Remove old keys
            var transfos = _.flatten(_.values(self.filteredTransformations));
            //Update keys
            self.filteredTransformations = _.groupBy(transfos, function (action) {
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