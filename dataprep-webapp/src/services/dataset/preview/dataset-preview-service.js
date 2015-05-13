(function() {
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:DatasetPreviewService
     * @description TODO
     * @requires data-prep.services.dataset.service:DatasetRestService
     */
    function DatasetPreviewService($q, DatasetRestService) {
        var self = this;


        /**
         * @ngdoc property
         * @name dataset
         * @propertyOf data-prep.services.dataset.service:DatasetPreviewService
         * @description the dataset
         */
        self.dataset = null;

        /**
         * @ngdoc property
         * @name data
         * @propertyOf data-prep.services.dataset.service:DatasetPreviewService
         * @description the dataset data preview
         */
        self.data = null;

        self.init = function(dataset) {
            return DatasetRestService.getContent(dataset.id, true,true)
                .then(function(data) {
                          //$scope.dataset=dataset;
                          //$scope.data=data;
                      });
        };
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetPreviewService', DatasetPreviewService);
})();