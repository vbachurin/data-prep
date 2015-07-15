(function(){
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.playground.service:PreviewService
     * @description Preview service. This service holds the preview datagrid (SlickGrid) view
     * @requires data-prep.services.playground.service:DatagridService
     * @requires data-prep.services.preparation.service:PreparationService
     */
    function PreviewService($q, DatagridService, PreparationService) {
        var self = this;

        /**
         * @ngdoc property
         * @name originalData
         * @propertyOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] The original data (columns and records) before switching to preview
         */
        var originalData;

        /**
         * @ngdoc property
         * @name displayedTdpIds
         * @propertyOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] The list of records TDP indexes that is displayed in the viewport
         */
        var displayedTdpIds;

        /**
         * @ngdoc property
         * @name previewCanceler
         * @propertyOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] The preview cancel promise. When it is resolved, the pending request is rejected
         */
        var previewCanceler;
        /**
         * @ngdoc property
         * @name modifiedRecords
         * @propertyOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] The preview records that is displayed
         */
        var modifiedRecords;

        /**
         * @ngdoc property
         * @name gridRangeIndex
         * @propertyOf data-prep.services.playground.service:PreviewService
         * @description The grid displayed rows id. It take filters into account.
         * This is updated by the {@link data-prep.datagrid.directive:Datagrid Datagrid} directive on scroll
         */
        self.gridRangeIndex = [];

        /**
         * @ngdoc method
         * @name getDisplayedRows
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] Get the actual displayed rows
         * @returns {object[]} The displayed rows
         */
        var getDisplayedRows = function() {
            var indexes = _.range(self.gridRangeIndex.top, self.gridRangeIndex.bottom + 1);
            return  _.chain(indexes)
                .map(DatagridService.dataView.getItem)
                .filter(function(item) {
                    return item;
                })
                .value();
        };

        /**
         * @ngdoc method
         * @name getDisplayedRows
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] Get the displayed rows TDP ids
         * @returns {object[]} The displayed rows TDP ids
         */
        var getDisplayedTdpIds = function() {
           return _.map(getDisplayedRows(), function(element) {
               return element.tdpId;
           });
        };

        /**
         * @ngdoc method
         * @name passfilters
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] Apply the current active filters from datagrid and return the result
         * @returns {boolean} The filter result
         */
        var passfilters = function(row) {
            return ! DatagridService.filters.length || DatagridService.getAllFiltersFn()(row);
        };

        /**
         * @ngdoc method
         * @name filterViableRecord
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] A viable row is a row that pass the active filters or that is flaged as NEW
         * @returns {object[]} The filtered rows
         */
        var filterViableRecord = function(records) {
            return _.filter(records, function(row) {
                return row.__tdpRowDiff !== 'new' || passfilters(row);
            });
        };

        /**
         * @ngdoc method
         * @name replaceRecords
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] Create a closure that take an http response and execute :
         * <ul>
         *     <li>Save the original records</li>
         *     <li>Copy the row list</li>
         *     <li>Insert the viable preview rows into the new list</li>
         *     <li>Display the new records</li>
         * </ul>
         * @returns {function} The request callback closure
         */
        var replaceRecords = function replaceRecords (recordsTdpId, stepColumn) {
            return function(response) {
                //save the original data
                originalData = originalData || DatagridService.data;
                modifiedRecords = originalData.records.slice(0);

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
                var data = {columns: response.data.columns, records: modifiedRecords, preview: true};
                DatagridService.updateData(data, stepColumn);
            };
        };

        /**
         * @ngdoc method
         * @name getPreviewDiffRecords
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description Call the diff preview service and replace records in the grid.
         * It cancel the previous preview first
         */
        self.getPreviewDiffRecords = function(currentStep, previewStep, stepColumnId) {
            self.cancelPreview(true, stepColumnId);

            previewCanceler = $q.defer();
            displayedTdpIds = getDisplayedTdpIds();

            PreparationService.getPreviewDiff(currentStep, previewStep, displayedTdpIds, previewCanceler)
                .then(replaceRecords(displayedTdpIds, stepColumnId))
                .finally(function() {
                    previewCanceler = null;
                });
        };

        /**
         * @ngdoc method
         * @name getPreviewUpdateRecords
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description Call the update step preview service and replace records in the grid.
         * It cancel the previous preview first
         */
        self.getPreviewUpdateRecords = function(currentStep, updateStep, newParams) {
            var stepColumnId = currentStep.column.id;
            self.cancelPreview(true, stepColumnId);

            previewCanceler = $q.defer();
            displayedTdpIds = getDisplayedTdpIds();

            PreparationService.getPreviewUpdate(currentStep, updateStep, newParams, displayedTdpIds, previewCanceler)
                .then(replaceRecords(displayedTdpIds, stepColumnId))
                .finally(function() {
                    previewCanceler = null;
                });
        };

        /**
         * @ngdoc method
         * @name cancelPreview
         * @param {boolean} partial If true, we cancel pending preview but we do NOT restore the original data
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description Cancel the current preview or the pending preview (resolving the cancel promise).
         * The original records is set back into the datagrid
         */
        self.cancelPreview = function(partial, stepColumnId) {

            if(previewCanceler) {
                previewCanceler.resolve('user cancel');
                previewCanceler = null;
            }

            if(!partial && originalData) {
                DatagridService.updateData(originalData, stepColumnId);
                originalData = null;
                modifiedRecords = null;
                displayedTdpIds = null;
            }
        };

        /**
         * @ngdoc method
         * @name previewInProgress
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description Test if a preview is currently displayed
         */
        self.previewInProgress = function() {
            return !!originalData;
        };
    }

    angular.module('data-prep.services.playground')
        .service('PreviewService', PreviewService);
})();