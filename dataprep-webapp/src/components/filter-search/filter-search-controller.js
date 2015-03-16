(function() {
    'use strict';
    
    function FilterSearchCtrl(DatasetGridService) {
        var vm = this;

        /**
         * Return the column id list that contains requested term
         * @param term - the searched terme
         * @returns {Array}
         */
        var getColumnsContaining = function(term) {
            if (!term) {
                return [];
            }

            var results = [];
            var isNumeric = !isNaN(term);
            var data = DatasetGridService.data.records;
            var potentialColumns = DatasetGridService.getColumns(!isNumeric);

            //we loop over the datas while there is data and potential columns that can contains the searched term
            //if a col value for a row contains the term, we add it to result
            var dataIndex = 0;
            while (dataIndex < data.length && potentialColumns.length) {
                var record = data[dataIndex];
                for (var colIndex in potentialColumns) {
                    var colId = potentialColumns[colIndex];
                    if (record[colId].toLowerCase().indexOf(term) > -1) {
                        potentialColumns.splice(colIndex, 1);
                        results.push(colId);
                    }
                }

                potentialColumns = _.difference(potentialColumns, results);
                dataIndex++;
            }

            return results;
        };

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
            var lowerTerm = term.toLowerCase().trim();
            var colContainingTerm = getColumnsContaining(lowerTerm);

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