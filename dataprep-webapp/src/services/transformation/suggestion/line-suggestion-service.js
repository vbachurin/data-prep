(function () {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.transformation.service:LineSuggestionService
     * @description Transformation Line suggestion service. This service provide the current line suggestions
     * @requires data-prep.services.transformation.service:TransformationCacheService
     * @requires data-prep.state.service:StateService
     */
    function LineSuggestionService(TransformationCacheService, StateService) {
        return {
            initTransformations: initTransformations
        };

        function initTransformations() {
            StateService.setSuggestionsLoading(true);
            TransformationCacheService.getLineTransformations()
                .then(function (lineTransformations) {
                    StateService.setLineTransformations({
                        allTransformations: lineTransformations.allTransformations,
                        allCategories: lineTransformations.allCategories,
                        filteredTransformations: lineTransformations.allCategories
                    });
                })
                .finally(function () {
                    StateService.setSuggestionsLoading(false);
                });
        }
    }

    angular.module('data-prep.services.transformation')
        .service('LineSuggestionService', LineSuggestionService);
})();