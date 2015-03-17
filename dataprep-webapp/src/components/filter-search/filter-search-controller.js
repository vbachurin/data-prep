(function() {
    'use strict';
    
    function FilterSearchCtrl(DatasetGridService) {
        var vm = this;

        /**
         * Return a closure function that create a suggestion item from column id
         * @param term
         * @returns {Function}
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
         * Create filter suggestions based on the typed term
         * @param term - the searched term
         * @returns {*}
         */
        var filterSuggestion = function (term) {
            var colContainingTerm = DatasetGridService.getColumnsContaining(term);

            return _.chain(colContainingTerm)
                .sortBy(function(colId) {
                    return colId.toLowerCase();
                })
                .map(createSuggestionItem(term))
                .value();
        };

        /**
         * Action when user select a suggestion
         * @param item - the selected suggestion
         */
        var suggestionSelect = function(item) {
            console.log('create filter from item');
            console.log(item);
            vm.filterSearch = '';
        };

        /**
         * Mass auto complete suggestions
         * @type {{suggest: Function}}
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