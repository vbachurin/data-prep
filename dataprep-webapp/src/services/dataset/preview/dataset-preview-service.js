(function(){
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:DatasetPreviewService
     * @description Dataset preview service. This service holds the preview datagrid (SliickGrid) view and the (SlickGrid) filters
     */
    function DatasetPreviewService($q, DatasetGridService, TransformationService) {
        var self = this;

        //original
        var originalRecords;

        //preview
        var previewCanceler;
        var modifiedRecords;

        self.gridRangeIndex = [];

        var getDisplayedRows = function() {
            var indexes = _.range(self.gridRangeIndex.top, self.gridRangeIndex.bottom + 1);
            return  _.chain(indexes)
                .map(DatasetGridService.dataView.getItem)
                .filter(function(element) {
                    return element;
                })
                .value();
        };

        self.getPreviewAppendRecords = function(actions) {
            //TODO if same actions and same displayed records, display modifiedRecords without REST call

            previewCanceler = $q.defer();
            var records = getDisplayedRows();
            TransformationService.getPreviewAppend(records, actions, previewCanceler)
                .then(function(response) {
                    originalRecords = DatasetGridService.data.records;
                    modifiedRecords = originalRecords.slice(0);

                    _.forEach(records, function(originalElement, arrayIndex) {
                        var tdpIndex = originalElement.tdpId;
                        modifiedRecords[tdpIndex] = response.data.records[arrayIndex];
                    });

                    DatasetGridService.updateRecords(modifiedRecords);
                })
                .finally(function() {
                    previewCanceler = null;
                });
        };

        self.cancelPreview = function() {
            if(previewCanceler) {
                previewCanceler.resolve('user cancel');
                previewCanceler = null;
            }

            if(originalRecords) {
                DatasetGridService.updateRecords(originalRecords);
                originalRecords = null;
            }
        };

        self.previewInProgress = function() {
            return !!originalRecords;
        };
    }

    angular.module('data-prep.services.dataset')
        .service('DatasetPreviewService', DatasetPreviewService);
})();