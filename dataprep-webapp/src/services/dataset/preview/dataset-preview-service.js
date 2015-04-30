(function(){
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.dataset.service:DatasetPreviewService
     * @description Dataset preview service. This service holds the preview datagrid (SliickGrid) view and the (SlickGrid) filters
     */
    function DatasetPreviewService($q, DatasetGridService, PreparationService) {
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
            self.cancelPreview();

            previewCanceler = $q.defer();
            var records = getDisplayedRows();

            PreparationService.getPreviewAppend(records, actions, previewCanceler)
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

        self.getPreviewDisableRecords = function(currentStep, stepToDisable) {
            self.cancelPreview();

            previewCanceler = $q.defer();
            var recordsTdpId = _.map(getDisplayedRows(), function(element) {
                return element.tdpId;
            });

            PreparationService.getPreviewDisable(currentStep, stepToDisable, recordsTdpId, previewCanceler)
                .then(function(response) {
                    originalRecords = DatasetGridService.data.records;
                    modifiedRecords = originalRecords.slice(0);

                    _.forEach(recordsTdpId, function(tdpId, arrayIndex) {
                        modifiedRecords[tdpId] = response.data.records[arrayIndex];
                    });

                    DatasetGridService.updateRecords(modifiedRecords);
                })
                .finally(function() {
                    previewCanceler = null;
                });
        };

        self.getPreviewUpdateRecords = function(step, newParams, lastActiveStep) {
            self.cancelPreview();

            previewCanceler = $q.defer();
            var recordsTdpId = _.map(getDisplayedRows(), function(element) {
                return element.tdpId;
            });

            PreparationService.getPreviewUpdate(step, newParams, lastActiveStep, recordsTdpId, previewCanceler)
                .then(function(response) {
                    originalRecords = DatasetGridService.data.records;
                    modifiedRecords = originalRecords.slice(0);

                    _.forEach(recordsTdpId, function(tdpId, arrayIndex) {
                        modifiedRecords[tdpId] = response.data.records[arrayIndex];
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