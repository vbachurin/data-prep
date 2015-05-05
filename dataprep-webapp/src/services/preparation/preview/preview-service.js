(function(){
    'use strict';

    /**
     * @ngdoc service
     * @name data-prep.services.preparation.service:PreviewService
     * @description Preview service. This service holds the preview datagrid (SlickGrid) view
     * @requires data-prep.services.dataset.service:DatasetGridService
     * @requires data-prep.services.preparation.service:PreviewService
     */
    function PreviewService($q, DatasetGridService, PreparationService) {
        var self = this;

        /**
         * @ngdoc property
         * @name originalRecords
         * @propertyOf data-prep.services.preparation.service:PreviewService
         * @description [PRIVATE] The original records before switching to preview
         */
        var originalRecords;
        /**
         * @ngdoc property
         * @name displayedTdpIds
         * @propertyOf data-prep.services.preparation.service:PreviewService
         * @description [PRIVATE] The list of records TDP indexes that is displayed in the viewport
         */
        var displayedTdpIds;

        /**
         * @ngdoc property
         * @name previewCanceler
         * @propertyOf data-prep.services.preparation.service:PreviewService
         * @description [PRIVATE] The preview cancel promise. When it is resolved, the pending request is rejected
         */
        var previewCanceler;
        /**
         * @ngdoc property
         * @name modifiedRecords
         * @propertyOf data-prep.services.preparation.service:PreviewService
         * @description [PRIVATE] The preview records that is displayed
         */
        var modifiedRecords;

        /**
         * @ngdoc property
         * @name gridRangeIndex
         * @propertyOf data-prep.services.preparation.service:PreviewService
         * @description The grid displayed rows id. It take filters into account.
         * This is updated by the {@link data-prep.datagrid.directive:Datagrid Datagrid} directive on scroll
         */
        self.gridRangeIndex = [];

        /**
         * @ngdoc method
         * @name getDisplayedRows
         * @methodOf data-prep.services.preparation.service:PreviewService
         * @description [PRIVATE] Get the actual displayed rows
         * @returns {object[]} The displayed rows
         */
        var getDisplayedRows = function() {
            var indexes = _.range(self.gridRangeIndex.top, self.gridRangeIndex.bottom + 1);
            return  _.chain(indexes)
                .map(DatasetGridService.dataView.getItem)
                .value();
        };

        /**
         * @ngdoc method
         * @name getDisplayedRows
         * @methodOf data-prep.services.preparation.service:PreviewService
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
         * @methodOf data-prep.services.preparation.service:PreviewService
         * @description [PRIVATE] Apply the current active filters from datagrid and return the result
         * @returns {boolean} The filter result
         */
        var passfilters = function(row) {
            return ! DatasetGridService.filters.length || DatasetGridService.getAllFiltersFn()(row);
        };

        /**
         * @ngdoc method
         * @name filterViableRecord
         * @methodOf data-prep.services.preparation.service:PreviewService
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
         * @methodOf data-prep.services.preparation.service:PreviewService
         * @description [PRIVATE] Create a closure that take an http response and execute :
         * <ul>
         *     <li>Save the original records</li>
         *     <li>Copy the row list</li>
         *     <li>Insert the viable preview rows into the new list</li>
         *     <li>Display the new records</li>
         * </ul>
         * @returns {function} The request callback closure
         */
        var replaceRecords = function(recordsTdpId) {
            return function(response) {
                //save the original records
                originalRecords = originalRecords || DatasetGridService.data.records;
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

        /**
         * @ngdoc method
         * @name getPreviewDiffRecords
         * @methodOf data-prep.services.preparation.service:PreviewService
         * @description Call the diff preview service and replace records in the grid.
         * It cancel the previous preview first
         */
        self.getPreviewDiffRecords = function(currentStep, previewStep) {
            self.cancelPreview();

            previewCanceler = $q.defer();
            displayedTdpIds = getDisplayedTdpIds();

            PreparationService.getPreviewDiff(currentStep, previewStep, displayedTdpIds, previewCanceler)
                .then(replaceRecords(displayedTdpIds))
                .finally(function() {
                    previewCanceler = null;
                });
        };

        /**
         * @ngdoc method
         * @name getPreviewUpdateRecords
         * @methodOf data-prep.services.preparation.service:PreviewService
         * @description Call the update step preview service and replace records in the grid.
         * It cancel the previous preview first
         */
        self.getPreviewUpdateRecords = function(currentStep, updateStep, newParams) {
            self.cancelPreview();

            previewCanceler = $q.defer();
            displayedTdpIds = getDisplayedTdpIds();

            PreparationService.getPreviewUpdate(currentStep, updateStep, newParams, displayedTdpIds, previewCanceler)
                .then(replaceRecords(displayedTdpIds))
                .finally(function() {
                    previewCanceler = null;
                });
        };

        /**
         * @ngdoc method
         * @name cancelPreview
         * @methodOf data-prep.services.preparation.service:PreviewService
         * @description Cancel the current preview or the pending preview (resolving the cancel promise).
         * The original records is set back into the datagrid
         */
        self.cancelPreview = function() {
            if(previewCanceler) {
                previewCanceler.resolve('user cancel');
                previewCanceler = null;
            }

            if(originalRecords) {
                DatasetGridService.updateRecords(originalRecords);
                originalRecords = null;
                modifiedRecords = null;
                displayedTdpIds = null;
            }
        };

        /**
         * @ngdoc method
         * @name previewInProgress
         * @methodOf data-prep.services.preparation.service:PreviewService
         * @description Test if a preview is currently displayed
         */
        self.previewInProgress = function() {
            return !!originalRecords;
        };
    }

    angular.module('data-prep.services.preparation')
        .service('PreviewService', PreviewService);
})();