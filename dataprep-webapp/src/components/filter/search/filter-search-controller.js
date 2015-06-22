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
            return function(col) {
                return {
                    label: term + ' in <b>' + col.name + '</b>',
                    value: term,
                    columnId: col.id,
                    columnName: col.name
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
                      .sortBy(function(col) {
                          return col.name.toLowerCase();
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
            FilterService.addFilter('contains', item.columnId, item.columnName, {phrase: item.value});
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