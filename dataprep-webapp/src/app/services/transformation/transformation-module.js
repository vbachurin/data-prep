import TransformationService from './transformation-service';
import ColumnSuggestionService from './suggestion/column-suggestion-service';
import LineSuggestionService from './suggestion/line-suggestion-service';
import SuggestionService from './suggestion/suggestion-service';
import TransformationRestService from './rest/transformation-rest-service';
import TransformationCacheService from './cache/transformation-cache-service';
import TransformationApplicationService from './application/transformation-application-service';

(() => {
    'use strict';

    /**
     * @ngdoc object
     * @name data-prep.services.transformation
     * @description This module contains the services to manipulate transformations
     * @requires data-prep.services.filter
     * @requires data-prep.services.playground
     * @requires data-prep.services.state
     * @requires data-prep.services.utils
     */
    angular.module('data-prep.services.transformation',
        [
            'data-prep.services.filter',
            'data-prep.services.playground',
            'data-prep.services.state',
            'data-prep.services.utils'
        ])
        .service('TransformationService', TransformationService)
        .service('ColumnSuggestionService', ColumnSuggestionService)
        .service('LineSuggestionService', LineSuggestionService)
        .service('SuggestionService', SuggestionService)
        .service('TransformationRestService', TransformationRestService)
        .service('TransformationCacheService', TransformationCacheService)
        .service('TransformationApplicationService', TransformationApplicationService);
})();