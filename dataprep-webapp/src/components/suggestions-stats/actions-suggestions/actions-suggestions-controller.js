(function() {
    'use strict';

    /**
     * @ngdoc controller
     * @name data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
     * @description Actions suggestion controller
     * @requires data-prep.services.transformation.service:SuggestionService
     * @requires data-prep.services.transformation.service:ColumnSuggestionService
     */
    function ActionsSuggestionsCtrl(state, SuggestionService, ColumnSuggestionService) {

        var vm = this;
        vm.columnSuggestionService = ColumnSuggestionService;
        vm.suggestionService = SuggestionService;
        vm.state = state;

        /**
         * @ngdoc method
         * @name shouldRenderAction
         * @methodOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @param {object} action The transformation to test
         * @description Determine if the transformation should be rendered.
         * The 'filtered' category transformations are not rendered if the applyTransformationOnFilters flag is false
         * @returns {boolean} True if the transformation should be rendered, False otherwise
         */
        vm.shouldRenderAction = function shouldRenderAction(action) {
            return state.playground.filter.applyTransformationOnFilters || (action.category !== 'filtered');
        };

        /**
         * @ngdoc method
         * @name shouldRenderCategory
         * @methodOf data-prep.actions-suggestions-stats.controller:ActionsSuggestionsCtrl
         * @param {object} categoryAction The categories with their transformations
         * @description Determine if the category should be rendered.
         * The 'suggestion' category is rendered if it has transformations to render
         * @returns {boolean} True if the category should be rendered, False otherwise
         */
        vm.shouldRenderCategory = function shouldRenderCategory(categoryAction) {
            return state.playground.filter.applyTransformationOnFilters ||            // display 'filtered' transformations (contained into 'suggestion' category)
                categoryAction.category !== 'suggestion' ||                           // not 'suggestion' category
                _.find(categoryAction.transformations, function(action) {             // 'suggestion' category: has transformations that is not a 'filtered' transformation
                    return action.category !== 'filtered';
                });
        };
    }

    angular.module('data-prep.actions-suggestions')
        .controller('ActionsSuggestionsCtrl', ActionsSuggestionsCtrl);
})();