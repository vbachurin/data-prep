(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.filter-search.controller:FilterSearchCtrl
     * @description Filter search controller.
     */
    function FilterSearchCtrl(FilterService) {
        var vm = this;

        /**
         * @ngdoc method
         * @name createSuggestionItem
         * @methodOf data-prep.filter-search.controller:FilterSearchCtrl
         * @param {string} term - the filter phrase
         * @description [PRIVATE] Return a closure function that create a suggestion item from column id
         * @returns {function} the item creation closure
         */
        var createSuggestionItem = function(term) {
            return function(colId) {
                return {
                    label: term + ' in <b>' + colId + '</b>',
                    value: term,
                    columnId: colId
                };
            };
        };

        /**
         * @ngdoc method
         * @name filterSuggestion
         * @methodOf data-prep.filter-search.controller:FilterSearchCtrl
         * @param {string} term - the searched term
         * @description [PRIVATE] Create filter suggestions based on the typed term
         * @returns {function[]} the suggestion list
         */
        var filterSuggestion = function (term) {
            var cleanTerm = term.toLowerCase().trim();
            var colContainingTerm = FilterService.getColumnsContaining(cleanTerm);

            return _.chain(colContainingTerm)
                .sortBy(function(colId) {
                    return colId.toLowerCase();
                })
                .map(createSuggestionItem(cleanTerm))
                .value();
        };

        /**
         * @ngdoc method
         * @name suggestionSelect
         * @methodOf data-prep.filter-search.controller:FilterSearchCtrl
         * @param {object} item - the filter infos
         * @description [PRIVATE] Action when user select a suggestion : create the filter and reset the input
         */
        var suggestionSelect = function(item) {
            FilterService.addFilter('contains', item.columnId, {phrase: item.value});
            vm.filterSearch = '';
        };

        /**
         * @ngdoc property
         * @name filterSuggestOptions
         * @propertyOf data-prep.filter-search.controller:FilterSearchCtrl
         * @description Mass auto complete suggestions options and callbacks
         */
        vm.filterSuggestOptions = {
            suggest: filterSuggestion,
            /*jshint camelcase: false */
            on_select: suggestionSelect
        };
    }

    angular.module('data-prep.filter-search')
        .controller('FilterSearchCtrl', FilterSearchCtrl);
})();