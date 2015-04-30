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

        var getDisplayedRowsTdpIds = function() {
           return _.map(getDisplayedRows(), function(element) {
               return element.tdpId;
           });
        };

        var passfilters = function(row) {
            return ! DatasetGridService.filters.length || DatasetGridService.getAllFiltersFn()(row);
        };

        var filterViableRecord = function(records) {
            return _.filter(records, function(row) {
                return row.__tdpRowDiff !== 'new' || passfilters(row);
            });
        };

        var replaceRecords = function(recordsTdpId) {
            return function(response) {
                //save the original records
                originalRecords = DatasetGridService.data.records;
                modifiedRecords = originalRecords.slice(0);

                //filter if necessary
                var viableRecords = filterViableRecord(response.data.records);

                //insert records at the tdp ids insertion points
                _.forEach(recordsTdpId, function(tdpId) {
                    modifiedRecords[tdpId] = viableRecords.shift();
                });

                //if all viable records are not already inserted, we insert them after the last targeted tdp id
                var insertionIndex = recordsTdpId[recordsTdpId.length - 1] + 1;
                while(viableRecords.length) {
                    modifiedRecords[insertionIndex++] = viableRecords.shift();
                }

                //update grid
                DatasetGridService.updateRecords(modifiedRecords);
            };
        };

        self.getPreviewAppendRecords = function(actions) {
            self.cancelPreview();

            previewCanceler = $q.defer();
            var records = getDisplayedRows();
            var recordsTdpId = _.map(records, function(element) {
                return element.tdpId;
            });

            PreparationService.getPreviewAppend(records, actions, previewCanceler)
                .then(replaceRecords(recordsTdpId))
                .finally(function() {
                    previewCanceler = null;
                });
        };

        self.getPreviewDisableRecords = function(currentStep, stepToDisable) {
            self.cancelPreview();

            previewCanceler = $q.defer();
            var recordsTdpId = getDisplayedRowsTdpIds();

            PreparationService.getPreviewDisable(currentStep, stepToDisable, recordsTdpId, previewCanceler)
                .then(replaceRecords(recordsTdpId))
                .finally(function() {
                    previewCanceler = null;
                });
        };

        self.getPreviewUpdateRecords = function(step, newParams, lastActiveStep) {
            self.cancelPreview();

            previewCanceler = $q.defer();
            var recordsTdpId = getDisplayedRowsTdpIds();

            PreparationService.getPreviewUpdate(step, newParams, lastActiveStep, recordsTdpId, previewCanceler)
                .then(replaceRecords(recordsTdpId))
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