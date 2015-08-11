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

        var service = {
            /**
             * @ngdoc property
             * @name gridRangeIndex
             * @propertyOf data-prep.services.playground.service:PreviewService
             * @description The grid displayed rows id. It take filters into account.
             * This is updated by the {@link data-prep.datagrid.directive:Datagrid Datagrid} directive on scroll
             */
            gridRangeIndex: [],

            getPreviewDiffRecords: getPreviewDiffRecords,
            getPreviewUpdateRecords: getPreviewUpdateRecords,
            cancelPreview: cancelPreview,
            stopPendingPreview: stopPendingPreview,
            previewInProgress: previewInProgress
        };
        return service;

        /**
         * @ngdoc method
         * @name getDisplayedRows
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] Get the actual displayed rows
         * @returns {object[]} The displayed rows
         */
        function getDisplayedRows() {
            var indexes = _.range(service.gridRangeIndex.top, service.gridRangeIndex.bottom + 1);
            return  _.chain(indexes)
                .map(DatagridService.dataView.getItem)
                .value();
        }

        /**
         * @ngdoc method
         * @name getDisplayedTdpIds
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] Get the rows TDP ids
         * @params {Array} records The records where to extract the TDP ids
         * @returns {object[]} The rows TDP ids
         */
        function getDisplayedTdpIds(records) {
           return _.map(records, function(element) {
               return element.tdpId;
           });
        }

        /**
         * @ngdoc method
         * @name getRecordsIndexes
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] Get the rows indexes in the data array
         * @params {Array} records The records where to extract the indexes
         * @returns {object[]} The rows indexes
         */
        function getRecordsIndexes(records) {
           return _.map(records, function(record) {
               return DatagridService.dataView.getIdxById(record.tdpId);
           });
        }

        /**
         * @ngdoc method
         * @name passfilters
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] Apply the current active filters from datagrid and return the result
         * @returns {boolean} The filter result
         */
        function passfilters(row) {
            return ! DatagridService.filters.length || DatagridService.getAllFiltersFn()(row);
        }

        /**
         * @ngdoc method
         * @name filterViableRecord
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description [PRIVATE] A viable row is a row that pass the active filters or that is flaged as NEW
         * @returns {object[]} The filtered rows
         */
        function filterViableRecord(records) {
            return _.filter(records, function(row) {
                return row.__tdpRowDiff !== 'new' || passfilters(row);
            });
        }

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
        function replaceRecords(displayedRows, colIdFromStep) {
            return function(response) {
                //save the original data
                originalData = originalData || DatagridService.data;
                modifiedRecords = originalData.records.slice(0);

                //filter if necessary
                var viableRecords = filterViableRecord(response.data.records);

                //insert records at the tdp ids insertion points
                var recordsIndexes = getRecordsIndexes(displayedRows);
                _.forEach(recordsIndexes, function(tdpId) {
                    modifiedRecords[tdpId] = viableRecords.shift();
                });

                //if all viable records are not already inserted, we insert them after the last targeted tdp id
                var insertionIndex = _.max(recordsIndexes) + 1;
                while(viableRecords.length) {
                    modifiedRecords[insertionIndex++] = viableRecords.shift();
                }

                //update grid
                var data = {columns: response.data.columns, records: modifiedRecords, preview: true};
                DatagridService.setFocusedColumn(colIdFromStep);
                DatagridService.updateData(data);
            };
        }

        /**
         * @ngdoc method
         * @name getPreviewDiffRecords
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description Call the diff preview service and replace records in the grid.
         * It cancel the previous preview first
         */
        function getPreviewDiffRecords(currentStep, previewStep, focusedColId) {
            cancelPreview(true, focusedColId);

            previewCanceler = $q.defer();
            var displayedRows = getDisplayedRows();
            displayedTdpIds = getDisplayedTdpIds(displayedRows);

            PreparationService.getPreviewDiff(currentStep, previewStep, displayedTdpIds, previewCanceler)
                .then(replaceRecords(displayedRows, focusedColId))
                .finally(function() {
                    previewCanceler = null;
                });
        }

        /**
         * @ngdoc method
         * @name getPreviewUpdateRecords
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description Call the update step preview service and replace records in the grid.
         * It cancel the previous preview first
         */
        function getPreviewUpdateRecords(currentStep, updateStep, newParams) {
            var focusedColId = currentStep.column.id;
            cancelPreview(true, focusedColId);

            previewCanceler = $q.defer();
            var displayedRows = getDisplayedRows();
            displayedTdpIds = getDisplayedTdpIds(displayedRows);

            PreparationService.getPreviewUpdate(currentStep, updateStep, newParams, displayedTdpIds, previewCanceler)
                .then(replaceRecords(displayedRows, focusedColId))
                .finally(function() {
                    previewCanceler = null;
                });
        }

        /**
         * @ngdoc method
         * @name cancelPreview
         * @param {boolean} partial If true, we cancel pending preview but we do NOT restore the original data
         * @param {string} focusedColId The column id where to set the grid focus
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description Cancel the current preview or the pending preview (resolving the cancel promise).
         * The original records is set back into the datagrid
         */
        function cancelPreview(partial, focusedColId) {

            stopPendingPreview();

            if(!partial && originalData) {
                DatagridService.setFocusedColumn(focusedColId);
                DatagridService.updateData(originalData);
                originalData = null;
                modifiedRecords = null;
                displayedTdpIds = null;
            }
        }

        /**
         * @ngdoc method
         * @name stopPendingPreview
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description Cancel the pending preview (resolving the cancel promise).
         */
        function stopPendingPreview() {
            if(previewCanceler) {
                previewCanceler.resolve('user cancel');
                previewCanceler = null;
            }
        }

        /**
         * @ngdoc method
         * @name previewInProgress
         * @methodOf data-prep.services.playground.service:PreviewService
         * @description Test if a preview is currently displayed
         */
        function previewInProgress() {
            return !!originalData;
        }
    }

    angular.module('data-prep.services.playground')
        .service('PreviewService', PreviewService);
})();